package com.github.salvatorenovelli.redirectcheck.io.excel;


import com.github.salvatorenovelli.redirectcheck.model.RedirectCheckResponse;
import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

public class RedirectCheckResponseExcelSerializer {

    private static final String[] HEADERS = new String[]{"Line #", "SourceURI", "RESULT", "ResultReason", "Expected URI", "Actual URI", "Last HTTP Status", "Clean Redirect", "Redirect Chain"};
    private final Workbook wb;
    private final Sheet sheet;
    private final String filename;
    private final CellStyle HEADERS_STYLE;
    private final CellStyle ROW_STYLE;

    private SortedSet<ResponseWrapper> responses = new TreeSet<>(Comparator.comparingInt(ResponseWrapper::getLineNumber));
    private int curRowIndex = 0;

    public RedirectCheckResponseExcelSerializer(String outFileName) throws IOException {

        this.filename = outFileName;
        wb = new XSSFWorkbook();
        sheet = wb.createSheet("RedirectCheck");
        HEADERS_STYLE = createHeaderStyle();
        ROW_STYLE = createBorderedStyle();

        createHeader();

    }

    public void addResponses(List<RedirectCheckResponse> responses) throws IOException {
        this.responses.addAll(responses.stream().map(ResponseWrapper::new).collect(Collectors.toList()));
    }

    public void addInvalidSpecs(List<RedirectSpecification> invalid) {
        this.responses.addAll(invalid.stream().map(ResponseWrapper::new).collect(Collectors.toList()));
    }

    public void write() throws IOException {
        try {
            responses.forEach(this::addResponse);
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i, true);
            }
        } finally {
            FileOutputStream out = new FileOutputStream(filename);
            wb.write(out);
            out.close();
            wb.close();
        }
    }

    private void createHeader() {
        Row row = sheet.createRow(curRowIndex++);
        int index = 0;
        for (String curHeader : HEADERS) {
            Cell cell = row.createCell(index++);
            cell.setCellValue(curHeader);
            cell.setCellStyle(HEADERS_STYLE);
        }
    }

    private void addResponse(ResponseWrapper cr) {
        List<String> fields;
        try {
            fields = Arrays.asList(
                    String.valueOf(cr.lineNumber), cr.sourceURI, cr.result, cr.reason, cr.expectedURI,
                    URLDecoder.decode(cr.actualURI, "UTF-8"), cr.lastHTTPStatus, String.valueOf(cr.isCleanRedirect),
                    serializeRedirectChain(cr.redirectChain));
        } catch (UnsupportedEncodingException e) {
            fields = Arrays.asList(String.valueOf(cr.lineNumber),
                    cr.sourceURI, cr.result, cr.reason, cr.expectedURI,
                    cr.actualURI, cr.lastHTTPStatus, String.valueOf(cr.isCleanRedirect),
                    serializeRedirectChain(cr.redirectChain));
        }
        writeRow(fields);
    }

    private String serializeRedirectChain(List<Integer> redirectChain) {
        return redirectChain.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    private void writeRow(List<String> fields) {
        Row row = sheet.createRow(curRowIndex++);
        int curCellIndex = 0;
        for (String field : fields) {
            Cell cell = row.createCell(curCellIndex++);
            cell.setCellValue(field);
            cell.setCellStyle(ROW_STYLE);
        }
    }

    private CellStyle createBorderedStyle() {
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        CellStyle style = wb.createCellStyle();
        style.setBorderRight(thin);
        style.setRightBorderColor(black);
        style.setBorderBottom(thin);
        style.setBottomBorderColor(black);
        style.setBorderLeft(thin);
        style.setLeftBorderColor(black);
        style.setBorderTop(thin);
        style.setTopBorderColor(black);
        return style;
    }

    private CellStyle createHeaderStyle() {
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        CellStyle style = createBorderedStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(headerFont);
        return style;
    }

}
