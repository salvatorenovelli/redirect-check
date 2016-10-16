package com.snovelli.seo.redirectcheck.testutils;

import com.snovelli.http.HttpResponse;
import com.snovelli.model.HttpClientRequestFactory;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class MockHttpClientRequestFactoryBuilder {


    private final Map<URI, HttpResponse> responses = new HashMap<>();

    public static MockHttpClientRequestFactoryBuilder mockRequestFactory() {
        return new MockHttpClientRequestFactoryBuilder();
    }


    public MockHttpClientRequestFactoryBuilder withRedirect(String source, HttpStatus status, String dstLocation) throws URISyntaxException {
        responses.put(new URI(source), new HttpResponse(status, new URI(dstLocation)));
        return this;
    }

    public MockHttpClientRequestFactoryBuilder withOk(String source) throws URISyntaxException {
        responses.put(new URI(source), new HttpResponse(HttpStatus.OK, new URI(source)));
        return this;
    }


    public HttpClientRequestFactory build() {
        return httpURI -> () -> responses.get(httpURI);
    }

}
