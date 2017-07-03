package com.scout24.models;

/**
 * Created by G on 02/07/17.
 */
public class ParseResult {

    boolean secure;

    String reason;

    boolean redirects;

    int statusCode;

    String originalUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isRedirects() {
        return redirects;
    }

    public void setRedirects(boolean redirects) {
        this.redirects = redirects;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "secure=" + secure +
                ", reason='" + reason + '\'' +
                ", redirects=" + redirects +
                ", statusCode=" + statusCode +
                ", originalUrl='" + originalUrl + '\'' +
                '}';
    }
}
