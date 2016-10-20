package com.github.salvatorenovelli.redirectcheck.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class RedirectAnalysisRequest {

    private String sourceURI;
    private String expectedDestination;

    public RedirectAnalysisRequest(String sourceURI, String expectedDestination) {
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
    }
}
