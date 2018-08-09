package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.domain.DefaultRedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.http.DefaultHttpConnectorFactory;
import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class IntegrationTestStuff {


    public void checkRedirect() {
        DefaultRedirectSpecAnalyser analyser = new DefaultRedirectSpecAnalyser(
                new DefaultRedirectChainAnalyser(new DefaultHttpConnectorFactory()), new RedirectCheckResponseFactory(),
                () -> {},
                Executors.newFixedThreadPool(1), 10000);

        RedirectCheckResponse redirectCheckResponse = analyser.checkRedirect(RedirectSpecification.createValid(0,
                "",
                "", 200));


        fail("This is just to test stuff... nothing to see here");


    }
}