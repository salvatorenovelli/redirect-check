package com.github.salvatorenovelli.redirectcheck.model;



import java.net.URI;


public final class RedirectChainElement {
    private final int httpStatus;
    private final URI destinationURI;


     public RedirectChainElement(int httpStatus, URI destinationURI) {
        this.httpStatus = httpStatus;
        this.destinationURI = destinationURI;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public URI getDestinationURI() {
        return destinationURI;
    }
}

