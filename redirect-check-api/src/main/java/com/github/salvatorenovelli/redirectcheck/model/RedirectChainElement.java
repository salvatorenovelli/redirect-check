package com.github.salvatorenovelli.redirectcheck.model;


import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;


public final class RedirectChainElement {
    private final int httpStatus;
    private final URI destinationURI;


    public RedirectChainElement(int httpStatus, URI destinationURI) {
        this.httpStatus = httpStatus;

        try {
            String decode = URLDecoder.decode(destinationURI.toASCIIString(), "UTF-8");
            this.destinationURI = URI.create(decode);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


    }


    public int getHttpStatus() {
        return httpStatus;
    }

    public URI getDestinationURI() {
        return destinationURI;
    }
}

