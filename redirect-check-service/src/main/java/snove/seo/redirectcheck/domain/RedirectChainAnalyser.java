package snove.seo.redirectcheck.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import snove.seo.redirectcheck.model.HttpResponse;
import snove.seo.redirectcheck.model.RedirectChain;
import snove.seo.redirectcheck.model.exception.RedirectLoopException;

import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;
import static org.springframework.http.HttpStatus.SEE_OTHER;


/**
 * Open connection to a given URI and follow the path if the request is redirected. Produces a
 * {@link RedirectChain}
 */
public class RedirectChainAnalyser {


    private static final Logger logger = LoggerFactory.getLogger(RedirectChainAnalyser.class);
    private final HttpRequestFactory requestFactory;

    @Autowired
    public RedirectChainAnalyser(HttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    /**
     * Open an HTTP connection to the provided {@link URI} and follow the redirects if they exists.
     *
     * @return an object that describes the chain of redirect that the startURI lead to.
     *
     * */
    public RedirectChain analyseRedirectChain(URI startURI) {

        RedirectChain result = new RedirectChain();

        try {

            URI currentURI = startURI;
            HttpResponse curResponse = null;

            while (curResponse == null || isRedirect(curResponse.getStatusCode())) {

                curResponse = requestFactory
                        .createRequest(currentURI)
                        .execute();

                HttpStatus status = curResponse.getStatusCode();
                result.addElement(curResponse);

                if (isRedirect(status)) {
                    currentURI = curResponse.getLocation();
                }
            }


        } catch (IOException | URISyntaxException e) {
            logger.info("Error while analysing {} because {}", startURI, e.getMessage());
            result.markAsInvalid(e.getMessage());
        } catch (RedirectLoopException e) {
            result.markAsRedirectLoop();
        }

        return result;


    }


    private boolean isRedirect(HttpStatus status) {
        return status == FOUND || status == MOVED_PERMANENTLY || status == SEE_OTHER;
    }
}
