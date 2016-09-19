package snove.seo.redirectcheck;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import snove.seo.redirectcheck.domain.RedirectChainAnalyser;
import snove.seo.redirectcheck.http.DefaultHttpRequestFactory;

@Configuration
public class ApplicationConfiguration {

    @Bean
    RedirectChainAnalyser getRedirectChainAnalyser(){
        return new RedirectChainAnalyser(new DefaultHttpRequestFactory());
    }
}
