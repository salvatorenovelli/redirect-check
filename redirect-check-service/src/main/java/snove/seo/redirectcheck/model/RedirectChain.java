package snove.seo.redirectcheck.model;

import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes a chain of steps ({@link RedirectChainElement}s) that creates a path between a source link to a destination link.
 */
public final class RedirectChain {

    private static final String REDIRECT_LOOP = "Redirect loop";
    private final List<RedirectChainElement> elements = new ArrayList<>();
    private boolean isInvalid = false;
    private String status = "";

    public int getNumOfRedirect() {
        return elements.size() - 1;
    }

    public List<RedirectChainElement> getElements() {
        return new ArrayList<>(elements);
    }

    public boolean addElement(RedirectChainElement redirectChainElement) {
        return elements.add(redirectChainElement);
    }

    public URI getDestinationURI() {
        return getLastElement().getDestinationURI();
    }

    public HttpStatus getLastStatus() {
        return getLastElement().getStatus();
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public String getStatus() {
        return status;
    }

    private RedirectChainElement getLastElement() {
        return elements.get(elements.size() - 1);
    }

    public void markAsRedirectLoop() {
        markAsInvalid(REDIRECT_LOOP);
    }

    public void markAsInvalid(String reason) {
        this.status = "Invalid: " + reason;
        this.isInvalid = true;
    }
}


