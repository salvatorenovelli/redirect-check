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


    private boolean isFailure() {
        return !success;
    }

    private VerificationResult(boolean success, String erroMessage) {
        this.success = success;
        this.errorMessage = erroMessage;
    }

    public static class VerificationResultBuilder {
        private final CheckedSupplier<Boolean> assertion;

        public VerificationResultBuilder(CheckedSupplier<Boolean> assertion) {
            this.assertion = assertion;
        }

        public VerificationResult orErrorMessage(String errorMessage) {

            try {
                Boolean assertionResult = assertion.apply();
                return new VerificationResult(assertionResult, !assertionResult ? errorMessage : "");
            } catch (Exception e) {
                return VerificationResult.failure(e.getMessage());
            }


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

    public static class Verification {
        public static VerificationResultBuilder verify(CheckedSupplier<Boolean> supplier) {
            return new VerificationResultBuilder(supplier);
        }
    }

    public interface CheckedSupplier<T> {
        T apply() throws Exception;
    }
}
