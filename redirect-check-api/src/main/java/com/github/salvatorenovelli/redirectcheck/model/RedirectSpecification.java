package com.github.salvatorenovelli.redirectcheck.model;

public final class RedirectSpecification {

    private final String sourceURI;
    private final String expectedDestination;
    private final int expectedStatusCode;
    private final int lineNumber;
    private final String errorMessage;


    private RedirectSpecification(int lineNumber, String sourceURI, String expectedDestination, int expectedStatusCode, String errorMessage) {
        this.lineNumber = lineNumber;
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
        this.expectedStatusCode = expectedStatusCode;
        this.errorMessage = errorMessage;
    }

    public static RedirectSpecification createValid(int lineNumber, String sourceURI, String expectedDestination, int expectedStatusCode) {
        return new RedirectSpecification(lineNumber, sourceURI, expectedDestination, expectedStatusCode, null);
    }

    public static RedirectSpecification createInvalid(int lineNumber, String errorMessage) {
        assertHasLength(errorMessage);
        return new RedirectSpecification(lineNumber, "n/a", "n/a", -1, errorMessage);
    }

    private static void assertHasLength(String text) {
        if (text.length() < 1) {
            throw new IllegalArgumentException("[Assertion failed] - this String argument must have length; it must not be null or empty");
        }
    }

    public String getSourceURI() {
        return sourceURI;
    }

    public String getExpectedDestination() {
        return expectedDestination;
    }

    public int getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "RedirectSpecification{" +
                "sourceURI='" + sourceURI + '\'' +
                ", expectedDestination='" + expectedDestination + '\'' +
                ", expectedStatusCode=" + expectedStatusCode +
                ", lineNumber=" + lineNumber +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public boolean isValid() {
        return errorMessage == null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
