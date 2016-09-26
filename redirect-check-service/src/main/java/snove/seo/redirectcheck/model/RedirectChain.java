package snove.seo.redirectcheck.model;

import org.springframework.http.HttpStatus;
import snove.seo.redirectcheck.model.exception.RedirectLoopException;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes a chain of steps ({@link HttpResponse}s) that creates a path between a source link to a destination link.
 */
public final class RedirectChain {

    public final static String REDIRECT_LOOP = "redirect loop";

    private final List<RedirectChainElement> elements = new ArrayList<>();
    private boolean isValid = true;
    private String finalStatus = "ok";

    public int getNumOfRedirect() {
        return elements.size() > 0 ? elements.size() - 1 : 0;
    }

    public boolean addElement(RedirectChainElement redirectChainElement) throws RedirectLoopException {

        if (redirectChainElement.getStatus() != HttpStatus.OK && alreadyExistInTheChain(redirectChainElement)) {
            throw new RedirectLoopException();
        }

        return elements.add(redirectChainElement);
    }

    public URI getDestinationURI() {
        return getLastElement().getDestinationURI();
    }

    public HttpStatus getLastStatus() {
        return getLastElement().getStatus();
    }

    public void markAsRedirectLoop() {
        this.isValid = false;
        this.finalStatus = REDIRECT_LOOP;
    }

    public void markAsInvalid(String message) {
        this.isValid = false;
        this.finalStatus = message;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getStatus() {
        return finalStatus;
    }

    List<RedirectChainElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    private boolean alreadyExistInTheChain(RedirectChainElement redirectChainElement) {
        return elements.stream()
                .map(RedirectChainElement::getDestinationURI)
                .filter(uri -> uri.equals(redirectChainElement.getDestinationURI()))
                .count() > 0;
    }

    private RedirectChainElement getLastElement() {
        return elements.get(elements.size() - 1);
    }
}


