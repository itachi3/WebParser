package com.scout24.models;

import java.util.List;
import java.util.Map;

public class Result {

    public static class Headings {
        int count;

        String name;

        public Headings(int count, String name) {
            this.count = count;
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private String originalUrl;

    private String finalUrl;

    private String title;

    private boolean redirects;

    private boolean loginForm;

    private boolean signUpForm;

    private String documentType;

    private List<Headings> headings;

    private Map<String, HyperLinks> hyperLinks;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getFinalUrl() {
        return finalUrl;
    }

    public void setFinalUrl(String finalUrl) {
        this.finalUrl = finalUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRedirects() {
        return redirects;
    }

    public void setRedirects(boolean redirects) {
        this.redirects = redirects;
    }

    public boolean isLoginForm() {
        return loginForm;
    }

    public void setLoginForm(boolean loginForm) {
        this.loginForm = loginForm;
    }

    public boolean isSignUpForm() {
        return signUpForm;
    }

    public void setSignUpForm(boolean signUpForm) {
        this.signUpForm = signUpForm;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public List<Headings> getHeadings() {
        return headings;
    }

    public void setHeadings(List<Headings> headings) {
        this.headings = headings;
    }

    public Map<String, HyperLinks> getHyperLinks() {
        return hyperLinks;
    }

    public void setHyperLinks(Map<String, HyperLinks> hyperLinks) {
        this.hyperLinks = hyperLinks;
    }
}
