package snove.seo.redirectcheck.http;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;

import snove.seo.redirectcheck.model.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


@RunWith(MockitoJUnitRunner.class)
public class HttpGetRequestTest {

    private Server server;

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void usesGetOn301WithRelativeDstPath() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", "/relative_destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source")).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.MOVED_PERMANENTLY));
        assertThat(execute.getLocation(), is(testUri("/relative_destination")));
    }

    @Test
    public void usesGetOn301WithAbsoluteDstPath() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", "http://absolute_destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source")).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.MOVED_PERMANENTLY));
        assertThat(execute.getLocation(), is(new URI("http://absolute_destination")));
    }

    @Test
    public void testGetOn302Redirect() throws Exception {
        givenAnHttpServer()
                .with_302_Found("/source", "/destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source")).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(execute.getLocation(), is(testUri("/destination")));
    }

    @Test
    public void testGetOn303SeeOther() throws Exception {
        givenAnHttpServer()
                .with_303_SeeOther("/source", "/destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source")).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SEE_OTHER));
        assertThat(execute.getLocation(), is(testUri("/destination")));
    }


    @Test
    public void getOn200() throws Exception {

        givenAnHttpServer()
                .with_200_Ok("/hello")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/hello")).execute();

        assertThat(execute.getStatus(), is(HttpStatus.OK));
        assertThat(execute.getLocation(), is(testUri("/hello")));
    }

    private TestServerScenarioBuilder givenAnHttpServer() {
        server = new Server(0);
        return new TestServerScenarioBuilder(server);
    }

    private URI testUri(String url) throws URISyntaxException {
        if (!server.isStarted()) {
            throw new IllegalStateException("Sorry, you'll need to run the scenario before asking for URI. (At the moment the server port is not known)");
        }
        int localPort = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
        return new URI("http://localhost:" + localPort + url);
    }
}
