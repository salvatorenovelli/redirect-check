package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

public class RedirectCheckResponseFactory {
    public RedirectCheckResponse createResponse(RedirectSpecification spec, RedirectChain redirectChain) {
        return RedirectCheckResponse.createResponse(spec, redirectChain);
    }

    public RedirectCheckResponse createResponseForError(String errorMessage, RedirectSpecification spec) {
        return RedirectCheckResponse.createResponseForError(errorMessage, spec);
    }
}
