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
    final List<Integer> redirectChain;
    final boolean isDestinationMatch;
    final boolean isHttpStatusCodeMatch;
    final boolean isPermanentRedirect;

    ResponseWrapper(RedirectCheckResponse cr) {
        this(cr.getRequestLineNumber(), cr.getSourceURI(),
                cr.getStatus().toString(),
                cr.getStatusMessage(),
                cr.isDestinationMatch(), cr.isStatusCodeMatch(), cr.isPermanentRedirect(),
                cr.getExpectedDestinationURI(),
                cr.getActualDestinationURI() != null ? cr.getActualDestinationURI() : "n/a",
                cr.getLastHttpStatus() != -1 ? "" + cr.getLastHttpStatus() : "n/a",
                cr.getHttpStatusChain());
    }

    ResponseWrapper(RedirectSpecification specification) {
        this(specification.getLineNumber(), specification.getSourceURI(),
                RedirectCheckResponse.Status.FAILURE.toString(), specification.getErrorMessage(),
                false, false, false,
                specification.getExpectedDestination(),
                "n/a", "n/a", Collections.emptyList());
    }

    private ResponseWrapper(int lineNumber, String sourceURI, String result, String reason,
                            boolean isDestinationMatch, boolean isHttpStatusCodeMatch, boolean isPermanentRedirect,
                            String expectedURI, String actualURI, String lastHTTPStatus, List<Integer> redirectChain) {
        this.lineNumber = lineNumber;
        this.sourceURI = sourceURI;
        this.result = result;
        this.reason = reason;
        this.isDestinationMatch = isDestinationMatch;
        this.isHttpStatusCodeMatch = isHttpStatusCodeMatch;
        this.isPermanentRedirect = isPermanentRedirect;
        this.expectedURI = expectedURI;
        this.actualURI = actualURI;
        this.lastHTTPStatus = lastHTTPStatus;
        this.redirectChain = redirectChain;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
