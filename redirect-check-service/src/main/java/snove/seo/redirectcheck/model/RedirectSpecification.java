package snove.seo.redirectcheck.model;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class RedirectSpecification {

    private URI sourceURI;
    private URI expectedDestination;

    public RedirectSpecification(String sourceURI, String expectedDestinationURI) throws URISyntaxException {
        this(new URI(sourceURI.trim()), new URI(expectedDestinationURI.trim()));
    }

    public RedirectSpecification(URI sourceURI, URI expectedDestination) {
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
    }
}
