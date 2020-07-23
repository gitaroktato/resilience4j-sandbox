package com.epam.example;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class RateLimitTest {

    @Test
    public void testRateLimiting() throws InterruptedException {
        var config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMillis(100))
                .limitForPeriod(10)
                .timeoutDuration(Duration.ofMillis(10000))
                .build();

        // Create registry
        var rateLimiterRegistry = RateLimiterRegistry.of(config);

        // Use registry
        var rateLimiter = rateLimiterRegistry
                    .rateLimiter("name1");

        var limitedCall = RateLimiter.decorateCallable(rateLimiter, () -> {
            Thread.sleep(25);
            return "OK";
        });
        for (int i=0; i < 100; i++) {
            new Thread(() -> {
                Try.run(() -> System.out.println(limitedCall.call()))
                        .onFailure((throwable) -> System.out.println(throwable.toString()));
            }).start();
        }
        Thread.sleep(10000);
    }
}
