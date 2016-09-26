package snove.seo.redirectcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

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

    private static final Logger logger = LoggerFactory.getLogger(RedirectSpecificationProcessor.class);

    @Autowired
    RedirectChainAnalyser analyser;


    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public RedirectChain handle(RedirectSpecification spec) {

        logger.info("Received spec {}", spec);
        final RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
        logger.info("Analysis of {} completed. Result: {}", spec, redirectChain);

        return redirectChain;
    }
}
