package com.snovelli.model;

import com.snovelli.http.HttpRequest;

import java.net.URI;

public interface HttpClientRequestFactory {
    HttpRequest getConnector(URI httpURI);
}
