package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.cli.ProgressMonitor;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRedirectSpecAnalyser implements RedirectSpecAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRedirectSpecAnalyser.class);
    private final RedirectChainAnalyser analyser;
    private final ProgressMonitor progressMonitor;
    private RedirectCheckResponseFactory redirectCheckResponseFactory;

    public DefaultRedirectSpecAnalyser(RedirectChainAnalyser redirectChainAnalyser,
                                       RedirectCheckResponseFactory redirectCheckResponseFactory,
                                       ProgressMonitor progressMonitor) {
        this.analyser = redirectChainAnalyser;
        this.redirectCheckResponseFactory = redirectCheckResponseFactory;
        this.progressMonitor = progressMonitor;
    }

    @Override
    public RedirectCheckResponse checkRedirect(RedirectSpecification spec) {

        try {
            logger.debug("Analysing " + spec);
            if (spec.isValid()) {
                RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
                return redirectCheckResponseFactory.createResponse(spec, redirectChain);
            } else {
                return redirectCheckResponseFactory.createResponseForInvalidSpec(spec);
            }
        } finally {
            progressMonitor.tick();
        }

    }
}
