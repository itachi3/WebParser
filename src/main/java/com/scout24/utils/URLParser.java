package com.scout24.utils;

import com.scout24.models.ApiResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public interface URLParser {
    String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    String Referrer = "http://www.google.com";

    default boolean redirects(String path) throws IOException {
        Connection.Response response = Jsoup.connect(path).timeout(3000)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .followRedirects(false)
                .userAgent(UserAgent)
                .referrer(Referrer)
                .execute();
        if (response.statusCode() == HttpStatus.PERMANENT_REDIRECT_308 ||
                response.statusCode() == HttpStatus.TEMPORARY_REDIRECT_307) {
            return true;
        } else if (response.hasHeader("location")) {
            return true;
        }
        return false;
    }

    ApiResponse fetch();
}
