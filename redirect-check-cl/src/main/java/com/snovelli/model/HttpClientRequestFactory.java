package com.snovelli.model;



import snove.seo.redirectcheck.domain.HttpRequest;

import java.net.URI;

public interface HttpClientRequestFactory {
    HttpRequest getConnector(URI httpURI);
}
