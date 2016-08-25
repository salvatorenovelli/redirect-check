package snove.seo.redirectcheck.http;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServerScenarioBuilder {

    private final Server server;
    private final Map<String, Redirect> redirects = new TreeMap<>();

    public TestServerScenarioBuilder(Server server) {
        this.server = server;
    }

    public TestServerScenarioBuilder with_301_MovedPermanently(String source, String destination) throws Exception {
        redirects.put(source, new Redirect(301, destination));
        return this;
    }

    public void run() throws Exception {
        server.setHandler(new NonSmartHandler());
        server.start();
    }

    private void handleAsRedirect(String s, Response httpServletResponse) throws IOException {
        final Redirect redirect = redirects.get(s);
        httpServletResponse.sendRedirect(redirect.httpStatusCode, redirect.dstPath);
    }

    /**
     * Unfortunately my first attempt to use {@link org.eclipse.jetty.server.handler.MovedContextHandler}
     * failed as when you define a redirect from /source to /destination, it would anyway
     * arbitrarily 302 redirect "/source" to "/source/" first and then "/source/" to "/destination"
     * (and probably it would go as far as creating another redirect to "/destination/")
     */
    private class NonSmartHandler extends AbstractHandler {
        @Override
        public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
            if (redirects.containsKey(s)) {
                handleAsRedirect(s, (Response) httpServletResponse);
            }
        }
    }

    private static class Redirect {
        final String dstPath;
        final int httpStatusCode;

        public Redirect(int httpStatusCode, String dstPath) {
            this.dstPath = dstPath;
            this.httpStatusCode = httpStatusCode;
        }
    }

}

