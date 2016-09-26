package snove.seo.redirectcheck.model;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class RedirectAnalysisRequest {

    private URI sourceURI;
    private URI expectedDestination;

    public RedirectAnalysisRequest(String sourceURI, String expectedDestinationURI) throws URISyntaxException {
        this(new URI(sourceURI.trim()), new URI(expectedDestinationURI.trim()));
    }

    public RedirectAnalysisRequest(URI sourceURI, URI expectedDestination) {
        this.sourceURI = sourceURI;
        this.expectedDestination = expectedDestination;
    }
}
