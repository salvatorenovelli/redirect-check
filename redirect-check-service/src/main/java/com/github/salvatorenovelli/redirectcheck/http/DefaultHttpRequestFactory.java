package com.github.salvatorenovelli.redirectcheck.http;


import com.github.salvatorenovelli.redirectcheck.domain.HttpRequest;

import org.springframework.stereotype.Component;

import java.net.URI;

import com.github.salvatorenovelli.redirectcheck.domain.HttpRequestFactory;


@Component
public class DefaultHttpRequestFactory implements HttpRequestFactory {
    @Override
    public HttpRequest createRequest(URI httpURI) {
        return new HttpGetRequest(httpURI);
    }
}
