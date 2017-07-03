package com.scout24.datasource;

import com.google.common.net.InternetDomainName;
import com.scout24.models.ApiResponse;
import com.scout24.models.HyperLinks;
import com.scout24.models.Result;
import com.scout24.utils.CacheUtils;
import com.scout24.utils.URLParser;
import com.scout24.utils.Utils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class DownloadDataSource implements URLParser {
    private final static Logger log = Logger.getLogger(DownloadDataSource.class);
    private static CacheUtils cache = CacheUtils.getInstance();
    private String path;

    private Result result;
    private Document doc;

    public DownloadDataSource(String path) {
        this.path = path;
        result = new Result();
        //For suggesting the URL
        new Thread(this::addSuggestion).start();
    }

    public static Set<String> getSuggestions() {
        return cache.getValue(CacheUtils.urls);
    }

    /*
        Checks whether URL redirects to another
        Fetches the final URL - html and calculates
        Title, Login, DocumentType, HyperLinks, etc
     */
    public ApiResponse fetch() {
        if (Utils.isEmptyorNull(path)) {
            return new ApiResponse(result);
        }
        result.setOriginalUrl(path);
        Connection.Response response;
        try {
            result.setRedirects(redirects(path));
            response = Jsoup.connect(path).timeout(3000)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .userAgent(UserAgent)
                    .referrer(Referrer)
                    .execute();
            if (response.statusCode() != HttpStatus.OK_200) {
                return new ApiResponse(response.statusMessage());
            }
            doc = response.parse();
        } catch (Exception e) {
            log.error("Exception while fetching URL ", e);
            return new ApiResponse("Kindly check the URL !! Error - " + e.getMessage());
        }
        String title = doc.title();
        result.setFinalUrl(response.url().toString());
        result.setTitle(title);

        isLoginForm();
        documentType();
        headingTags();
        hyperLinks();
        return new ApiResponse(result);
    }

    //Cache to support auto suggest, while, typing the URL
    private void addSuggestion() {
        Set<String> suggestions = cache.getValue(CacheUtils.urls);
        suggestions.add(path);
        cache.addElement(CacheUtils.urls, new HashSet<>(suggestions));
    }

    private void documentType() {
        List<Node> nodes = doc.childNodes();
        String docType = nodes
                .stream()
                .filter(node -> node instanceof DocumentType)
                .map(this::generateHtmlVersion)
                .collect(Collectors.joining());
        result.setDocumentType(docType);
    }

    //HTML5 is return by default, as it is predominant now
    private String generateHtmlVersion(Node node) {
        DocumentType documentType = (DocumentType) node;
        String htmlVersion = documentType.attr("publicid");
        return "".equals(htmlVersion) ? "HTML5" : htmlVersion;
    }

    private void headingTags() {
        Elements hTags = doc.select("h1, h2, h3, h4, h5, h6");
        List<Result.Headings> headings = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            String tag = "h" + i;
            Elements hIndividualTags = hTags.select(tag);
            if (!Utils.isEmptyorNull(hIndividualTags)) {
                headings.add(new Result.Headings(hIndividualTags.size(), "Heading " + tag));
            }
        }
        result.setHeadings(headings);
    }

    /*
        All hyper link domain names are compared with input
        If same internal otherwise external
        InternetDomainName is used to get only valid domains
     */
    private void hyperLinks() {
        String host;
        try {
            host = new URI(path).getHost();
            InternetDomainName domainName = InternetDomainName.from(host);
            host = domainName.topPrivateDomain().toString();
        } catch (URISyntaxException e) {
            log.error("Invalid URI " + path);
            return;
        }

        Elements elts = doc.select("a");
        List<String> internal = new ArrayList<>(), external = new ArrayList<>();
        for (Element elt : elts) {
            String link = elt.attr("abs:href");
            if (Utils.isEmptyorNull(link)) {
                continue;
            }
            if (link.contains(host)) {
                internal.add(link);
            } else {
                external.add(link);
            }
        }

        HyperLinks internalLink = new HyperLinks(internal);
        HyperLinks externalLink = new HyperLinks(external);
        Map<String, HyperLinks> hyperLinkMap = new HashMap<>();
        hyperLinkMap.put("internal", internalLink);
        hyperLinkMap.put("external", externalLink);
        result.setHyperLinks(hyperLinkMap);
    }

    /*
        Checking whether HTML has login form
         1. Contains password type
         2. Has Forgot password text
         3. More than 2 password type, could be a signup form
         Enhancements
         1. Check for social login buttons or image
         2. Contains username text
     */
    private void isLoginForm() {
        Elements elts = doc.select("input[type=password]");
        if (!Utils.isEmptyorNull(elts)) {
            if (elts.size() > 1) {
                result.setSignUpForm(true);
            }
            result.setLoginForm(true);
        }
        if (!result.isLoginForm()) {
            elts = doc.select("div:contains(forgot password)");
            if (!Utils.isEmptyorNull(elts)) {
                result.setLoginForm(true);
            }
        }
    }

    public Result getResult() {
        return result;
    }
}