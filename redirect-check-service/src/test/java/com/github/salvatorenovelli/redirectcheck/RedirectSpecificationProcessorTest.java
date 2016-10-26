package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectAnalysisRequest;
import com.github.salvatorenovelli.redirectcheck.model.RedirectAnalysisResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedirectSpecificationProcessorTest {


    public static final String EXAMPLE_URI = "http://example.com";
    public static final RedirectChain REDIRECT_CHAIN = new RedirectChain();
    @Mock RedirectChainAnalyser analyser;

    @InjectMocks RedirectSpecificationProcessor sut = new RedirectSpecificationProcessor();

    @Before
    public void setUp() throws Exception {
        when(analyser.analyseRedirectChain(EXAMPLE_URI)).thenReturn(REDIRECT_CHAIN);
    }

    @Test
    public void testHandle() throws Exception {

        RedirectAnalysisRequest spec = new RedirectAnalysisRequest(EXAMPLE_URI, "http://www.example.com");
        RedirectAnalysisResponse handle = sut.handle(spec);

        assertThat(handle.getRequest(), is(spec));
        assertThat(handle.getRedirectChain(), is(REDIRECT_CHAIN));
    }
}