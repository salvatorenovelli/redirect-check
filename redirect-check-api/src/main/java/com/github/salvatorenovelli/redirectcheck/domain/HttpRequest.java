package com.github.salvatorenovelli.redirectcheck.domain;

import com.github.salvatorenovelli.redirectcheck.model.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public interface HttpRequest {
    HttpResponse execute() throws IOException, URISyntaxException;
}
