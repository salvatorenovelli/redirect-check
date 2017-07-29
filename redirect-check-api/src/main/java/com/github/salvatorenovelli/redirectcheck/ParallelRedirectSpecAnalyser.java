package com.github.salvatorenovelli.redirectcheck;


import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ParallelRedirectSpecAnalyser {

    private final int numWorkers;
    private final RedirectSpecAnalyser analyser;

    public ParallelRedirectSpecAnalyser(int numWorkers, RedirectSpecAnalyser analyser) {
        this.numWorkers = numWorkers;
        this.analyser = analyser;
    }

    public List<RedirectCheckResponse> runParallelAnalysis(List<RedirectSpecification> redirectCheckSpecs) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numWorkers);
        try {
            List<CompletableFuture<RedirectCheckResponse>> collect = redirectCheckSpecs.stream()
                    .map(spec -> CompletableFuture.supplyAsync(() -> analyser.checkRedirect(spec), executorService))
                    .collect(Collectors.toList());

            return collect.stream().map(CompletableFuture::join).collect(Collectors.toList());
        } finally {
            executorService.shutdownNow();
        }
    }

}

