package com.snovelli.model;

import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URL;

public final class RedirectChainElement {
    private final HttpStatus status;
    private final URI destinationURI;


    public RedirectChainElement(int httpStatus, URI destinationURI) {
        this(HttpStatus.valueOf(httpStatus), destinationURI);
    }

    public RedirectChainElement(HttpStatus status, URI destinationURI) {
        this.status = status;
        this.destinationURI = destinationURI;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public URI getDestinationURI() {
        return destinationURI;
    }
}

