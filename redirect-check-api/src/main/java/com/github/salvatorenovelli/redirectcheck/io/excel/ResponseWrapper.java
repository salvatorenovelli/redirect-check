package com.github.salvatorenovelli.redirectcheck.io.excel;

import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;

class ResponseWrapper {

    final String result;
    final String reason;
    final String expectedURI;
    final String actualURI;
    final String lastHTTPStatus;
    final String sourceURI;
    final int lineNumber;
    final boolean isCleanRedirect;

    ResponseWrapper(RedirectCheckResponse cr) {
        this(cr.getRequestLineNumber(), cr.getSourceURI(),
                cr.getStatus().toString(),
                cr.getStatusMessage(),
                cr.getExpectedDestinationURI(),
                cr.getActualDestinationURI() != null ? cr.getActualDestinationURI() : "n/a",
                cr.getLastHttpStatus() != -1 ? "" + cr.getLastHttpStatus() : "n/a",
                cr.isCleanRedirect());
    }

    ResponseWrapper(RedirectSpecification specification) {
        this(specification.getLineNumber(),
                specification.getSourceURI(),
                RedirectCheckResponse.Status.FAILURE.toString(),
                specification.getErrorMessage(),
                specification.getExpectedDestination(),
                "n/a", "n/a", false);
    }

    private ResponseWrapper(int lineNumber, String sourceURI, String result, String reason, String expectedURI, String actualURI, String lastHTTPStatus, boolean isCleanRedirect) {
        this.lineNumber = lineNumber;
        this.sourceURI = sourceURI;
        this.result = result;
        this.reason = reason;
        this.expectedURI = expectedURI;
        this.actualURI = actualURI;
        this.lastHTTPStatus = lastHTTPStatus;
        this.isCleanRedirect = isCleanRedirect;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
