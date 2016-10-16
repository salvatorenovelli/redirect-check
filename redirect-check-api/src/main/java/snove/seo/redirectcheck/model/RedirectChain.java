package snove.seo.redirectcheck.model;


import org.apache.http.HttpStatus;
import snove.seo.redirectcheck.model.exception.RedirectLoopException;

import java.util.ArrayList;
import java.util.List;

public final class RedirectChain {

    public static final String REDIRECT_LOOP = "Redirect loop";
    private final List<RedirectChainElement> elements;
    private boolean isFailed = false;
    private String status = "";

    public RedirectChain() {
        elements = new ArrayList<>();
    }

    public List<RedirectChainElement> getElements() {
        return new ArrayList<>(elements);
    }

    public int getNumOfRedirect() {
        return elements.size() - 1;
    }

    public boolean addElement(RedirectChainElement redirectChainElement) throws RedirectLoopException {

        if (redirectChainElement.getHttpStatus() != HttpStatus.SC_OK && alreadyExistInTheChain(redirectChainElement)) {
            throw new RedirectLoopException();
        }

        return elements.add(redirectChainElement);
    }

    public void markAsRedirectLoop() {
        markAsFailed(REDIRECT_LOOP);
    }

    public void markAsFailed(String reason) {
        this.status = "Failed: " + reason;
        this.isFailed = true;
    }

    public String getDestinationURI() {
        return getLastElement().getDestinationURI().toASCIIString();
    }

    public int getLastHttpStatus() {
        return getLastElement().getHttpStatus();
    }

    public boolean isFailed() {
        return isFailed;
    }

    public String getStatus() {
        return status;
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


