package com.github.salvatorenovelli.redirectcheck.io.excel;

import com.github.salvatorenovelli.redirectcheck.model.RedirectChain;
import com.github.salvatorenovelli.redirectcheck.model.RedirectChainElement;
import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import com.github.salvatorenovelli.redirectcheck.model.exception.RedirectLoopException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RedirectCheckResponseExcelSerializerTest {

    private static final RedirectSpecification TEST_SPEC = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);
    public static final int FIRST_VALID_ROW_SKIPPING_HEADER = 1;
    public static final int CLEAN_REDIRECT_COLUMN = 7;
    public static final int REDIRECT_CHAIN_COLUMN = 8;


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private int curDestNumber = 0;

    @Test
    public void shouldSerializeNonCleanRedirect() throws Exception {
        RedirectChain testChain = givenARedirectChainWithA302();
        String outFileName = whenWeSerializeIntoExcelWorkbook(testChain);
        assertThat(getWorkbookCleanRedirectResultField(outFileName), is(false));
    }

    @Test
    public void shouldSerializeCleanRedirect() throws Exception {
        RedirectChain testChain = givenARedirectChainWithOnly301();
        String outFileName = whenWeSerializeIntoExcelWorkbook(testChain);
        assertThat(getWorkbookCleanRedirectResultField(outFileName), is(true));
    }

    @Test
    public void shouldSerializeRedirectHttpStatusChain() throws Exception {
        RedirectChain testChain = givenARedirectChainWith(301, 301, 302, 200);
        String outFileName = whenWeSerializeIntoExcelWorkbook(testChain);
        assertThat(getWorkbookRedirectChainField(outFileName), is("301, 301, 302, 200"));
    }

    private RedirectChain givenARedirectChainWithOnly301() {
        return givenARedirectChainWith(301, 301, 301, 200);
    }

    private RedirectChain givenARedirectChainWithA302() {
        return givenARedirectChainWith(301, 301, 302, 200);
    }

    private RedirectChain givenARedirectChainWith(int... redirects) {

        RedirectChain testChain = new RedirectChain();
        Arrays.stream(redirects)
                .mapToObj(this::toRedirectChainElement)
                .forEach(redirectChainElement -> addToChain(testChain, redirectChainElement));

        return testChain;
    }

    private String getTempFilename() throws IOException {
        return temporaryFolder.getRoot().toPath().resolve(temporaryFolder.newFile().getName() + ".xls").toString();
    }

    private Sheet getFirstVisibleSheet(Workbook wb) {
        final Optional<Sheet> first = StreamSupport.stream(wb.spliterator(), false)
                .filter(sheet -> !(wb.isSheetHidden(wb.getSheetIndex(sheet)) || wb.isSheetVeryHidden(wb.getSheetIndex(sheet))))
                .findFirst();
        //A workbook without a visible sheet is impossible to create with Microsoft Excel or via APIs (but I'll leave the check there just in case I'm missing something)
        return first.orElseThrow(() -> new RuntimeException("The workbook looks empty!"));
    }

    private void addToChain(RedirectChain testChain, RedirectChainElement redirectChainElement) {
        try {
            testChain.addElement(redirectChainElement);
        } catch (RedirectLoopException e) {
            throw new RuntimeException(e);
        }
    }

    private RedirectChainElement toRedirectChainElement(int status) {
        try {
            return new RedirectChainElement(status, new URI("http://destination" + curDestNumber++));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean getWorkbookCleanRedirectResultField(String outFileName) throws IOException, InvalidFormatException {
        Workbook sheets = WorkbookFactory.create(new FileInputStream(outFileName));
        Sheet firstVisibleSheet = getFirstVisibleSheet(sheets);
        return Boolean.parseBoolean(firstVisibleSheet.getRow(FIRST_VALID_ROW_SKIPPING_HEADER).getCell(CLEAN_REDIRECT_COLUMN).getStringCellValue());
    }

    private String getWorkbookRedirectChainField(String outFileName) throws IOException, InvalidFormatException {
        Workbook sheets = WorkbookFactory.create(new FileInputStream(outFileName));
        Sheet firstVisibleSheet = getFirstVisibleSheet(sheets);
        return firstVisibleSheet.getRow(FIRST_VALID_ROW_SKIPPING_HEADER).getCell(REDIRECT_CHAIN_COLUMN).getStringCellValue();
    }

    private String whenWeSerializeIntoExcelWorkbook(RedirectChain testChain) throws IOException {
        String outFileName = getTempFilename();
        RedirectCheckResponseExcelSerializer redirectCheckResponseExcelSerializer = new RedirectCheckResponseExcelSerializer(outFileName);
        redirectCheckResponseExcelSerializer.addResponses(Arrays.asList(RedirectCheckResponse.createResponse(TEST_SPEC, testChain)));
        redirectCheckResponseExcelSerializer.write();
        return outFileName;
    }
}