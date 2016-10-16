package com.snovelli;

import com.snovelli.http.DefaultHttpConnectorFactory;
import com.snovelli.model.RedirectChain;
import com.snovelli.model.RedirectCheckResponse;
import com.snovelli.model.RedirectSpecification;
import com.snovelli.seo.redirect.RedirectChainAnalyser;
import com.snovelli.seo.redirect.RedirectSpecificationCSVReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private static final int NUM_WORKERS = 50;
    private final RedirectChainAnalyser analyser;
    private final String filename;
    private final FileWriter csvOutput;


    private Application(String sourceFilename) throws IOException {
        this.filename = sourceFilename;
        this.csvOutput = new FileWriter(new File(sourceFilename + "_out.csv"));
        csvOutput.append(csvHeader() + "\n");

        this.analyser = new RedirectChainAnalyser(new DefaultHttpConnectorFactory());
    }

    public static void main(String[] args) throws IOException {


        if (args.length < 1) {
            printUsage();
        }

        setUncaughtExceptionHandler();

        long start = System.currentTimeMillis();

        Application application = new Application(args[0]);
        application.setNumWorkers(NUM_WORKERS);

        try {

            logger.info("Running analysis... (this may take several minutes)");
            application.runAnalysis();
            long elapsedTime = (System.currentTimeMillis() - start) / 1000;
            System.out.println("Analysis complete in " + elapsedTime + " secs. :)");

        } catch (Throwable e) {
            logger.error("Error while running analysis", e);
        } finally {
            pressKey();
        }


    }

    private static void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.warn("Uncaught exception:", e));
    }

    private static void printUsage() {
        System.err.println("Please specify CSV filename");
        pressKey();
        System.exit(1);
    }

    private static void pressKey() {
        try {
            System.out.println("Press any key to exit...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runAnalysis() throws IOException {
        List<RedirectCheckResponse> responses = analyseRedirectsInCSV(filename);
        responses.forEach(this::tocsv);
        csvOutput.close();

    }

    private void setNumWorkers(int i) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(i));
    }

    private List<RedirectCheckResponse> analyseRedirectsInCSV(String filePath) throws IOException {
        List<RedirectSpecification> specs = RedirectSpecificationCSVReader.parse(Paths.get(filePath));
        return specs.parallelStream().map(this::checkRedirect).collect(Collectors.toList());
    }


    private String csvHeader() {
        return "SourceURI, RESULT, ResultReason, Expected URI, Actual URI, Last HTTP Status";
    }

    private void tocsv(RedirectCheckResponse cr) {
        List<String> fields = Arrays.asList(
                cr.getSourceURI().toString(),
                cr.getStatus().toString(),
                cr.getStatusMessage(),
                cr.getExpectedDestinationURI().toString(),
                cr.getActualDestinationURI() != null ? cr.getActualDestinationURI().toString() : "n/a",
                cr.getLastHttpStatus() != null ? cr.getLastHttpStatus().toString() : "n/a"
        );

        fields.forEach(this::appendToCSVOutput);
        appendToCSVFile("\n");
    }

    private void appendToCSVOutput(String field) {
        appendToCSVFile(field + ",");
    }

    private void appendToCSVFile(String field) {
        try {
            csvOutput.append(field);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private RedirectCheckResponse checkRedirect(RedirectSpecification spec) {
        logger.debug("Analysing " + spec);
        RedirectChain redirectChain = analyser.analyseRedirectChain(spec.getSourceURI());
        return new RedirectCheckResponse(spec, redirectChain);
    }
}
