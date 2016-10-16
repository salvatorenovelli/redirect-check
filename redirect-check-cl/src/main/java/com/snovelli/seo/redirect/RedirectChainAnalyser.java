package com.snovelli.seo.redirect;

import com.snovelli.http.HttpResponse;
import com.snovelli.model.HttpClientRequestFactory;
import com.snovelli.model.RedirectChain;
import com.snovelli.model.RedirectChainElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.HttpStatus.FOUND;
import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;
import static org.springframework.http.HttpStatus.SEE_OTHER;

@Component
public class RedirectChainAnalyser {


    private static final Logger logger = LoggerFactory.getLogger(RedirectChainAnalyser.class);


    private final HttpClientRequestFactory connectorFactory;

    @Autowired
    public RedirectChainAnalyser(HttpClientRequestFactory connectorFactory) {
        this.connectorFactory = connectorFactory;
    }

    /**
     *
     * */
    public RedirectChain analyseRedirectChain(String startURI) {

        RedirectChain result = new RedirectChain();

        try {

            URI currentURI = getAbsoluteURI(startURI);
            HttpResponse curResponse = null;

            while (curResponse == null || isRedirect(curResponse.getStatusCode())) {


                curResponse = connectorFactory.getConnector(currentURI).execute();
                HttpStatus status = curResponse.getStatusCode();

                if (isRedirect(status)) {
                    currentURI = curResponse.getLocation();
                }


                boolean insertResult = result.addElement(new RedirectChainElement(status, currentURI));

                if (!insertResult) {
                    result.markAsRedirectLoop();
                    break;
                }

            }


        } catch (Exception e) {
            logger.warn("Error while analysing {} because {}", startURI, e.toString());
            result.markAsFailed(e.getMessage());
        }

        return result;


    }

    private URI getAbsoluteURI(String startURI) throws URISyntaxException {

        if (!startURI.startsWith("http")) {
            startURI = "http://" + startURI;
        }

        return new URI(startURI.trim());
    }

    private boolean isRedirect(HttpStatus status) {
        return status == FOUND || status == MOVED_PERMANENTLY || status == SEE_OTHER;
    }
}
