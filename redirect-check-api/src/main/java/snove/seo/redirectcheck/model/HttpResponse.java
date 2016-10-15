package snove.seo.redirectcheck.model;

import lombok.Data;

import java.net.URI;

@Data
public class HttpResponse {

    private final int httpStatus;
    private final URI location;

    public HttpResponse(int httpStatus, URI location) {
        this.httpStatus = httpStatus;
        this.location = location;
    }

    public URI getLocation() {
        return location;
    }

    public int getStatusCode() {
        return httpStatus;
    }
}
