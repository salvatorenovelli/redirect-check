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
    private static final int REDIRECT_STATUS_COLUMN = 2;
    private static final int REDIRECT_STATUS_ERROR_COLUMN = 3;
    private static final int CLEAN_REDIRECT_COLUMN = 7;
    private static final int REDIRECT_CHAIN_COLUMN = 8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldSerializeNonCleanRedirect() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainContaining302()
                .serialize();
        assertThat(getWorkbookCleanRedirectResultField(workbookFilename), is(false));
    }

    @Test
    public void shouldMarkStatusAppropriately() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainContaining302()
                .serialize();
        assertThat(getWorkbookRedirectStatusField(workbookFilename), is("FAILURE"));
    }

    @Test
    public void shouldMarkStatusErrorAppropriately_NonPermanentRedirect() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainContaining302()
                .serialize();
        assertThat(getWorkbookRedirectStatusErrorField(workbookFilename), is("Non permanent redirect"));
    }

    @Test
    public void shouldSerializeCleanRedirect() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainContainingOnly301()
                .serialize();
        assertThat(getWorkbookCleanRedirectResultField(workbookFilename), is(true));
    }

    @Test
    public void shouldSerializeRedirectHttpStatusChain() throws Exception {
        String workbookFilename = givenAnExcelWorkbook()
                .withARedirectChainWith(301, 301, 302, 200)
                .serialize();

        assertThat(getWorkbookRedirectChainField(workbookFilename), is("301, 301, 302, 200"));
    }

    private TestWorkbookBuilder givenAnExcelWorkbook() {
        return new TestWorkbookBuilder(temporaryFolder);
    }

    private boolean getWorkbookCleanRedirectResultField(String outFileName) throws IOException, InvalidFormatException {
        return Boolean.parseBoolean(getFieldValue(outFileName, CLEAN_REDIRECT_COLUMN));
    }

    private String getWorkbookRedirectChainField(String outFileName) throws IOException, InvalidFormatException {
        return getFieldValue(outFileName, REDIRECT_CHAIN_COLUMN);
    }

    private String getWorkbookRedirectStatusField(String outFileName) throws IOException, InvalidFormatException {
        return getFieldValue(outFileName, REDIRECT_STATUS_COLUMN);
    }

    private String getWorkbookRedirectStatusErrorField(String outFileName) throws IOException, InvalidFormatException {
        return getFieldValue(outFileName, REDIRECT_STATUS_ERROR_COLUMN);
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