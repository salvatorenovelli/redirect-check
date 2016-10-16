package com.snovelli.http;

import com.snovelli.model.HttpClientRequestFactory;
import com.snovelli.model.RedirectChain;
import com.snovelli.seo.redirect.RedirectChainAnalyser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.URISyntaxException;

import static com.snovelli.seo.redirectcheck.testutils.MockHttpClientRequestFactoryBuilder.mockRequestFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class DefaultRedirectChainAnalyserTest {

    RedirectChainAnalyser sut;


    @Before
    public void init() throws URISyntaxException {
        HttpClientRequestFactory requestFactory = mockRequestFactory()
                .withRedirect("http://www.fedesarda.it/it", HttpStatus.MOVED_PERMANENTLY, "http://www.fedesarda.it/it/")
                .withOk("http://www.fedesarda.it/it/").build();
        sut = new RedirectChainAnalyser(requestFactory);
    }


    @Test
    public void testCheckRedirect() throws Exception {
        RedirectChain redirectChain = sut.analyseRedirectChain("http://www.fedesarda.it/it");
        assertThat(redirectChain.getNumOfRedirect(), is(1));
        assertThat(redirectChain.getDestinationURI(), equalTo("http://www.fedesarda.it/it/"));
    }


}