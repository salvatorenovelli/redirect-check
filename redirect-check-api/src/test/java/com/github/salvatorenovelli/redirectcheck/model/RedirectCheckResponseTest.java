package com.github.salvatorenovelli.redirectcheck.model;

import org.junit.Test;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;


public class RedirectCheckResponseTest {


    public static final RedirectChain REDIRECT_CHAIN = new RedirectChain();
    public static final RedirectSpecification TEST_SPEC = RedirectSpecification.createValid(0, "http://www.example.com", "http://www.example.com/", 200);

    @Test
    public void getStatus() throws Exception {
        REDIRECT_CHAIN.markAsFailed("Test");
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, REDIRECT_CHAIN);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString("Test"));
    }

    @Test
    public void unmatchingDestinationShouldBeReported() throws Exception {
        REDIRECT_CHAIN.addElement(new RedirectChainElement(200, new URI("http://wrong-destination")));
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, REDIRECT_CHAIN);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString(RedirectCheckResponse.DESTINATION_MISMATCH));
    }

    @Test
    public void unmatchingHttpStatusShouldBeReportedAsError() throws Exception {
        REDIRECT_CHAIN.addElement(new RedirectChainElement(500, new URI(TEST_SPEC.getExpectedDestination())));
        RedirectCheckResponse response = RedirectCheckResponse.createResponse(TEST_SPEC, REDIRECT_CHAIN);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString(RedirectCheckResponse.STATUS_CODE_MISMATCH));
    }

    @Test
    public void invalidDestingTionIsMarkedAsFailure() throws Exception {
        REDIRECT_CHAIN.addElement(new RedirectChainElement(200, new URI("http://destination")));
        RedirectSpecification specWithInvalidDestination = RedirectSpecification.createValid(0, "http://www.example.com", "http://invalidDst invalid", 200);

        RedirectCheckResponse response = RedirectCheckResponse.createResponse(specWithInvalidDestination, REDIRECT_CHAIN);
        assertThat(response.getStatus(), is(RedirectCheckResponse.Status.FAILURE));
        assertThat(response.getStatusMessage(), containsString(RedirectCheckResponse.DESTINATION_MISMATCH));

    }
}