package com.snovelli.model;

import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;

public final class RedirectCheckResponse {


    private final Status status;
    private final String statusMessage;
    private final String sourceURI;
    private final String expectedDestinationURI;
    private String actualDestinationURI;
    private HttpStatus lastHttpStatus;

    private List<RedirectChainElement> redirectChain;

    private int numberOfRedirects;

    public RedirectCheckResponse(RedirectSpecification request, RedirectChain redirectChain) {


        this.sourceURI = request.getSourceURI();
        this.redirectChain = redirectChain.getElements();
        this.expectedDestinationURI = request.getExpectedDestination();


        if (redirectChain.isFailed()) {
            status = Status.FAILURE;
            statusMessage = redirectChain.getStatus();
            return;
        }


        this.actualDestinationURI = redirectChain.getDestinationURI();
        this.lastHttpStatus = redirectChain.getLastStatus();
        this.numberOfRedirects = redirectChain.getNumOfRedirect();


        if (!request.getExpectedDestination().equals(actualDestinationURI)) {
            status = Status.FAILURE;
            statusMessage = "Destination doesn't match";
            return;
        }

        if (lastHttpStatus != HttpStatus.OK) {
            status = Status.FAILURE;
            statusMessage = "HTTP Status is not 200 (OK)";
            return;
        }


        status = Status.SUCCESS;
        statusMessage = "";


    }


    public String getActualDestinationURI() {
        return actualDestinationURI;
    }

    public int getNumberOfRedirects() {
        return numberOfRedirects;
    }


    public HttpStatus getLastHttpStatus() {
        return lastHttpStatus;
    }

    public Status getStatus() {
        return status;
    }

    public List<RedirectChainElement> getRedirectChain() {
        return redirectChain;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getExpectedDestinationURI() {
        return expectedDestinationURI;
    }

    @Override
    public String toString() {
        return "RedirectCheckResponse{" +
                "status=" + status +
                ", statusMessage='" + statusMessage + "'" +
                '}';
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public enum Status {
        SUCCESS, FAILURE
    }
}
