package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;

public interface RedirectSpecAnalyser {
    RedirectCheckResponse checkRedirect(RedirectSpecification spec);
}
