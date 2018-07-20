package com.github.salvatorenovelli.redirectcheck.model;

import lombok.EqualsAndHashCode;

import java.net.URISyntaxException;
import java.util.List;

@EqualsAndHashCode
public class RedirectCheckResponse {


    public static final String DESTINATION_MISMATCH = "Destination doesn't match";
    public static final String STATUS_CODE_MISMATCH = "HTTP Status is not ";
    private final Status status;
    private final String statusMessage;
    private final String sourceURI;
    private final String expectedDestinationURI;
    private final int requestLineNumber;
    private String actualDestinationURI;
    private int lastHttpStatus = -1;
    private boolean isCleanRedirect = false;

    private List<RedirectChainElement> redirectChain;

    private int numberOfRedirects;


    private RedirectCheckResponse(RedirectSpecification invalidRequest) {
        assertTrue(!invalidRequest.isValid(), "This constructor should be used only for invalid spec requests.");
        status = Status.FAILURE;
        statusMessage = invalidRequest.getErrorMessage();
        requestLineNumber = invalidRequest.getLineNumber();
        sourceURI = "n/a";
        expectedDestinationURI = "n/a";
    }

    private RedirectCheckResponse(RedirectSpecification request, RedirectChain redirectChain) {

        assertTrue(request.isValid(), "This constructor should be used only for valid spec requests.");
        this.requestLineNumber = request.getLineNumber();
        this.sourceURI = request.getSourceURI();
        this.redirectChain = redirectChain.getElements();
        this.expectedDestinationURI = request.getExpectedDestination();

        isCleanRedirect = redirectChain.getElements().stream().filter(redirectChainElement -> redirectChainElement.getHttpStatus() != 301).count() == 1;

        if (redirectChain.isFailed()) {
            status = Status.FAILURE;
            statusMessage = redirectChain.getStatus();
            return;
        }

        this.actualDestinationURI = redirectChain.getDestinationURI();
        this.lastHttpStatus = redirectChain.getLastHttpStatus();
        this.numberOfRedirects = redirectChain.getNumOfRedirect();

        boolean compare = false;
        try {
            compare = EscapedUriComparator.compare(request.getExpectedDestination(), actualDestinationURI);
        } catch (URISyntaxException e) {
            //Will be failing on the next lines
        }

        if (!compare) {
            status = Status.FAILURE;
            statusMessage = DESTINATION_MISMATCH;
            return;
        }

        if (lastHttpStatus != request.getExpectedStatusCode()) {
            status = Status.FAILURE;
            statusMessage = STATUS_CODE_MISMATCH + request.getExpectedStatusCode();
            return;
        }


        status = Status.SUCCESS;
        statusMessage = "";
    }

    public static RedirectCheckResponse createResponse(RedirectSpecification request, RedirectChain redirectChain) {
        return new RedirectCheckResponse(request, redirectChain);
    }

    public static RedirectCheckResponse createResponseForInvalidSpec(RedirectSpecification request) {
        return new RedirectCheckResponse(request);
    }

    private void assertTrue(boolean valid, String s) {
        if (!valid) {
            throw new IllegalArgumentException(s);
        }
    }

    public String getActualDestinationURI() {
        return actualDestinationURI;
    }

    public int getNumberOfRedirects() {
        return numberOfRedirects;
    }

    public int getLastHttpStatus() {
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

    public int getRequestLineNumber() {
        return requestLineNumber;
    }

    public boolean isCleanRedirect() {
        return isCleanRedirect;
    }

    public enum Status {
        SUCCESS, FAILURE
    }
}
