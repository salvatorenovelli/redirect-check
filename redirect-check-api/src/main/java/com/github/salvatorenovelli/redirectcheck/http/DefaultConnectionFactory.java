package com.github.salvatorenovelli.redirectcheck.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public class DefaultConnectionFactory implements ConnectionFactory {
    @Override
    public HttpURLConnection createConnection(URI uri) throws IOException {
        return (HttpURLConnection) uri.toURL().openConnection();
    }
}
