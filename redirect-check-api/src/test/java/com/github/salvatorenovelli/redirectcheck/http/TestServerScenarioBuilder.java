package com.github.salvatorenovelli.redirectcheck.http;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class TestServerScenarioBuilder {

    private static final Logger logger = LoggerFactory.getLogger(TestServerScenarioBuilder.class);
    private final Server server;
    private final Map<String, Redirect> redirectedLocations = new TreeMap<>();
    private final Set<String> servedLocations = new HashSet<>();


    public TestServerScenarioBuilder(Server server) {
        this.server = server;
    }

    public TestServerScenarioBuilder with_200_Ok(String location) {
        servedLocations.add(location);
        return this;
    }

    public TestServerScenarioBuilder with_301_MovedPermanently(String source, String destination) throws Exception {
        setRedirect(301, source, destination);
        return this;
    }

    public TestServerScenarioBuilder with_302_Found(String source, String destination) {
        setRedirect(302, source, destination);
        return this;
    }

    public TestServerScenarioBuilder with_303_SeeOther(String source, String destination) {
        setRedirect(303, source, destination);
        return this;
    }

    public void run() throws Exception {
        server.setHandler(new NonSmartHandler());
        server.start();
        logger.info("Test server listening on http://localhost:{}", ((ServerConnector) server.getConnectors()[0]).getLocalPort());
    }

    private void setRedirect(int httpStatus, String source, String destination) {
        redirectedLocations.put(source, new Redirect(httpStatus, destination));
    }

    private void handleAsRedirect(String s, HttpServletResponse httpServletResponse) throws IOException {
        final Redirect destination = redirectedLocations.get(s);
        httpServletResponse.setHeader("Location", destination.dstPath);
        httpServletResponse.setStatus(destination.httpStatusCode);
    }

    private void handleAsServed(String s, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
    }


    private static class Redirect {
        final String dstPath;
        final int httpStatusCode;

        public Redirect(int httpStatusCode, String dstPath) {
            this.dstPath = dstPath;
            this.httpStatusCode = httpStatusCode;
        }
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
            if (redirectedLocations.containsKey(s)) {
                handleAsRedirect(s, httpServletResponse);
                request.setHandled(true);
            }

            if (servedLocations.contains(s)) {
                handleAsServed(s, httpServletResponse);
                request.setHandled(true);
            }
        }
    }

}

