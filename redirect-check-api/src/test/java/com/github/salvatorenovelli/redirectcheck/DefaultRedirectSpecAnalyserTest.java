package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.cli.ProgressMonitor;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChainElement;
import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import static com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse.Status.FAILURE;
import static com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse.Status.SUCCESS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class DefaultRedirectSpecAnalyserTest {


    private static final RedirectSpecification TEST_SPEC = RedirectSpecification.createValid(0, "http://some-uri", "http://correct-dest", 200);
    private static final int TEST_ANALYSIS_TIMEOUT = 200;
    private RedirectChain testChain = new RedirectChain();

    DefaultRedirectSpecAnalyser sut;

    @Mock private RedirectChainAnalyser analyser;
    @Mock private ProgressMonitor mockProgressMonitor;

    @Before
    public void setUp() throws Exception {


        sut = new DefaultRedirectSpecAnalyser(
                analyser,
                new RedirectCheckResponseFactory(),
                mockProgressMonitor,
                Executors.newSingleThreadExecutor(), TEST_ANALYSIS_TIMEOUT);

        when(analyser.analyseRedirectChain(TEST_SPEC.getSourceURI())).thenReturn(testChain);
    }

    @Test
    public void shouldCheckRedirect() throws Exception {

        testChain.addElement(new RedirectChainElement(200, new URI("http://correct-dest")));

        RedirectCheckResponse redirectCheckResponse = sut.checkRedirect(TEST_SPEC);

        assertThat(redirectCheckResponse.getStatus(), is(SUCCESS));
        assertThat(redirectCheckResponse.isDestinationMatch(), is(true));
        assertThat(redirectCheckResponse.getActualDestinationURI(), is("http://correct-dest"));

    }

    @Test(timeout = 1000)
    public void shouldTimeoutIfAnalyserTakesTooLong() {
        when(analyser.analyseRedirectChain(any())).then(invocationOnMock -> {
            Thread.sleep(5000);
            fail("Execution should never reach here. Execution should timeout after " + TEST_ANALYSIS_TIMEOUT + " milliseconds");
            return null;
        });

        RedirectCheckResponse redirectCheckResponse = sut.checkRedirect(TEST_SPEC);

        assertThat(redirectCheckResponse.getStatus(), is(FAILURE));
        assertThat(redirectCheckResponse.getStatusMessage(), is("Exception while checking url:java.util.concurrent.TimeoutException"));
    }

    @Test(timeout = 1000)
    public void exceptionAreHandledGracefully() {
        when(analyser.analyseRedirectChain(any())).then(invocationOnMock -> {
            throw new RuntimeException("This should be reported in status message");
        });

        RedirectCheckResponse redirectCheckResponse = sut.checkRedirect(TEST_SPEC);

        assertThat(redirectCheckResponse.getStatus(), is(FAILURE));
        assertThat(redirectCheckResponse.getStatusMessage(), is("Exception while checking url:java.util.concurrent.ExecutionException: java.lang.RuntimeException: This should be reported in status message"));
    }

    @Test(timeout = 1000)
    public void progressMonitorIsInvokedEvenWhenExceptionsAreThrown() {
        when(analyser.analyseRedirectChain(any())).then(invocationOnMock -> {
            throw new RuntimeException("This should be reported in status message");
        });

        sut.checkRedirect(TEST_SPEC);

        verify(mockProgressMonitor).tick();
    }

    @Test
    public void invalidSpecIsReportedAsError() {
        RedirectCheckResponse redirectCheckResponse = sut.checkRedirect(RedirectSpecification.createInvalid(10, "This is an invalid spec"));

        assertThat(redirectCheckResponse.getStatus(), is(FAILURE));
        assertThat(redirectCheckResponse.getStatusMessage(), is("This is an invalid spec"));
    }


    @Test(timeout = 1000)
    public void invalidUrlShouldReportAllFlagAsFalse() {

        when(analyser.analyseRedirectChain(any())).then(invocationOnMock -> {
            throw new URISyntaxException("Input", "Reason");
        });

        RedirectCheckResponse redirectCheckResponse = sut.checkRedirect(TEST_SPEC);

        assertThat(redirectCheckResponse.isDestinationMatch(), is(false));
        assertThat(redirectCheckResponse.isStatusCodeMatch(), is(false));
        assertThat(redirectCheckResponse.isPermanentRedirect(), is(false));
    }

}