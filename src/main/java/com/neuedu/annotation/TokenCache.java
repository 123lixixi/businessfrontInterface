package com.neuedu.annotation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 基于Guava Cache缓存类
 * */
    public class TokenCache{
        private static LoadingCache<String,String> localCache= CacheBuilder.newBuilder()
                .initialCapacity(1000)
                .maximumSize(10000)
                .expireAfterAccess(12, TimeUnit.HOURS)
                .build(new CacheLoader<String,String>(){
                    @Override
                    public String load(String s)throws Exception{
                        return "null";
                    }
                });
        public static void set(String key,String value){
            localCache.put(key,value);
        }
        public static String get(String key){
            String value=null;
            try{
                value=localCache.get(key);
                if("null".equals(value)){
                    return null;
                }
                return value;
            }catch (ExecutionException e){
                e.printStackTrace();
            }
            return null;
        }
    }
