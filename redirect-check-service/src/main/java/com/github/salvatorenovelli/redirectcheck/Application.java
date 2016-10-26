package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

import com.github.salvatorenovelli.redirectcheck.model.RedirectAnalysisRequest;
import com.github.salvatorenovelli.redirectcheck.model.RedirectAnalysisResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

