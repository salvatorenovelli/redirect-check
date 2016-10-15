package snove.seo.redirectcheck.domain;



import org.apache.http.HttpStatus;
import snove.seo.redirectcheck.model.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


class MockHttpRequestFactory implements HttpRequestFactory {


    private final Map<URI, HttpResponse> responses = new HashMap<>();
    private final Map<URI, IOException> exceptions = new HashMap<>();


    public MockHttpRequestFactory withRedirect(String source, int httpStatus, String dstLocation) throws URISyntaxException {
        responses.put(new URI(source), new HttpResponse(httpStatus, new URI(dstLocation)));
        return this;
    }

    public MockHttpRequestFactory withOk(String source) throws URISyntaxException {
        responses.put(new URI(source), new HttpResponse(HttpStatus.SC_OK, new URI(source)));
        return this;
    }

    @Override
    public HttpRequest createRequest(URI httpURI) {
        return () -> {
            if (exceptions.containsKey(httpURI)) {
                throw exceptions.get(httpURI);
            }
            return responses.get(httpURI);
        };
    }

    public void withIOException(String sourceURI, String errorMessage) throws URISyntaxException {
        exceptions.put(new URI(sourceURI), new IOException(errorMessage));
    }
}
