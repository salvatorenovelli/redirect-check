package snove.seo.redirectcheck.domain;


import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import snove.seo.redirectcheck.model.HttpResponse;
import snove.seo.redirectcheck.model.RedirectChain;
import snove.seo.redirectcheck.model.RedirectChainElement;
import snove.seo.redirectcheck.model.exception.RedirectLoopException;

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
    public RedirectChain analyseRedirectChain(URI startURI) {

        RedirectChain result = new RedirectChain();

        try {

            URI currentURI = startURI;
            HttpResponse curResponse = null;

            while (curResponse == null || isRedirect(curResponse.getStatusCode())) {

                curResponse = requestFactory
                        .createRequest(currentURI)
                        .execute();

                int httpStatus = curResponse.getStatusCode();
                result.addElement(new RedirectChainElement(httpStatus, currentURI));

                if (isRedirect(httpStatus)) {
                    currentURI = curResponse.getLocation();
                }
            }


        } catch (IOException | IllegalArgumentException | URISyntaxException e) {
            logger.error("Error while analysing {}: {}", startURI, e.toString());
            result.markAsInvalid(e.toString());
        } catch (RedirectLoopException e) {
            result.markAsRedirectLoop();
        }

        return result;


    }


    private boolean isRedirect(int httpStatus) {
        return httpStatus == HttpStatus.SC_MOVED_TEMPORARILY || httpStatus == SC_MOVED_PERMANENTLY || httpStatus == HttpStatus.SC_SEE_OTHER;
    }
}
