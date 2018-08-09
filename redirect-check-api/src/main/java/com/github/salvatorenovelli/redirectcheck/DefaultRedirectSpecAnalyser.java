package com.github.salvatorenovelli.redirectcheck;

import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.cli.ProgressMonitor;
import com.github.salvatorenovelli.redirectcheck.domain.RedirectChainAnalyser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

@Slf4j
public class DefaultRedirectSpecAnalyser implements RedirectSpecAnalyser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRedirectSpecAnalyser.class);
    private final RedirectChainAnalyser analyser;
    private final ProgressMonitor progressMonitor;
    private final ExecutorService executorService;
    private final RedirectCheckResponseFactory redirectCheckResponseFactory;
    private final long timeoutMillis;

    public DefaultRedirectSpecAnalyser(RedirectChainAnalyser redirectChainAnalyser,
                                       RedirectCheckResponseFactory redirectCheckResponseFactory,
                                       ProgressMonitor progressMonitor, ExecutorService executorService, long analysisTimeout) {
        this.analyser = redirectChainAnalyser;
        this.redirectCheckResponseFactory = redirectCheckResponseFactory;
        this.progressMonitor = progressMonitor;
        this.executorService = executorService;
        this.timeoutMillis = analysisTimeout;
    }

    @Override
    public RedirectCheckResponse checkRedirect(RedirectSpecification spec) {

        try {
            return checkRedirectWithTimeout(spec, timeoutMillis);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.warn("Exception while analysing spec", e);
            return RedirectCheckResponse.createResponseForError("Exception while checking url:" + e, spec);
        } catch (Throwable e) {
            return redirectCheckResponseFactory.createResponseForError(e.getMessage(), spec);
        } finally {
            progressMonitor.tick();
        }
    }

    private RedirectCheckResponse checkRedirectWithTimeout(RedirectSpecification spec, long timeoutMillis) throws InterruptedException, ExecutionException, TimeoutException {
        return CompletableFuture.supplyAsync(() -> execute(spec), executorService).get(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    private RedirectCheckResponse execute(RedirectSpecification spec) {
        logger.debug("Analysing " + spec);
        if (spec.isValid()) {
            RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
            return redirectCheckResponseFactory.createResponse(spec, redirectChain);
        } else {
            return redirectCheckResponseFactory.createResponseForError(spec.getErrorMessage(), spec);
        }
    }
}
