package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import com.github.salvatorenovelli.redirectcheck.model.RedirectAnalysisRequest;
import com.github.salvatorenovelli.redirectcheck.model.RedirectAnalysisResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

@EnableBinding(Processor.class)
class RedirectSpecificationProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RedirectSpecificationProcessor.class);

    @Autowired
    RedirectChainAnalyser analyser;


    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public RedirectAnalysisResponse handle(RedirectAnalysisRequest spec) {
        logger.info("Received spec {}", spec);
        final RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
        logger.info("Analysis of {} completed. Result: {}", spec, redirectChain);
        return new RedirectAnalysisResponse(spec, redirectChain);
    }
}
