package com.epam.example;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/hello")
public class MyResource {

    private RateLimiterConfig rateLimiterConfig;
    private RateLimiterRegistry rateLimiterRegistry;

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
    @Metered(name = "hello")
    public String hello() throws Exception {
        // TODO variable from header
        var rateLimiter = rateLimiterRegistry
                .rateLimiter("dio-styx");
        var limitedCall = RateLimiter.decorateCallable(rateLimiter, () -> {
            return "hello";
        });
        return limitedCall.call();
    }
}