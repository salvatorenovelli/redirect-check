package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.domain.DefaultRedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.salvatorenovelli.redirectcheck.http.DefaultHttpRequestFactory;

@Configuration
public class ApplicationConfiguration {

    @Bean
    RedirectChainAnalyser getRedirectChainAnalyser(){
        return new DefaultRedirectChainAnalyser(new DefaultHttpRequestFactory());
    }
}
