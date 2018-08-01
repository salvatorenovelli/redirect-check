package com.github.salvatorenovelli.redirectcheck.model;

import lombok.EqualsAndHashCode;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class RedirectCheckResponse {


    static final String DESTINATION_MISMATCH = "Destination mismatch";
    static final String STATUS_CODE_MISMATCH = "Status is not ";
    static final String NON_PERMANENT_REDIRECT = "Non permanent redirect";

    private final String sourceURI;
    private final String expectedDestinationURI;
    private String actualDestinationURI;

    private final int requestLineNumber;
    private final List<RedirectChainElement> redirectChain;

    private Status status = Status.SUCCESS;
    private String statusMessage = "";

    private int lastHttpStatus = -1;
    private int numberOfRedirects;


    private RedirectCheckResponse(RedirectSpecification invalidRequest) {
        assertTrue(!invalidRequest.isValid(), "This constructor should be used only for invalid spec requests.");
        status = Status.FAILURE;
        statusMessage = invalidRequest.getErrorMessage();
        requestLineNumber = invalidRequest.getLineNumber();
        sourceURI = "n/a";
        expectedDestinationURI = "n/a";
        redirectChain = Collections.emptyList();
    }

    private RedirectCheckResponse(RedirectSpecification request, RedirectChain redirectChain) {

        assertTrue(request.isValid(), "This constructor should be used only for valid spec requests.");
        this.requestLineNumber = request.getLineNumber();
        this.sourceURI = request.getSourceURI();
        this.redirectChain = redirectChain.getElements();
        this.expectedDestinationURI = request.getExpectedDestination();

        if (redirectChain.isFailed()) {
            status = Status.FAILURE;
            statusMessage = addStatusMessage(redirectChain.getStatus());
            return;
        }

        this.actualDestinationURI = redirectChain.getDestinationURI();
        this.lastHttpStatus = redirectChain.getLastHttpStatus();
        this.numberOfRedirects = redirectChain.getNumOfRedirect();

        verifyDestinationMismatch(request);
        verifyStatusCodeMismatch(request);
        verifyDirtyRedirect();

    }

    private void verifyDirtyRedirect() {
        if (!isCleanRedirect()) {
            status = Status.FAILURE;
            statusMessage = addStatusMessage(NON_PERMANENT_REDIRECT);
        }
    }

    private void verifyStatusCodeMismatch(RedirectSpecification request) {
        if (lastHttpStatus != request.getExpectedStatusCode()) {
            status = Status.FAILURE;
            statusMessage = addStatusMessage(STATUS_CODE_MISMATCH + request.getExpectedStatusCode());
        }
    }

    private void verifyDestinationMismatch(RedirectSpecification request) {
        boolean compare = false;
        try {
            compare = EscapedUriComparator.compare(request.getExpectedDestination(), actualDestinationURI);
        } catch (URISyntaxException e) {
            //Will be failing on the next lines
        }

        if (!compare) {
            status = Status.FAILURE;
            statusMessage = addStatusMessage(DESTINATION_MISMATCH);
        }
    }

    private String addStatusMessage(String status) {
        if (this.statusMessage.isEmpty()) return status;
        return this.statusMessage + ", " + status;
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
        return redirectChain.stream().filter(redirectChainElement -> redirectChainElement.getHttpStatus() != 301).count() == 1;
    }

    public List<Integer> getHttpStatusChain() {
        return redirectChain.stream().map(RedirectChainElement::getHttpStatus).collect(Collectors.toList());
    }

    public enum Status {
        SUCCESS, FAILURE
    }
}
