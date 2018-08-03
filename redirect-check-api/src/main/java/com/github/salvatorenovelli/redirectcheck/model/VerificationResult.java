package com.github.salvatorenovelli.redirectcheck.model;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

class VerificationResult {

    private final boolean success;
    private final String errorMessage;

    static VerificationResult failure(String message) {
        return new VerificationResult(false, message);
    }

    static VerificationMapperBuilder forEach(VerificationResult... destinationMatchResult) {
        return new VerificationMapperBuilder(destinationMatchResult);
    }

    static VerificationResultBuilder assertTrue(boolean assertion) {
        return new VerificationResultBuilder(assertion);
    }

    private boolean isFailure() {
        return !success;
    }

    private VerificationResult(boolean success, String erroMessage) {
        this.success = success;
        this.errorMessage = erroMessage;
    }

    public static class VerificationResultBuilder {
        private final boolean assertion;

        public VerificationResultBuilder(boolean assertion) {
            this.assertion = assertion;
        }

        public VerificationResult orErrorMessage(String errorMessage) {
            return new VerificationResult(assertion, !assertion ? errorMessage : "");
        }
    }

    public static class VerificationMapperBuilder {
        private final VerificationResult[] destinationMatchResult;

        public VerificationMapperBuilder(VerificationResult... destinationMatchResult) {
            this.destinationMatchResult = destinationMatchResult;
        }

        public void mapFailures(BiConsumer<Boolean, String> biConsumer) {
            Stream.of(destinationMatchResult)
                    .filter(VerificationResult::isFailure)
                    .forEach(verificationResult -> biConsumer.accept(verificationResult.success, verificationResult.errorMessage));
        }
    }
}
