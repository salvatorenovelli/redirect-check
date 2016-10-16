package com.snovelli.model;

import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class RedirectChain {

    private static final String REDIRECT_LOOP = "Redirect loop";
    private final List<RedirectChainElement> elements;
    private boolean isFailed = false;
    private String status = "";

    public RedirectChain() {
        elements = new ArrayList<>();
    }

    public List<RedirectChainElement> getElements() {
        return new ArrayList<>(elements);
    }

    public int getNumOfRedirect() {
        return elements.size() - 1;
    }

    private RedirectChainElement getLastElement() {
        return elements.get(elements.size() - 1);
    }

    public boolean addElement(RedirectChainElement redirectChainElement) {
        return elements.add(redirectChainElement);
    }

    public void markAsRedirectLoop() {
        markAsFailed(REDIRECT_LOOP);
    }

    public void markAsFailed(String reason) {
        this.status = "Failed: " + reason;
        this.isFailed = true;
    }

    public String getDestinationURI() {
        return getLastElement().getDestinationURI().toASCIIString();
    }

    public HttpStatus getLastStatus() {
        return getLastElement().getStatus();
    }

    public boolean isFailed() {
        return isFailed;
    }

    public String getStatus() {
        return status;
    }
}
