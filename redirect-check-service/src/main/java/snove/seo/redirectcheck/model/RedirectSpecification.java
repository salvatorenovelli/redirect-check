package snove.seo.redirectcheck.model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Salvatore on 24/04/2016.
 */
public final class RedirectSpecification {

    private URI sourceURI;
    private URI expectedDestination;


    public RedirectSpecification() {
    }

    public RedirectSpecification(String sourceURI, String expectedDestinationURI) throws URISyntaxException {
        this(new URI(sourceURI.trim()), new URI(expectedDestinationURI.trim()));
    }

    public RedirectSpecification(URI sourceURI, URI expectedDestination) {
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
    }

    public URI getSourceURI() {
        return sourceURI;
    }

    public void setSourceURI(URI sourceURI) {

        this.sourceURI = sourceURI;
    }

    public URI getExpectedDestination() {
        return expectedDestination;
    }

    public void setExpectedDestination(URI expectedDestination) {
        this.expectedDestination = expectedDestination;
    }

    @Override
    public String toString() {
        return "RedirectSpecification{" +
                "sourceURI=" + sourceURI +
                ", expectedDestination=" + expectedDestination + '}';
    }
}
