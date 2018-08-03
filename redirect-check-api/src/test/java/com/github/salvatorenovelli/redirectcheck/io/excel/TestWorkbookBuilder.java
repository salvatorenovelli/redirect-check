package com.github.salvatorenovelli.redirectcheck.io.excel;

import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChainElement;
import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.model.exception.RedirectLoopException;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

class TestWorkbookBuilder {

    private RedirectChain curRedirectChain;
    private RedirectSpecification spec = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);
    private final TemporaryFolder temporaryFolder;
    private int curDestNumber = 0;

    TestWorkbookBuilder(TemporaryFolder temporaryFolder) {
        this.temporaryFolder = temporaryFolder;
    }

    TestWorkbookBuilder withARedirectChainContaining302() {
        curRedirectChain = createRedirectChain(301, 301, 302, 200);
        return this;
    }

    TestWorkbookBuilder withARedirectChainContainingOnly301() {
        curRedirectChain = createRedirectChain(301, 301, 301, 200);
        return this;
    }

    TestWorkbookBuilder withARedirectChainWith(int... statusCodes) {
        curRedirectChain = createRedirectChain(statusCodes);
        return this;
    }


    TestWorkbookBuilder withDestinationMatch(boolean match) {

        curRedirectChain = createRedirectChain(match);

        return this;
    }

    String serialize() throws IOException {
        return serializeWorkbook(curRedirectChain, spec);
    }

    private String serializeWorkbook(RedirectChain testChain, RedirectSpecification spec) throws IOException {
        String outFileName = getTempFilename();
        RedirectCheckResponseExcelSerializer redirectCheckResponseExcelSerializer = new RedirectCheckResponseExcelSerializer(outFileName);
        redirectCheckResponseExcelSerializer.addResponses(Collections.singletonList(RedirectCheckResponse.createResponse(spec, testChain)));
        redirectCheckResponseExcelSerializer.write();
        return outFileName;
    }

    private RedirectChain createRedirectChain(boolean destinationMatch) {

        this.spec = RedirectSpecification.createValid(0, "http://destination0", "http://destination1" + (destinationMatch ? "" : "wrong"), 200);

        RedirectChain testChain = new RedirectChain();
        Arrays.stream(new int[]{301, 200})
                .mapToObj(this::toRedirectChainElement)
                .forEach(redirectChainElement -> addToChain(testChain, redirectChainElement));

        return testChain;
    }

    private RedirectChain createRedirectChain(int... redirects) {

        this.spec = RedirectSpecification.createValid(0, "http://destination0", "http://destination" + (redirects.length - 1), 200);

        RedirectChain testChain = new RedirectChain();
        Arrays.stream(redirects)
                .mapToObj(this::toRedirectChainElement)
                .forEach(redirectChainElement -> addToChain(testChain, redirectChainElement));

        return testChain;
    }

    private RedirectChainElement toRedirectChainElement(int status) {
        try {
            return new RedirectChainElement(status, new URI("http://destination" + curDestNumber++));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToChain(RedirectChain testChain, RedirectChainElement redirectChainElement) {
        try {
            testChain.addElement(redirectChainElement);
        } catch (RedirectLoopException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTempFilename() throws IOException {

        return temporaryFolder.getRoot().toPath().resolve(temporaryFolder.newFile().getName() + ".xls").toString();
    }
}
