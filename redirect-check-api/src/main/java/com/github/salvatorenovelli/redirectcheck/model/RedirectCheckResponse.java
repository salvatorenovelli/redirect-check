package com.github.salvatorenovelli.redirectcheck.model;

import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.salvatorenovelli.redirectcheck.model.EscapedUriComparator.compare;
import static com.github.salvatorenovelli.redirectcheck.model.Verification.of;

@EqualsAndHashCode
public class RedirectCheckResponse {

    static final String DESTINATION_MISMATCH = "Destination mismatch";
    static final String STATUS_CODE_MISMATCH = "Status is not ";
    static final String NON_PERMANENT_REDIRECT = "Non permanent redirect";

    private boolean generalFailure = false;
    private boolean statusCodeMatch = true;
    private boolean destinationMatch = true;

    private final String sourceURI;
    private final String expectedDestinationURI;
    private String actualDestinationURI;

    private final int requestLineNumber;
    private final List<RedirectChainElement> redirectChain;

    private Status status = Status.SUCCESS;
    private String statusMessage = "";

    private int lastHttpStatus = -1;
    private int numberOfRedirects;


    public static RedirectCheckResponse createResponse(RedirectSpecification request, RedirectChain redirectChain) {
        return new RedirectCheckResponse(request, redirectChain);
    }

    public static RedirectCheckResponse createResponseForError(String errorMessage, RedirectSpecification request) {
        return new RedirectCheckResponse(errorMessage, request);
    }


    private RedirectCheckResponse(String errorMessage, RedirectSpecification request) {
        status = Status.FAILURE;
        statusMessage = errorMessage;
        requestLineNumber = request.getLineNumber();
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

        destinationMatch = of(() -> compare(actualDestinationURI, request.getExpectedDestination())).onException(exceptionMessage -> this.statusMessage = addStatusMessage(exceptionMessage));
        statusCodeMatch = lastHttpStatus == request.getExpectedStatusCode();

        status = destinationMatch && statusCodeMatch && isPermanentRedirect() ? Status.SUCCESS : Status.FAILURE;

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
                addFlagsMessage() +
                '}';
    }

    private String addFlagsMessage() {
        if (redirectChain.size() > 0) {
            return (!destinationMatch ? ", DestinationMismatch" : "") +
                    (!statusCodeMatch ? ", StatusCodeMismatch" : "") +
                    (!isPermanentRedirect() ? ", NonPermanentRedirect" : "");
        } else {
            return "";
        }
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public int getRequestLineNumber() {
        return requestLineNumber;
    }

    public boolean isPermanentRedirect() {
        return redirectChain.stream().filter(redirectChainElement -> redirectChainElement.getHttpStatus() != 301).count() == 1;
    }

    public List<Integer> getHttpStatusChain() {
        return redirectChain.stream().map(RedirectChainElement::getHttpStatus).collect(Collectors.toList());
    }

    private String addStatusMessage(String status) {
        if (this.statusMessage.isEmpty()) return status;
        return this.statusMessage + ", " + status;
    }

    private void assertTrue(boolean valid, String s) {
        if (!valid) {
            throw new IllegalArgumentException(s);
        }
    }

    public boolean isDestinationMatch() {
        return destinationMatch;
    }

    public boolean isStatusCodeMatch() {
        return statusCodeMatch;
    }


    public enum Status {
        SUCCESS, FAILURE
    }

}
