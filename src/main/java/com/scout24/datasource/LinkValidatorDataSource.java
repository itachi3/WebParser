package com.scout24.datasource;

import com.scout24.models.ApiResponse;
import com.scout24.models.HyperLinks;
import com.scout24.models.ParseResult;
import com.scout24.utils.URLParser;
import com.scout24.utils.Utils;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LinkValidatorDataSource implements URLParser, Runnable {

    private HyperLinks hyperLinks;
    private final static Logger log = Logger.getLogger(LinkValidatorDataSource.class);
    private String url;
    private List<ParseResult> result = new CopyOnWriteArrayList<>(new ArrayList<ParseResult>());

    public LinkValidatorDataSource(HyperLinks hyperLinks) {
        this.hyperLinks = hyperLinks;
    }

    private LinkValidatorDataSource(String url, List<ParseResult> result) {
        this.url = url;
        this.result = result;
    }

    /*
        Currently takes in 15 as maximum URL's for search
        Threads are allocated based on number of processors
        Each thread works on an URL and checks, if it is secure/redirects
     */
    public ApiResponse fetch() {
        if (Utils.isEmptyorNull(hyperLinks.getLinks())) {
            return new ApiResponse(result, "No link received");
        } else if (hyperLinks.getLinks().size() > 15) {
            return new ApiResponse("Limit exceeded, allowed is 15 URL's");
        }

        int threads = Runtime.getRuntime().availableProcessors();
        log.info("Number of threads : " + threads);

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (String link : hyperLinks.getLinks()) {
            executorService.execute(new LinkValidatorDataSource(link, result));
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Executor service abrupt shutdown : ", e);
        }

        return new ApiResponse(result);
    }

    /*
        Runnable implementation, used by ExecutorService for each URL
        Validates the security of the URL & Checks for redirection
        Result is added concurrent list
     */
    public void run() {
        ParseResult urlResult = validateHttps(url);
        urlResult.setOriginalUrl(url);
        try {
            if (Utils.isEmptyorNull(urlResult.getReason())) {
                urlResult.setRedirects(redirects(url));
            }
        } catch (Exception e) {
            urlResult.setReason(e.getMessage());
            log.error("Exception while redirecting " + e.getMessage() + " " + url);
        }
        result.add(urlResult);
    }

    /*
        Validates the HTTPs URL using HEAD method
        HEAD - is used to avoid downloading all the data
        Minimal timeout is set to facilitate more URL search
        And avoid blocking on one.
     */
    private ParseResult validateHttps(String URLName) {
        URLName = URLName.replace("http:", "https:");
        ParseResult result = new ParseResult();
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL(URLName).openConnection();
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            con.setRequestProperty("User-Agent", URLParser.UserAgent);
            con.setRequestProperty("REFERER", URLParser.Referrer);
            con.setRequestMethod("HEAD");
            int code = con.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                result.setSecure(true);
            } else {
                result.setStatusCode(code);
                result.setReason(con.getResponseMessage());
            }
        } catch (Exception e) {
            log.error("Exception while fetching " + e.getMessage() + " " + url);
            result.setReason(e.getMessage());
        }
        return result;
    }

}
