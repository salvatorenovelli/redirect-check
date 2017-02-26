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
    private final ConnectionFactory connectionFactory;


    public HttpGetRequest(URI uri, ConnectionFactory connectionFactory) {
        this.uri = uri;
        this.connectionFactory = connectionFactory;
    }


    @Override
    public HttpResponse execute() throws IOException, URISyntaxException {

        HttpURLConnection connection = connectionFactory.createConnection(new URI(uri.toASCIIString()));

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
        String locationHeader = connection.getHeaderField("location");
        URI location;


        if (containsUnicodeCharacters(locationHeader)) {
            logger.warn("Redirect destination {} contains non ASCII characters (as required by the standard)", connection.getURL());
            location = new URI(SafeStringEncoder.encodeString(locationHeader));
        } else {
            location = new URI(locationHeader);
        }

        if (location.isAbsolute()) {
            return location;
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

}
