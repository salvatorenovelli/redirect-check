package com.github.salvatorenovelli.redirectcheck.http;

import com.github.salvatorenovelli.redirectcheck.model.HttpResponse;

import org.apache.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class HttpGetRequestTest {

    private Server server;
    // Stands for UTF-8 "/família"
    public static final String LOCATION_WITH_UNICODE_CHARACTERS = new String(new byte[]{0x2f, 0x66, 0x61, 0x6d, -61, -83, 0x6c, 0x69, 0x61});

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

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_MOVED_PERMANENTLY));
        assertThat(execute.getLocation(), is(testUri("/relative_destination")));
    }

    @Test
    public void usesGetOn301WithAbsoluteDstPath() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", "http://absolute_destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source")).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_MOVED_PERMANENTLY));
        assertThat(execute.getLocation(), is(new URI("http://absolute_destination")));
    }

    @Test
    public void testGetOn302Redirect() throws Exception {
        givenAnHttpServer()
                .with_302_Found("/source", "/destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source")).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_MOVED_TEMPORARILY));
        assertThat(execute.getLocation(), is(testUri("/destination")));
    }

    @Test
    public void testGetOn303SeeOther() throws Exception {
        givenAnHttpServer()
                .with_303_SeeOther("/source", "/destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source")).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_SEE_OTHER));
        assertThat(execute.getLocation(), is(testUri("/destination")));
    }

    @Test
    public void getOn200() throws Exception {
        givenAnHttpServer()
                .with_200_Ok("/hello")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/hello")).execute();

        assertThat(execute.getHttpStatus(), is(HttpStatus.SC_OK));
        assertThat(execute.getLocation(), is(testUri("/hello")));
    }

    /**
     *
     * Some website, put unicode characters in their target location (without escaping them).
     *
     * The response bytes containing the unicode characters will "decoded" into string with the default charset, and returned as a
     * header parameter. The problem is that the web server might have encoded them in a different charset than ours,
     * therefore we had to read the charset from the header and transcode the our string in the intendend charset.
     *
     *
     * This test demonstrate the functionality works.
     *
     * PS: I used two passes as it made the test easier as we didn't have to assert against a unicode target URL.
     *
     */
    @Test
    public void weShouldHandleUnicodeCharacters() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", LOCATION_WITH_UNICODE_CHARACTERS)
                .with_301_MovedPermanently(LOCATION_WITH_UNICODE_CHARACTERS, "/destination")
                .run();

        HttpResponse firstPass = new HttpGetRequest(testUri("/source")).execute();
        HttpResponse secondPass = new HttpGetRequest(firstPass.getLocation()).execute();

        assertThat(secondPass.getStatusCode(), is(HttpStatus.SC_MOVED_PERMANENTLY));
        assertThat(secondPass.getLocation(), is(testUri("/destination")));
    }

    @Test
    public void unicodeRedirectGetUrlEncoded() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", LOCATION_WITH_UNICODE_CHARACTERS)
                .run();

        HttpResponse request = new HttpGetRequest(testUri("/source")).execute();
        assertThat(request.getLocation(), is(testUri("/fam%C3%ADlia")));

    }

    @Test
    public void unicodeCharactersInLocationHeaderGetDecodedEvenWhenCharsetIsNotSpecified() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", LOCATION_WITH_UNICODE_CHARACTERS)
                .disableCharsetHeader()
                .run();

        HttpResponse request = new HttpGetRequest(testUri("/source")).execute();
        assertThat(request.getLocation(), is(testUri("/fam%C3%ADlia")));

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
