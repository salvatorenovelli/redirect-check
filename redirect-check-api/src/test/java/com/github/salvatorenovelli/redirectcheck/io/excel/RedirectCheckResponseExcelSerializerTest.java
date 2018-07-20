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

import static com.github.salvatorenovelli.redirectcheck.io.RedirectSpecExcelParser.NON_PARALLEL;

public class RedirectCheckResponseExcelSerializerTest {

    private static final RedirectSpecification TEST_SPEC = RedirectSpecification.createValid(0, "http://destination0", "http://destination4", 200);
    public static final int FIRST_VALID_ROW_SKIPPING_HEADER = 1;
    public static final int CLEAN_REDIRECT_COLUMN = 7;


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldSerializaNonCleanRedirect() throws Exception {
        RedirectChain testChain = givenARedirectChainWithA302();
        String outFileName = whenWeSerializeIntoExcelWorkbook(testChain);
        assertThat(getWorkbookCleanRedirectResult(outFileName), is(false));
    }

    @Test
    public void shouldSerializaCleanRedirect() throws Exception {
        RedirectChain testChain = givenARedirectChainWithOnly301();
        String outFileName = whenWeSerializeIntoExcelWorkbook(testChain);
        assertThat(getWorkbookCleanRedirectResult(outFileName), is(true));
    }

    private boolean getWorkbookCleanRedirectResult(String outFileName) throws IOException, InvalidFormatException {
        Workbook sheets = WorkbookFactory.create(new FileInputStream(outFileName));
        Sheet firstVisibleSheet = getFirstVisibleSheet(sheets);
        firstVisibleSheet.getRow(1).getCell(7);
        return Boolean.parseBoolean(firstVisibleSheet.getRow(FIRST_VALID_ROW_SKIPPING_HEADER).getCell(CLEAN_REDIRECT_COLUMN).getStringCellValue());
    }

    private String whenWeSerializeIntoExcelWorkbook(RedirectChain testChain) throws IOException {
        String outFileName = getTempFilename();
        RedirectCheckResponseExcelSerializer redirectCheckResponseExcelSerializer = new RedirectCheckResponseExcelSerializer(outFileName);
        redirectCheckResponseExcelSerializer.addResponses(Arrays.asList(RedirectCheckResponse.createResponse(TEST_SPEC, testChain)));
        redirectCheckResponseExcelSerializer.write();
        return outFileName;
    }


    private RedirectChain givenARedirectChainWithOnly301() throws RedirectLoopException, URISyntaxException {

        RedirectChain testChain = new RedirectChain();

        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination2")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination3")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination4")));
        return testChain;
    }

    private RedirectChain givenARedirectChainWithA302() throws RedirectLoopException, URISyntaxException {

        RedirectChain testChain = new RedirectChain();

        testChain.addElement(new RedirectChainElement(301, new URI("http://destination1")));
        testChain.addElement(new RedirectChainElement(302, new URI("http://destination2")));
        testChain.addElement(new RedirectChainElement(301, new URI("http://destination3")));
        testChain.addElement(new RedirectChainElement(200, new URI("http://destination4")));
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
}