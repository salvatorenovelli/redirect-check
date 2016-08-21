package snove.seo.redirectcheck.domain;

import snove.seo.redirectcheck.model.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Salvatore on 24/04/2016.
 */
public interface HttpRequest {
    HttpResponse execute() throws IOException, URISyntaxException;
}
