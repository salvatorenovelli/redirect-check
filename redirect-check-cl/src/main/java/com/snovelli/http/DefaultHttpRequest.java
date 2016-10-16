package com.snovelli.http;


import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

public final class DefaultHttpRequest implements HttpRequest {

    private final URI uri;
    private final HttpMethod method;

    public DefaultHttpRequest(URI uri, HttpMethod method) {
        this.uri = uri;
        this.method = method;
    }


    @Override
    public HttpResponse execute() throws IOException, URISyntaxException {


        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();

        connection.setRequestMethod(method.name());
        connection.setInstanceFollowRedirects(false);
        connection.connect();


        URI dstURI = uri;

        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                URI location = new URI(encodeURIComponent(connection.getHeaderField("location")));

                if (location.isAbsolute()) {
                    dstURI = location;
                } else {
                    dstURI = dstURI.resolve(location);
                }
            }
        }


        return new HttpResponse(HttpStatus.valueOf(connection.getResponseCode()), dstURI);
    }

    private String encodeURIComponent(String s) {

        return s
                .replaceAll("\\s", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultHttpRequest that = (DefaultHttpRequest) o;

        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;
        return method == that.method;

    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }
}
