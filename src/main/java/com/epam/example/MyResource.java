package com.epam.example;

import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class MyResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Metered(name = "hello")
    public String hello() {
        return "hello";
    }
}