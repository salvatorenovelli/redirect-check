package snove.seo.redirectcheck.http;


import org.springframework.stereotype.Component;

import java.net.URI;

import snove.seo.redirectcheck.domain.HttpRequest;
import snove.seo.redirectcheck.domain.HttpRequestFactory;


@Component
public class DefaultHttpRequestFactory implements HttpRequestFactory {
    @Override
    public HttpRequest createRequest(URI httpURI) {
        return new HttpGetRequest(httpURI);
    }
}
