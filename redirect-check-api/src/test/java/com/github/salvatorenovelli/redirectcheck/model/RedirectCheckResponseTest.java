package com.github.salvatorenovelli.redirectcheck.model;

import com.github.salvatorenovelli.redirectcheck.model.exception.RedirectLoopException;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse.DESTINATION_MISMATCH;
import static com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse.NON_PERMANENT_REDIRECT;
import static com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse.STATUS_CODE_MISMATCH;
import static com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse.Status.FAILURE;
import static com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse.Status.SUCCESS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;


public class RedirectCheckResponseTest {


    public static final int EXPECTED_STATUS_CODE = 200;
    private RedirectChain testChain = new RedirectChain();
    private static final RedirectSpecification TEST_SPEC = RedirectSpecification.createValid(0, "http://www.example.com", "http://www.example.com/", 200);

    @Test
    public void getStatus() {
        testChain.markAsFailed("Test");
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, testChain);
        assertThat(response.getStatus(), is(FAILURE));
        assertThat(response.getStatusMessage(), containsString("Test"));
    }

    @Test
    public void destinationMismatchIsMarkedAsFailure() throws Exception {
        testChain.addElement(new RedirectChainElement(200, new URI("http://wrong-destination")));
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, testChain);
        assertThat(response.getStatus(), is(FAILURE));
        assertThat(response.isDestinationMatch(), is(false));
    }

    @Test
    public void statusCodeMismatchIsMarkedAsFailure() throws Exception {
        testChain.addElement(new RedirectChainElement(500, new URI(TEST_SPEC.getExpectedDestination())));
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, testChain);
        assertThat(response.getStatus(), is(FAILURE));
        assertThat(response.isStatusCodeMatch(), is(false));
    }


    @Test
    public void nonPermanentRedirectIsMarkedAsFailure() throws Exception {

        givenADirtyRedirectChain();

        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(FAILURE));
        assertThat(response.isPermanentRedirect(), is(false));
    }

    @Test
    public void shouldMarkCleanChainInCaseOfOnly301Redirects() throws Exception {
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination2")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination3")));


        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination3", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(SUCCESS));
        assertThat(response.isPermanentRedirect(), is(true));
    }

    @Test
    public void shouldMarkCleanIfNoRedirect() throws Exception {
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination1")));

        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination1", 200);
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(SUCCESS));
        assertThat(response.isPermanentRedirect(), is(true));
    }

    @Test
    public void itShouldEvaluateCleanChainEvenWhenOtherFailuresOccur() throws Exception {

        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination2")));

        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(FAILURE));
        assertThat(response.isPermanentRedirect(), is(true));

    }

    @Test
    public void shouldEvaluateRedirectChainProperly() throws Exception {

        givenADirtyRedirectChain();

        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(FAILURE));
    }


    @Test
    public void shouldDecodeRedirectLocationAndConsiderItMatch() throws Exception {

        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://ετικέτα", "http://προϊόντα", EXPECTED_STATUS_CODE);

        testChain.addElement(new RedirectChainElement(301, new URI("http://ετικέτα")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://%cf%80%cf%81%ce%bf%cf%8a%cf%8c%ce%bd%cf%84%ce%b1")));

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(SUCCESS));

    }


    private void givenADirtyRedirectChain() throws RedirectLoopException, URISyntaxException {
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(302, new URI("http://destination2")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination3")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination4")));
    }
}