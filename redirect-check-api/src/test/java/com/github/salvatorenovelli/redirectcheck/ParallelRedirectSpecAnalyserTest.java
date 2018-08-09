package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParallelRedirectSpecAnalyserTest {

    private static final List<RedirectSpecification> EMPTY_SPECS = Collections.emptyList();
    private static final int NUM_WORKERS = 10;
    ParallelRedirectSpecAnalyser sut;
    @Mock private RedirectSpecAnalyser analyser;
    private List<RedirectCheckResponse> expectedResponses = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        sut = new ParallelRedirectSpecAnalyser(NUM_WORKERS, analyser);
    }

    @Test
    public void testRunAnalysis() throws Exception {
        List<RedirectCheckResponse> responseList = sut.runParallelAnalysis(EMPTY_SPECS);
        assertNotNull(responseList);
        assertThat(responseList, hasSize(0));
    }

    @Test
    public void analysisShouldWrapResponse() throws Exception {
        List<RedirectCheckResponse> redirectCheckResponses = sut.runParallelAnalysis(createTestSpecWithSize(1));
        assertThat(redirectCheckResponses, hasSize(1));
        assertThat(redirectCheckResponses.get(0), is(expectedResponses.get(0)));
    }

    @Test
    public void multipleSpecShouldBeAllProcessed() throws Exception {
        List<RedirectCheckResponse> redirectCheckResponses = sut.runParallelAnalysis(createTestSpecWithSize(10));
        assertThat(redirectCheckResponses, hasSize(10));

        for (int i = 0; i < redirectCheckResponses.size(); i++) {
            assertThat(redirectCheckResponses.get(i), is(expectedResponses.get(i)));
        }
    }

    @Test(timeout = 5000)
    public void multipleSpecsShouldBeAnalysedInParallel() throws Exception {
        int GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS = Runtime.getRuntime().availableProcessors() + 5;

        CountDownLatch latch = new CountDownLatch(GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS);

        sut = new ParallelRedirectSpecAnalyser(GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS, s -> {
            awaitForOtherThreads(latch);
            return mock(RedirectCheckResponse.class);
        });

        sut.runParallelAnalysis(createTestSpecWithSize(GREATER_THAN_SYSTEM_DEFAULT_NUM_WORKERS));

    }

    private void awaitForOtherThreads(CountDownLatch latch) {
        try {
            latch.countDown();
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<RedirectSpecification> createTestSpecWithSize(int size) {

        List<RedirectSpecification> spec = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            RedirectSpecification curSpec = RedirectSpecification.createValid(0, "http://www.example.com/" + i, "http://www.example.com/" + i + "/dst", 200);
            RedirectCheckResponse curChainResponse = mock(RedirectCheckResponse.class);
            when(analyser.checkRedirect(curSpec)).thenReturn(curChainResponse);
            spec.add(curSpec);
            expectedResponses.add(curChainResponse);
        }

        return spec;
    }
}