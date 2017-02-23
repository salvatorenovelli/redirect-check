package com.github.salvatorenovelli.redirectcheck.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public interface ConnectionFactory {
    HttpURLConnection createConnection(URI uri) throws IOException;
}
