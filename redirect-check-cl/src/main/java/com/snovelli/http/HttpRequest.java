package com.snovelli.http;

import java.io.IOException;
import java.net.URISyntaxException;

public interface HttpRequest {
    HttpResponse execute() throws IOException, URISyntaxException;
}
