package com.snovelli.model;

public final class RedirectSpecification {

    private final String sourceURI;
    private final String expectedDestination;


    public RedirectSpecification(String sourceURI, String expectedDestination) {
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public String getExpectedDestination() {
        return expectedDestination;
    }


    @Override
    public String toString() {
        return "RedirectSpecification{" +
                "sourceURI=" + sourceURI +
                ", expectedDestination=" + expectedDestination +
                '}';
    }
}
