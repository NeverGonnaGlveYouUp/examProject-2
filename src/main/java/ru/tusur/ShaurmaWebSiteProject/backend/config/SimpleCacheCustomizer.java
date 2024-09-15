package ru.tusur.ShaurmaWebSiteProject.backend.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
public class SimpleCacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    public static final String PRODUCTS = "products";
    public static final String PRODUCT = "product";
    public static final String PROMOTIONS = "promotions";

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(asList(PRODUCTS, PRODUCT, PROMOTIONS));
    }
}
