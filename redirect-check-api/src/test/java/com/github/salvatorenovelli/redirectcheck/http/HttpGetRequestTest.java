package com.github.salvatorenovelli.redirectcheck.http;

import com.github.salvatorenovelli.redirectcheck.model.HttpResponse;
import org.apache.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpGetRequestTest {

    public static final String LOCATION_WITH_UNICODE_CHARACTERS = "/família";
    Logger logger = LoggerFactory.getLogger(HttpGetRequestTest.class);
    private Server server;
    private ConnectionFactory connectionFactory = new DefaultConnectionFactory();
    @Mock private ConnectionFactory mockConnectionFactory;
    @Mock private java.net.HttpURLConnection mockConnection;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }


    @Test
    public void itShouldBeAbleToDealWithDifferentCharsets() throws Exception {

        when(mockConnectionFactory.createConnection(any(URI.class))).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(301);
        when(mockConnection.getHeaderField("location")).thenReturn("/teg/Ñ\u0083Ñ\u0085Ð¾Ð´-Ð·Ð°-Ð¾Ð´ÐµÐ¶Ð´Ð¾Ð¹");


        HttpResponse execute = new HttpGetRequest(new URI("/тэг/уход-за-одеждой"), mockConnectionFactory).execute();
        assertThat(execute.getStatusCode(), is(HttpStatus.SC_MOVED_PERMANENTLY));
        assertThat(execute.getLocation(), is(new URI("/teg/%D1%83%D1%85%D0%BE%D0%B4-%D0%B7%D0%B0-%D0%BE%D0%B4%D0%B5%D0%B6%D0%B4%D0%BE%D0%B9")));
    }

    @Test
    public void usesGetOn301WithRelativeDstPath() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", "/relative_destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source"), connectionFactory).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_MOVED_PERMANENTLY));
        assertThat(execute.getLocation(), is(testUri("/relative_destination")));
    }

    @Test
    public void usesGetOn301WithAbsoluteDstPath() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", "http://absolute_destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source"), connectionFactory).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_MOVED_PERMANENTLY));
        assertThat(execute.getLocation(), is(new URI("http://absolute_destination")));
    }

    @Test
    public void testGetOn302Redirect() throws Exception {
        givenAnHttpServer()
                .with_302_Found("/source", "/destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source"), connectionFactory).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_MOVED_TEMPORARILY));
        assertThat(execute.getLocation(), is(testUri("/destination")));
    }

    @Test
    public void testGetOn303SeeOther() throws Exception {
        givenAnHttpServer()
                .with_303_SeeOther("/source", "/destination")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/source"), connectionFactory).execute();

        assertThat(execute.getStatusCode(), is(HttpStatus.SC_SEE_OTHER));
        assertThat(execute.getLocation(), is(testUri("/destination")));
    }

    @Test
    public void getOn200() throws Exception {
        givenAnHttpServer()
                .with_200_Ok("/hello")
                .run();

        HttpResponse execute = new HttpGetRequest(testUri("/hello"), connectionFactory).execute();

        assertThat(execute.getHttpStatus(), is(HttpStatus.SC_OK));
        assertThat(execute.getLocation(), is(testUri("/hello")));
    }

    /**
     * Some website, put unicode characters in their target location (without escaping them).
     * <p>
     * The response bytes containing the unicode characters will end up being "decoded" into string with the default charset, and returned as a
     * header parameter. The problem is that the web server might have encoded them in a different charset than ours,
     * therefore we had to read the charset from the header and transcode the string into the intended charset.
     * <p>
     * <p>
     * PS: I used two redirects (firstPass, secondPass) as it made the test easier as we didn't have to assert against a unicode target URL.
     */
    @Test
    public void weShouldHandleUnicodeCharacters() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", LOCATION_WITH_UNICODE_CHARACTERS)
                .with_301_MovedPermanently(LOCATION_WITH_UNICODE_CHARACTERS, "/destination")
                .run();

        HttpResponse firstPass = new HttpGetRequest(testUri("/source"), connectionFactory).execute();
        HttpResponse secondPass = new HttpGetRequest(firstPass.getLocation(), connectionFactory).execute();

        assertThat(secondPass.getStatusCode(), is(HttpStatus.SC_MOVED_PERMANENTLY));
        assertThat(secondPass.getLocation(), is(testUri("/destination")));
    }

    @Test
    public void unicodeRedirectGetUrlEncoded() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", LOCATION_WITH_UNICODE_CHARACTERS)
                .run();

        HttpResponse request = new HttpGetRequest(testUri("/source"), connectionFactory).execute();
        assertThat(request.getLocation(), is(testUri("/fam%edlia")));

    }

    @Test
    public void unicodeCharactersInLocationHeaderGetDecodedEvenWhenCharsetIsNotSpecified() throws Exception {
        givenAnHttpServer()
                .with_301_MovedPermanently("/source", LOCATION_WITH_UNICODE_CHARACTERS)
                .disableCharsetHeader()
                .run();

        HttpResponse request = new HttpGetRequest(testUri("/source"), connectionFactory).execute();
        assertThat(request.getLocation(), is(testUri("/fam%edlia")));

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
