package com.snovelli.http;


import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import snove.seo.redirectcheck.domain.HttpRequest;
import snove.seo.redirectcheck.domain.HttpRequestFactory;

import java.net.URI;


@Component
public class DefaultHttpConnectorFactory implements HttpRequestFactory {

    @Override
    public HttpRequest createRequest(URI httpURI) {
        return new DefaultHttpRequest(httpURI, HttpMethod.GET);
    }
}
