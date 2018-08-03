package com.github.salvatorenovelli.redirectcheck.model;

import java.util.function.Consumer;
import java.util.stream.Stream;

class VerificationResult {

    private final boolean success;
    private final String errorMessage;

    static VerificationResult failure(String message) {
        return new VerificationResult(false, message);
    }


    public static VerificationMapperBuilder of(VerificationResult... results) {
        return new VerificationMapperBuilder(results);
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
        private final VerificationResult[] results;

        public VerificationMapperBuilder(VerificationResult... results) {
            this.results = results;
        }

        public void forEachFailure(Consumer<String> consumer) {
            Stream.of(results)
                    .filter(VerificationResult::isFailure)
                    .forEach(curResult -> consumer.accept(curResult.errorMessage));
        }
    }

    public static class Verification {
        public static VerificationResultBuilder verification(CheckedSupplier<Boolean> supplier) {
            return new VerificationResultBuilder(supplier);
        }
    }

    public interface CheckedSupplier<T> {
        T apply() throws Exception;
    }
}
