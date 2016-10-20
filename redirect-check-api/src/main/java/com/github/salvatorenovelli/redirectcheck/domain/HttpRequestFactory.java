package com.github.salvatorenovelli.redirectcheck.domain;



import java.net.URI;


public interface HttpRequestFactory {
    HttpRequest createRequest(URI httpURI);
}
