package com.movienow.org.uitls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Slf4j
public class CacheUtils {

    @Autowired
    private RedisCacheManager cacheManager;

    /**
     * Used to update Cache List
     * @param cacheName
     * @param key
     * @param classType
     * @param value
     * @param <T>
     */
    public <T> void updateCacheList(String cacheName, Long key, Class<T> classType, T value) {
        Cache cache = cacheManager.getCache(cacheName);
        List<T> movieDetailsResponses = new ArrayList<>();
        if (cache != null) {
            Object cachedValue = cache.get(key);
            try {
                cachedValue = ((SimpleValueWrapper) cachedValue).get();
                if (cachedValue instanceof List) {
                    movieDetailsResponses.addAll((List<T>) cachedValue);
                    movieDetailsResponses.add(value);
                    System.out.println(movieDetailsResponses);
                    cache.put(key, movieDetailsResponses);
                } else {
                    log.error("CACHE ERROR::existing cache is not a list");
                }
            } catch (Exception e) {
                log.error("CACHE ERROR::unable to update cache: " + e.getMessage());
            }
        } else {
            movieDetailsResponses.add(value);
            cache.put(key, value);
        }
    }
}
