package snove.seo.redirectcheck.domain;

import snove.seo.redirectcheck.model.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface HttpRequest {
    HttpResponse execute() throws IOException, URISyntaxException;
}
