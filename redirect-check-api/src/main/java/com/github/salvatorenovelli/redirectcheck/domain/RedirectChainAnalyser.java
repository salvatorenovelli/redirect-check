package com.github.salvatorenovelli.redirectcheck.domain;

import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

import java.net.URI;

/**
 * Open connection to a given URI and follow the path if the request is redirected. Produces a
 * {@link RedirectChain}
 */
public interface RedirectChainAnalyser {
    /**
     * Open an HTTP connection to the provided {@link URI} and follow the redirects if they exists.
     *
     * @return an object that describes the chain of redirect that the startURI lead to.
     */
    RedirectChain analyseRedirectChain(String startURI);
}
