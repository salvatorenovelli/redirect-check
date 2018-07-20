package com.github.salvatorenovelli.redirectcheck.io.excel;

import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;

import java.util.Collections;
import java.util.List;

class ResponseWrapper {

    final String result;
    final String reason;
    final String expectedURI;
    final String actualURI;
    final String lastHTTPStatus;
    final String sourceURI;
    final int lineNumber;
    final boolean isCleanRedirect;
    final List<Integer> redirectChain;

    ResponseWrapper(RedirectCheckResponse cr) {
        this(cr.getRequestLineNumber(), cr.getSourceURI(),
                cr.getStatus().toString(),
                cr.getStatusMessage(),
                cr.getExpectedDestinationURI(),
                cr.getActualDestinationURI() != null ? cr.getActualDestinationURI() : "n/a",
                cr.getLastHttpStatus() != -1 ? "" + cr.getLastHttpStatus() : "n/a",
                cr.isCleanRedirect(), cr.getHttpStatusChain());
    }

    ResponseWrapper(RedirectSpecification specification) {
        this(specification.getLineNumber(),
                specification.getSourceURI(),
                RedirectCheckResponse.Status.FAILURE.toString(),
                specification.getErrorMessage(),
                specification.getExpectedDestination(),
                "n/a", "n/a", false, Collections.emptyList());
    }

    private ResponseWrapper(int lineNumber, String sourceURI, String result, String reason, String expectedURI,
                            String actualURI, String lastHTTPStatus, boolean isCleanRedirect, List<Integer> redirectChain) {
        this.lineNumber = lineNumber;
        this.sourceURI = sourceURI;
        this.result = result;
        this.reason = reason;
        this.expectedURI = expectedURI;
        this.actualURI = actualURI;
        this.lastHTTPStatus = lastHTTPStatus;
        this.isCleanRedirect = isCleanRedirect;
        this.redirectChain = redirectChain;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
