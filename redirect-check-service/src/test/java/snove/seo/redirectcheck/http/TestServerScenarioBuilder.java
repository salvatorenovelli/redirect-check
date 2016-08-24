package snove.seo.redirectcheck.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.MovedContextHandler;

public class TestServerScenarioBuilder {

    private final Server server;
    private final ContextHandlerCollection contexts = new ContextHandlerCollection();

    public TestServerScenarioBuilder(Server server) {
        this.server = server;
    }


    public TestServerScenarioBuilder withMovedPermanently(String source, String destination) throws Exception {
        MovedContextHandler handler = new MovedContextHandler(contexts, source, destination);
        handler.setPermanent(true);
        handler.setDiscardPathInfo(true);
        return this;
    }

    public void run() throws Exception {
        server.setHandler(contexts);
        server.start();
    }
}
