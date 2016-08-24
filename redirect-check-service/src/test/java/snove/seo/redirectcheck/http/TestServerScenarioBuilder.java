package snove.seo.redirectcheck.http;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.MovedContextHandler;

import java.util.ArrayList;
import java.util.List;

public class TestServerScenarioBuilder {

    private final Server server;
    private final List<Handler> handlers = new ArrayList<>();

    public TestServerScenarioBuilder(Server server) {
        this.server = server;
    }


    public TestServerScenarioBuilder withPermanentlyRedirect(String source, String destination) {
        MovedContextHandler handler = new MovedContextHandler(null, source, destination);
        handler.setPermanent(true);
        handlers.add(handler);
        return this;
    }

    public void run() throws Exception {
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(handlers.toArray(new Handler[handlers.size()]));
        server.setHandler(contexts);
        server.start();
    }
}
