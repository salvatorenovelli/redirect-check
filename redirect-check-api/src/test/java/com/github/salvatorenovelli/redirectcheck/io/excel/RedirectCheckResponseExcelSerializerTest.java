package com.github.salvatorenovelli.redirectcheck.io.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RedirectCheckResponseExcelSerializerTest {

    private static final int FIRST_VALID_ROW_SKIPPING_HEADER = 1;
    private static final int GENERAL_STATUS_COLUMN = 2;
    private static final int REDIRECT_STATUS_ERROR_COLUMN = 3;
    private static final int DESTINATION_MATCH_FLAG_COLUMN = 4;
    private static final int STATUS_CODE_MATCH_FLAG_COLUMN = 5;
    private static final int PERMANENT_REDIRECT_FLAG_COLUMN = 6;
    private static final int REDIRECT_CHAIN_COLUMN = 10;
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";


    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void destinationMatchShouldBeFlagged() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withDestinationMatch(true)
                .serialize();

        assertThat(getFieldValue(workbookFilename, GENERAL_STATUS_COLUMN), is(SUCCESS));
        assertThat(getFieldValue(workbookFilename, DESTINATION_MATCH_FLAG_COLUMN), is("true"));
    }

    @Test
    public void destinationMismatchShouldBeFlagged() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withDestinationMatch(false)
                .serialize();

        assertThat(getFieldValue(workbookFilename, GENERAL_STATUS_COLUMN), is(FAILURE));
        assertThat(getFieldValue(workbookFilename, DESTINATION_MATCH_FLAG_COLUMN), is("false"));
    }

    @Test
    public void statusCodeMatchShouldBeFlagged() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainWith(301, 200)
                .serialize();

        assertThat(getFieldValue(workbookFilename, GENERAL_STATUS_COLUMN), is(SUCCESS));
        assertThat(getFieldValue(workbookFilename, STATUS_CODE_MATCH_FLAG_COLUMN), is("true"));
    }

    @Test
    public void statusCodeMismatchShouldBeFlagged() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainWith(301, 404)
                .serialize();

        assertThat(getFieldValue(workbookFilename, GENERAL_STATUS_COLUMN), is(FAILURE));
        assertThat(getFieldValue(workbookFilename, STATUS_CODE_MATCH_FLAG_COLUMN), is("false"));
    }

    @Test
    public void permanentRedirectShouldBeFlagged() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainContainingOnly301()
                .serialize();

        assertThat(getFieldValue(workbookFilename, GENERAL_STATUS_COLUMN), is(SUCCESS));
        assertThat(getFieldValue(workbookFilename, PERMANENT_REDIRECT_FLAG_COLUMN), is("true"));
    }

    @Test
    public void nonPermanentRedirectShouldBeFlagged() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainContaining302()
                .serialize();

        assertThat(getFieldValue(workbookFilename, GENERAL_STATUS_COLUMN), is(FAILURE));
        assertThat(getFieldValue(workbookFilename, PERMANENT_REDIRECT_FLAG_COLUMN), is("false"));
    }

    @Test
    public void shouldSerializeRedirectHttpStatusChain() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainWith(301, 301, 302, 200)
                .serialize();

        assertThat(getFieldValue(workbookFilename, REDIRECT_CHAIN_COLUMN), is("301, 301, 302, 200"));
    }

    @Test
    public void invalidSpecShouldBeReported() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .serializeInvalidSpec("This was a total failure!");

        assertThat(getFieldValue(workbookFilename, GENERAL_STATUS_COLUMN), is(FAILURE));
        assertThat(getFieldValue(workbookFilename, REDIRECT_STATUS_ERROR_COLUMN), is("This was a total failure!"));
    }

    private TestWorkbookBuilder givenAnExcelWorkbook() {
        return new TestWorkbookBuilder(temporaryFolder);
    }


    private String getFieldValue(String outFileName, int column) throws IOException, InvalidFormatException {
        Workbook sheets = WorkbookFactory.create(new FileInputStream(outFileName));
        Sheet firstVisibleSheet = getFirstVisibleSheet(sheets);
        return firstVisibleSheet.getRow(FIRST_VALID_ROW_SKIPPING_HEADER).getCell(column).getStringCellValue();
    }

    private Sheet getFirstVisibleSheet(Workbook wb) {
        final Optional<Sheet> first = StreamSupport.stream(wb.spliterator(), false)
                .filter(sheet -> !(wb.isSheetHidden(wb.getSheetIndex(sheet)) || wb.isSheetVeryHidden(wb.getSheetIndex(sheet))))
                .findFirst();
        //A workbook without a visible sheet is impossible to create with Microsoft Excel or via APIs (but I'll leave the check there just in case I'm missing something)
        return first.orElseThrow(() -> new RuntimeException("The workbook looks empty!"));
    }


}