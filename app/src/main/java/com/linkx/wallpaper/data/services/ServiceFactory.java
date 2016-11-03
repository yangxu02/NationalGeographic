package com.linkx.wallpaper.data.services;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by ulyx.yang@yeahmobi.com on 2016/11/1.
 */
public class ServiceFactory {
    public static <T> T createServiceFrom(final Class<T> serviceClass, String endpoint, Converter.Factory factory) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(endpoint)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        if (null != factory) {
            builder.addConverterFactory(factory);
        }
        return builder.build().create(serviceClass);
    }

    public static <T> T createServiceFrom(final Class<T> serviceClass, String endpoint) {
        return createServiceFrom(serviceClass, endpoint, JacksonConverterFactory.create());
    }




}
