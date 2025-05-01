package com.example.helloworldretroclient;

import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.IdlingResource.ResourceCallback;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple implementation of IdlingResource that can be manually incremented and decremented
 * during tests. This is useful for handling asynchronous operations like network calls.
 */
public class EspressoTestingIdlingResource {
    private final String resourceName = "GLOBAL";
    private final AtomicInteger counter = new AtomicInteger(0);
    private volatile ResourceCallback resourceCallback;

    /**
     * Returns the IdlingResource instance
     */
    public IdlingResource getIdlingResource() {
        return new IdlingResource() {
            @Override
            public String getName() {
                return resourceName;
            }

            @Override
            public boolean isIdleNow() {
                return counter.get() == 0;
            }

            @Override
            public void registerIdleTransitionCallback(ResourceCallback callback) {
                resourceCallback = callback;
            }
        };
    }

    /**
     * Increments the counter when an asynchronous operation begins
     */
    public void increment() {
        counter.incrementAndGet();
    }

    /**
     * Decrements the counter when an asynchronous operation completes
     */
    public void decrement() {
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0 && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        
        if (counterVal < 0) {
            throw new IllegalStateException("Counter has been decremented below 0");
        }
    }
} 