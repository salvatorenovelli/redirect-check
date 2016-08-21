package snove.seo.redirectcheck.model.exception;

/**
 * Created by Salvatore on 21/08/2016.
 */
public class RedirectLoopException extends Exception {

    public RedirectLoopException() {
    }

    public RedirectLoopException(String message) {
        super(message);
    }
}
