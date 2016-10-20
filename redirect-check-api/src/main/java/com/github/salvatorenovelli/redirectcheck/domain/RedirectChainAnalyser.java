package com.github.salvatorenovelli.redirectcheck.domain;


import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.salvatorenovelli.redirectcheck.model.HttpResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChainElement;
import com.github.salvatorenovelli.redirectcheck.model.exception.RedirectLoopException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.http.HttpStatus.SC_MOVED_PERMANENTLY;


/**
 * Open connection to a given URI and follow the path if the request is redirected. Produces a
 * {@link RedirectChain}
 */
public class RedirectChainAnalyser {


    private static final Logger logger = LoggerFactory.getLogger(RedirectChainAnalyser.class);
    private final HttpRequestFactory requestFactory;

    public RedirectChainAnalyser(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    /**
     * Open an HTTP connection to the provided {@link URI} and follow the redirects if they exists.
     *
     * @return an object that describes the chain of redirect that the startURI lead to.
     *
     * */
    public RedirectChain analyseRedirectChain(String startURI) {

        RedirectChain result = new RedirectChain();

        try {

            URI currentURI = getAbsoluteURI(startURI);
            HttpResponse curResponse = null;

            while (curResponse == null || isRedirect(curResponse.getStatusCode())) {


                curResponse = requestFactory.createRequest(currentURI).execute();
                int httpStatus = curResponse.getStatusCode();
                result.addElement(new RedirectChainElement(httpStatus, currentURI));

                if (isRedirect(httpStatus)) {
                    currentURI = curResponse.getLocation();
                }
            }


        } catch (IOException | IllegalArgumentException | URISyntaxException e) {
            logger.warn("Error while analysing {}: {}", startURI, e.toString());
            result.markAsFailed(e.toString());
        } catch (RedirectLoopException e) {
            result.markAsRedirectLoop();
        }

        return result;


    }

    private URI getAbsoluteURI(String startURI) throws URISyntaxException {

        if (!startURI.startsWith("http")) {
            startURI = "http://" + startURI;
        }

        return new URI(startURI.trim());
    }


    private boolean isRedirect(int httpStatus) {
        return httpStatus == HttpStatus.SC_MOVED_TEMPORARILY || httpStatus == SC_MOVED_PERMANENTLY || httpStatus == HttpStatus.SC_SEE_OTHER;
    }
}
