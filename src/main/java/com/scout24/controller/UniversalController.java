package com.scout24.controller;

import com.scout24.datasource.DownloadDataSource;
import com.scout24.datasource.LinkValidatorDataSource;
import com.scout24.models.ApiResponse;
import com.scout24.models.HyperLinks;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * DATA STRUCTURE & DESIGN CHOICES
 * -----------------------------
 * REQUIREMENT:
 * -----------
 * (1) Given the URL fetch the HTML, parse and display the basic details
 * (2) The input data file - config.yml
 * <p>
 * GOAL:
 * -----
 * - The fetch query should return the response as soon as possible.
 * - Given multiple HyperLinks, check for redirection, SSL secure, etc
 * <p>
 * TRADE-OFFs:
 * ----------
 * (1) Minimal timeout for fetch, to avoid user blocked on one URL.
 * - Possibility of not getting result for an valid URL.
 * (2) Some websites might load HTML in fragments. For those only the first returned HTML is considered for parsing
 * - Since, We will never know, how long we need to wait for the entire page to be loaded or is there more ajax.
 * <p>
 * Single URL
 * -----------
 * - Calculate the results from a HTML in parallel / sequential.
 * - Fetch Time Complexity O(Most expensive attribute) / O(Sum of all attributes)
 * <p>
 * - First approach works, when, extensive analysis is required on a HTML.
 * Second is being used in this implementation
 * <p>
 * Multiple URL
 * ------------
 * - Process each URL in parallel, with minimal timeout
 * - Concurrent List is used intentionally to avoid object level lock in Synchronized list
 * - Executor service is preferred, as it can be extended for future enhancements
 * - Limitation of 15 URL's at the moment
 */

@Path("/v1.0")
public class UniversalController {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response availability() {
        return Response.status(Response.Status.OK).entity("Welcome to scout24").build();
    }

    @GET
    @Path("/fetch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchURL(@QueryParam("url") String url) {
        DownloadDataSource ds = new DownloadDataSource(url);
        ApiResponse result = ds.fetch();
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("/suggestion")
    @Produces(MediaType.APPLICATION_JSON)
    public Response suggestion() {
        return Response.status(Response.Status.OK).entity(DownloadDataSource.getSuggestions()).build();
    }

    @POST
    @Path("/parse")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processHyperLinks(HyperLinks hyperLinks) {
        LinkValidatorDataSource linkDs = new LinkValidatorDataSource(hyperLinks);
        return Response.status(Response.Status.OK).entity(linkDs.fetch()).build();
    }

}
