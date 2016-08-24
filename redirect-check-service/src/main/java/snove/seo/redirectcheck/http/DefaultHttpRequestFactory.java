package snove.seo.redirectcheck.http;


import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import snove.seo.redirectcheck.domain.HttpRequestFactory;
import snove.seo.redirectcheck.domain.HttpRequest;

import java.net.URI;


@Component
public class DefaultHttpRequestFactory implements HttpRequestFactory {
    @Override
    public HttpRequest createRequest(URI httpURI) {
        return new DefaultHttpRequest(httpURI, HttpMethod.GET);
    }
}
