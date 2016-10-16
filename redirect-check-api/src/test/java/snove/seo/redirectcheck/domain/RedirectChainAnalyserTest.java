package snove.seo.redirectcheck.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.apache.http.HttpStatus;

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
        RedirectChain redirectChain = sut.analyseRedirectChain("http://www.example.com");

        assertThat(redirectChain.isFailed(), is(false));
    }

    @Test
    public void noRedirectShouldBeOK() throws Exception {

        givenAScenario()
                .withOk("http://www.example.com");

        RedirectChain redirectChain = sut.analyseRedirectChain("http://www.example.com");

        assertThat(redirectChain.getNumOfRedirect(), is(0));
        assertThat(redirectChain.getDestinationURI(), equalTo("http://www.example.com"));
        assertThat(redirectChain.getLastHttpStatus(), is(HttpStatus.SC_OK));
    }

    @Test
    public void shouldFollowTheRedirectChain() throws Exception {

        givenAScenario()
                .withRedirect("http://www.example.com", HttpStatus.SC_MOVED_PERMANENTLY, "http://www.example.com/hello")
                .withOk("http://www.example.com/hello");

        RedirectChain redirectChain = sut.analyseRedirectChain("http://www.example.com");

        assertThat(redirectChain.getNumOfRedirect(), is(1));
        assertThat(redirectChain.getDestinationURI(), equalTo("http://www.example.com/hello"));
        assertThat(redirectChain.getLastHttpStatus(), is(HttpStatus.SC_OK));
    }

    @Test
    public void redirectLoopShouldMarkTheChainAsInvalid() throws Exception {
        givenAScenario()
                .withRedirect("http://www.example.com/1", HttpStatus.SC_MOVED_PERMANENTLY, "http://www.example.com/2")
                .withRedirect("http://www.example.com/2", HttpStatus.SC_MOVED_PERMANENTLY, "http://www.example.com/1");

        RedirectChain redirectChain = sut.analyseRedirectChain("http://www.example.com/1");

        assertThat(redirectChain.isFailed(), is(true));
        assertThat(redirectChain.getStatus(), containsString(RedirectChain.REDIRECT_LOOP));
    }

    @Test
    public void shouldRecognizeComplexRedirectLoop() throws Exception {
        givenAScenario()
                .withRedirect("http://www.example.com/1", HttpStatus.SC_MOVED_PERMANENTLY, "http://www.example.com/2")
                .withRedirect("http://www.example.com/2", HttpStatus.SC_MOVED_PERMANENTLY, "http://www.example.com/3")
                .withRedirect("http://www.example.com/3", HttpStatus.SC_MOVED_PERMANENTLY, "http://www.example.com/4")
                .withRedirect("http://www.example.com/4", HttpStatus.SC_MOVED_PERMANENTLY, "http://www.example.com/2");

        RedirectChain redirectChain = sut.analyseRedirectChain("http://www.example.com/1");

        assertThat(redirectChain.isFailed(), is(true));
        assertThat(redirectChain.getStatus(), containsString(RedirectChain.REDIRECT_LOOP));
    }

    @Test
    public void shouldMarkAsInvalidInCaseOfIOException() throws Exception {
        String errorMessage = "This is a test exception, everything is fine :)";
        givenAScenario()
                .withIOException("http://www.example.com", errorMessage);

        RedirectChain redirectChain = sut.analyseRedirectChain("http://www.example.com");
        assertThat(redirectChain.isFailed(), is(true));
        assertThat(redirectChain.getStatus(), containsString(errorMessage));
    }

    private MockHttpRequestFactory givenAScenario() {
        return mockHttpRequestFactory;
    }

}
