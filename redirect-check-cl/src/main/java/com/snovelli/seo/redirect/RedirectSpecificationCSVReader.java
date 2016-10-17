package com.snovelli.seo.redirect;

import com.snovelli.model.RedirectSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RedirectSpecificationCSVReader {


    private static final String[] CHARSETS = new String[]{"ISO-8859-1", "UTF-8", "US-ASCII", "UTF-16BE", "UTF-16LE", "UTF-16"};

    private static final RedirectSpecification INVALID_URI_SPEC = null;
    private static Logger logger = LoggerFactory.getLogger(RedirectSpecificationCSVReader.class);
    private static int currentLine = 0;

    public static List<RedirectSpecification> parse(Path csvFile) throws IOException {


        try {
            for (String charset : CHARSETS) {
                try {
                    logger.debug("Attempting decoding using charset: " + charset);
                    return Files.lines(csvFile, Charset.forName(charset))
                            .map(s -> s.split(","))
                            .map(toRedirectSpecification())
                            .filter(isValid())
                            .collect(Collectors.toList());
                } catch (MalformedInputException e) {
                    logger.debug("Unable to decode using charset:" + charset);
                }
            }
        } catch (Exception e) {
            logger.error("Unable to complete analysis because: " + e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    private static Function<String[], RedirectSpecification> toRedirectSpecification() {


        return strings -> {
            currentLine++;
            if (strings.length > 1) {
                return new RedirectSpecification(strings[0], strings[1]);
            } else {
                if (strings.length > 0) {
                    logger.warn("Missing expected url in line: {} ", currentLine);
                }
            }
            return INVALID_URI_SPEC;
        };
    }

    private static Predicate<RedirectSpecification> isValid() {
        return redirectSpecification -> redirectSpecification != INVALID_URI_SPEC;
    }
}
