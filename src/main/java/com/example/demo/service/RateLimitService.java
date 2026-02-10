package com.example.demo.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimitService {

    Map<String, Bucket> buckets = new HashMap<>();

//    public Bucket getBucket(){
//
//    }

}
