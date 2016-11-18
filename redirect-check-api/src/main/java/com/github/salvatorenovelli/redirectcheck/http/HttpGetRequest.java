package com.github.salvatorenovelli.redirectcheck.http;


import com.github.salvatorenovelli.redirectcheck.domain.HttpRequest;
import com.github.salvatorenovelli.redirectcheck.model.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;


public class HttpGetRequest implements HttpRequest {

    private static final Logger logger = LoggerFactory.getLogger(HttpGetRequest.class);
    private final URI uri;


    public HttpGetRequest(URI uri) {
        this.uri = uri;

    }

    @Override
    public HttpResponse execute() throws IOException, URISyntaxException {

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(false);
        connection.connect();

        URI dstURI = uri;

        int status = connection.getResponseCode();

        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
            dstURI = extractDestinationUri(connection, dstURI);
        }

        return new HttpResponse(connection.getResponseCode(), dstURI);
    }

    private URI extractDestinationUri(HttpURLConnection connection, URI initialLocation) throws URISyntaxException, UnsupportedEncodingException {
        String locationHeaderField = connection.getHeaderField("location");
        URI location = new URI(locationHeaderField);

        if (containsUnicodeCharacters(locationHeaderField)) {
            logger.warn("Header field 'location' contains unicode characters, trying to decode them. ({})", locationHeaderField);
            location = escapeUnicodeCharacters(locationHeaderField, extractCharacterEncoding(connection));
        }

        if (location.isAbsolute()) {
            return new URI(locationHeaderField);
        } else {
            return initialLocation.resolve(location);
        }
    }

    private boolean containsUnicodeCharacters(String locationHeaderField) {
        for (int i = 0; i < locationHeaderField.length(); i++) {
            if (locationHeaderField.charAt(i) >= 128) return true;
        }
        return false;
    }

    private URI escapeUnicodeCharacters(String locationHeaderField, String encoding) throws UnsupportedEncodingException, URISyntaxException {
        final String locationWithOriginalEncoding = new String(locationHeaderField.getBytes(), encoding != null ? encoding : "UTF-8");


        return new URI(new URI(locationWithOriginalEncoding).toASCIIString());
    }

    private String extractCharacterEncoding(HttpURLConnection connection) {
        if (connection.getContentType() != null) {
            return connection.getContentType().split("=")[1];
        }
        return null;
    }

}
