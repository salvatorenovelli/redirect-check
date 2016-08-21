package snove.seo.redirectcheck.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.net.URI;

@Data
public class HttpResponse {

    private final HttpStatus status;
    private final URI location;

    public HttpResponse(HttpStatus status, URI location) {
        this.status = status;
        this.location = location;
    }

    public URI getLocation() {
        return location;
    }

    public HttpStatus getStatusCode() {
        return status;
    }
}
