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
import java.nio.charset.Charset;


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

    private URI extractDestinationUri(HttpURLConnection connection, URI initialLocation) throws URISyntaxException {
        final String locationHeaderField = connection.getHeaderField("location");
        final String encoding = extractCharacterEncoding(connection);

        URI location = decode(locationHeaderField, encoding);

        if (location.isAbsolute()) {
            return location;
        } else {
            return initialLocation.resolve(location);
        }

    }

    private URI decode(String locationHeaderField, String characterEncoding) throws URISyntaxException {
        URI locationsByte = tryDecode(locationHeaderField, characterEncoding);
        if (locationsByte != null) return locationsByte;
        return new URI(locationHeaderField);
    }

    private URI tryDecode(String locationHeaderField, String characterEncoding) throws URISyntaxException {
        try {
            if (characterEncoding != null) {
                byte[] locationsByte = locationHeaderField.getBytes(Charset.defaultCharset().name());
                return new URI(new URI(new String(locationsByte, characterEncoding)).toASCIIString());
            }
        } catch (UnsupportedEncodingException e) {
            logger.info("Decoding of {} failed, using default charset. Error: {}", locationHeaderField, e.getMessage());
        }
        return null;
    }

    private String extractCharacterEncoding(HttpURLConnection connection) {
        if (connection.getContentType() != null) {
            return connection.getContentType().split("=")[1];
        }
        return null;
    }

}
