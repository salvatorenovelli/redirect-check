package com.github.salvatorenovelli.redirectcheck.http;


import com.github.salvatorenovelli.redirectcheck.domain.HttpRequest;
import com.github.salvatorenovelli.redirectcheck.model.HttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;


public class HttpGetRequest implements HttpRequest {

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
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {

                Optional<String> contentType = extractCharacterEncodingIfPresent(connection);

                URI location = contentType
                        .map(s -> tryDecode(connection.getHeaderField("location").getBytes(), s))
                        .orElse(Optional.of(new URI(connection.getHeaderField("location"))))
                        .get();


                if (location.isAbsolute()) {
                    dstURI = location;
                } else {
                    dstURI = dstURI.resolve(location);
                }
            }
        }


        return new HttpResponse(connection.getResponseCode(), dstURI);
    }

    private Optional<URI> tryDecode(byte[] urlBytes, String characterEncoding) {
        try {
            return Optional.of(new URI(new String(urlBytes, characterEncoding)));
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            return Optional.<URI>empty();
        }
    }

    private Optional<String> extractCharacterEncodingIfPresent(HttpURLConnection connection) {

        if(connection.getContentType()!=null){
            return Optional.of(connection.getContentType().split("=")[1]);
        }
        return Optional.empty();
    }

    private String encodeURIComponent(String s) {
        return s.replaceAll("%20", "\\s")
                .replaceAll("%21", "!")
                .replaceAll("%27", "'")
                .replaceAll("%28", "(")
                .replaceAll("%29", ")")
                .replaceAll("%7E", "~");
    }
}
