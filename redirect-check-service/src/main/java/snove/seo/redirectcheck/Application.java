package snove.seo.redirectcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;

import snove.seo.redirectcheck.domain.RedirectChainAnalyser;
import snove.seo.redirectcheck.model.RedirectChain;
import snove.seo.redirectcheck.model.RedirectSpecification;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@EnableBinding(Processor.class)
class RedirectSpecificationProcessor {

    @Autowired
    RedirectChainAnalyser analyser;


    @StreamListener(Processor.INPUT)
    public void handle(RedirectSpecification spec) {

        final RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());

        System.out.println("Received spec: " + spec);
    }
}
