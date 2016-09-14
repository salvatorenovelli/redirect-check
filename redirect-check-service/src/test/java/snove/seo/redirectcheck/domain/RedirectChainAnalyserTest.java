package snove.seo.redirectcheck.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.net.URI;

import snove.seo.redirectcheck.model.RedirectChain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.containsString;


@RunWith(MockitoJUnitRunner.class)
public class RedirectChainAnalyserTest {

    MockHttpRequestFactory mockHttpRequestFactory = new MockHttpRequestFactory();

    RedirectChainAnalyser sut = new RedirectChainAnalyser(mockHttpRequestFactory);

    @Test
    public void successfulRequestShouldBeNotMarkedAsInvalid() throws Exception {
        givenAScenario()
                .withOk("http://www.example.com");
        RedirectChain redirectChain = sut.analyseRedirectChain(new URI("http://www.example.com"));

        assertThat(redirectChain.isValid(), is(true));
    }

    @Test
    public void noRedirectShouldBeOK() throws Exception {

        givenAScenario()
                .withOk("http://www.example.com");

        RedirectChain redirectChain = sut.analyseRedirectChain(new URI("http://www.example.com"));

        assertThat(redirectChain.getNumOfRedirect(), is(0));
        assertThat(redirectChain.getDestinationURI(), equalTo(new URI("http://www.example.com")));
        assertThat(redirectChain.getLastStatus(), is(HttpStatus.OK));
    }

    @Test
    public void shouldFollowTheRedirectChain() throws Exception {

        givenAScenario()
                .withRedirect("http://www.example.com", HttpStatus.MOVED_PERMANENTLY, "http://www.example.com/hello")
                .withOk("http://www.example.com/hello");

        RedirectChain redirectChain = sut.analyseRedirectChain(new URI("http://www.example.com"));

        assertThat(redirectChain.getNumOfRedirect(), is(1));
        assertThat(redirectChain.getDestinationURI(), equalTo(new URI("http://www.example.com/hello")));
        assertThat(redirectChain.getLastStatus(), is(HttpStatus.OK));
    }

    @Test
    public void redirectLoopShouldMarkTheChainAsInvalid() throws Exception {
        givenAScenario()
                .withRedirect("http://www.example.com/1", HttpStatus.MOVED_PERMANENTLY, "http://www.example.com/2")
                .withRedirect("http://www.example.com/2", HttpStatus.MOVED_PERMANENTLY, "http://www.example.com/1");

        RedirectChain redirectChain = sut.analyseRedirectChain(new URI("http://www.example.com/1"));

        assertThat(redirectChain.isValid(), is(false));
        assertThat(redirectChain.getStatus(), is(RedirectChain.REDIRECT_LOOP));
    }

    @Test
    public void shouldRecognizeComplexRedirectLoop() throws Exception {
        givenAScenario()
                .withRedirect("http://www.example.com/1", HttpStatus.MOVED_PERMANENTLY, "http://www.example.com/2")
                .withRedirect("http://www.example.com/2", HttpStatus.MOVED_PERMANENTLY, "http://www.example.com/3")
                .withRedirect("http://www.example.com/3", HttpStatus.MOVED_PERMANENTLY, "http://www.example.com/4")
                .withRedirect("http://www.example.com/4", HttpStatus.MOVED_PERMANENTLY, "http://www.example.com/2");

        RedirectChain redirectChain = sut.analyseRedirectChain(new URI("http://www.example.com/1"));

        assertThat(redirectChain.isValid(), is(false));
        assertThat(redirectChain.getStatus(), is(RedirectChain.REDIRECT_LOOP));
    }

    @Test
    public void shouldMarkAsInvalidInCaseOfIOException() throws Exception {
        String errorMessage = "This is a test exception, everything is fine :)";
        givenAScenario()
                .withIOException("http://www.example.com", errorMessage);

        RedirectChain redirectChain = sut.analyseRedirectChain(new URI("http://www.example.com"));
        assertThat(redirectChain.isValid(), is(false));
        assertThat(redirectChain.getStatus(), containsString(errorMessage));
    }

    private MockHttpRequestFactory givenAScenario() {
        return mockHttpRequestFactory;
    }

}
