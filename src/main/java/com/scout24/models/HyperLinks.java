package com.scout24.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scout24.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HyperLinks {
    int count;

    List<String> links = new ArrayList<>();

    public HyperLinks() {

    }

    public HyperLinks(List<String> links) {
        this.links = links;
        if (!Utils.isEmptyorNull(links)) {
            this.count = links.size();
        } else {
            this.count = 0;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }
}
