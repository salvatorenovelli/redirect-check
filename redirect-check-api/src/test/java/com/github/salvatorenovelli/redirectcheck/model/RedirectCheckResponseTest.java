package com.github.salvatorenovelli.redirectcheck.model;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;


public class RedirectCheckResponseTest {


    private RedirectChain testChain = new RedirectChain();
    private static final RedirectSpecification TEST_SPEC = RedirectSpecification.createValid(0, "http://www.example.com", "http://www.example.com/", 200);

    @Test
    public void getStatus() {
        testChain.markAsFailed("Test");
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, testChain);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString("Test"));
    }

    @Test
    public void unmatchingDestinationShouldBeReported() throws Exception {
        testChain.addElement(new RedirectChainElement(200, new URI("http://wrong-destination")));
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, testChain);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString(RedirectCheckResponse.DESTINATION_MISMATCH));
    }

    @Test
    public void unmatchingHttpStatusShouldBeReportedAsError() throws Exception {
        testChain.addElement(new RedirectChainElement(500, new URI(TEST_SPEC.getExpectedDestination())));
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, testChain);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString(RedirectCheckResponse.STATUS_CODE_MISMATCH));
    }

    @Test
    public void invalidDestingTionIsMarkedAsFailure() throws Exception {
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination")));
        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://www.example.com", "http://invalidDst invalid", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString(RedirectCheckResponse.DESTINATION_MISMATCH));

    }


    @Test
    public void shouldMarkCleanChainInCaseOfOnly301Redirects() throws Exception {
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination2")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination3")));


        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination3", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.SUCCESS));
        assertThat(response.isCleanRedirect(), is(true));
    }

    @Test
    public void shouldMarkNotCleanChainInCaseOfNon301Redirects() throws Exception {
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(302, new URI("http://destination2")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination3")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination4")));


        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.SUCCESS));
        assertThat(response.isCleanRedirect(), is(false));
    }

    @Test
    public void shouldMarkCleanIfNoRedirect() throws Exception {
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination1")));

        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination1", 200);
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.SUCCESS));
        assertThat(response.isCleanRedirect(), is(true));
    }

    @Test
    public void itShouldEvaluateCleanChainEvenWhenOtherFailuresOccur() throws Exception {

        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination2")));

        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);

        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.isCleanRedirect(), is(true));

    }

    @Test
    public void shouldEvaluateRedirectChainProperly() throws Exception {
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(302, new URI("http://destination2")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination3")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination4")));


        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, testChain);


        assertThat(response.getHttpStatusChain(), Matchers.contains(301, 302, 301, 200));
    }
}