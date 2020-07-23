package com.epam.example;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/hello")
public class MyResource {

    private RateLimiterConfig rateLimiterConfig;
    private RateLimiterRegistry rateLimiterRegistry;
    @Inject
    MetricRegistry metricRegistry;

    @PostConstruct
    public void ConfigureRateLimits() {
        rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(167)
                .timeoutDuration(Duration.ofMillis(5000))
                .build();

        rateLimiterRegistry = RateLimiterRegistry.of(rateLimiterConfig);

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@HeaderParam("application") String application) throws Exception {
        // Getting the meter for the specific application
        Meter meter = getMeterForApplication(application);
        meter.mark();
        //
        var rateLimiter = getRateLimiterForApplication(application);
        var limitedCall = RateLimiter.decorateCallable(rateLimiter, () -> {
            return "hello";
        });
        return limitedCall.call();
    }

    private RateLimiter getRateLimiterForApplication(@HeaderParam("application") String application) {
        if (application == null || application.isEmpty() || application.isBlank()) {
            application = "NULL";
        }
        return rateLimiterRegistry.rateLimiter(application);
    }

    private Meter getMeterForApplication(String application) {
        if (application == null || application.isEmpty() || application.isBlank()) {
            application = "NULL";
        }
        var tag = new Tag("application", application);
        return metricRegistry.meter("hello", tag);
    }
}