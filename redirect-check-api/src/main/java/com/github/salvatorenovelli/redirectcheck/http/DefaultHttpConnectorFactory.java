package com.github.salvatorenovelli.redirectcheck.http;


import com.github.salvatorenovelli.redirectcheck.domain.HttpRequest;
import com.github.salvatorenovelli.redirectcheck.domain.HttpRequestFactory;

import java.net.URI;


public class DefaultHttpConnectorFactory implements HttpRequestFactory {

    @Override
    public HttpRequest createRequest(URI httpURI) {
        return new HttpGetRequest(httpURI, new DefaultConnectionFactory());
    }
}
