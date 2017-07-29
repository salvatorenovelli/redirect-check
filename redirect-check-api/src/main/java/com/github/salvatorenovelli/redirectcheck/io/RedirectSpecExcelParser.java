package com.github.salvatorenovelli.redirectcheck.io;

import com.github.salvatorenovelli.redirectcheck.model.RedirectSpecification;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

public class RedirectSpecExcelParser implements RedirectSpecificationParser {

    public static final int DEFAULT_STATUS_CODE = 200;
    public static final boolean NON_PARALLEL = false;
    private static final Logger logger = LoggerFactory.getLogger(RedirectSpecExcelParser.class);

    private final Workbook wb;
    private final Sheet sheet;

    public RedirectSpecExcelParser(String filename) throws IOException, InvalidFormatException {
        this.wb = WorkbookFactory.create(new FileInputStream(filename));
        this.sheet = getFirstVisibleSheet();
    }

    private Sheet getFirstVisibleSheet() {
        final Optional<Sheet> first = StreamSupport.stream(wb.spliterator(), NON_PARALLEL)
                .filter(sheet -> !(wb.isSheetHidden(wb.getSheetIndex(sheet)) || wb.isSheetVeryHidden(wb.getSheetIndex(sheet))))
                .findFirst();
        //A workbook without a visible sheet is impossible to create with Microsoft Excel or via APIs (but I'll leave the check there just in case I'm missing something)
        return first.orElseThrow(() -> new RuntimeException("The workbook looks empty!"));
    }


    @Override
    public int getNumSpecs() {
        return sheet.getPhysicalNumberOfRows();
    }


    @Override
    public void parse(Consumer<RedirectSpecification> validSpecConsumer) throws IOException {
        StreamSupport.stream(sheet.spliterator(), NON_PARALLEL)
                .map(this::toRedirectSpecification)
                .forEach(validSpecConsumer);
    }

    private RedirectSpecification toRedirectSpecification(Row row) {
        int lineNumber = row.getRowNum() + 1;
        try {
            String col1 = extractSourceURI(row);
            String col2 = extractExpectedDestination(row);
            int expectedStatusCode = extractExpectedStatusCode(row);
            return RedirectSpecification.createValid(lineNumber, col1, col2, expectedStatusCode);
        } catch (Exception e) {
            logger.warn("Unable to parse specification in row {} because:  {}", lineNumber, e.toString());
            return RedirectSpecification.createInvalid(lineNumber, e.getMessage());
        }
    }

    private String extractSourceURI(Row row) {
        Cell cell = extractCell(row, 0, "Source URI");
        return cell.getStringCellValue();
    }

    private String extractExpectedDestination(Row row) {
        Cell cell = extractCell(row, 1, "Expected Destination");
        return cell.getStringCellValue();
    }

    private int extractExpectedStatusCode(Row row) {
        Cell cell = row.getCell(2);
        if (cell == null || cell.getCellTypeEnum() == CellType.BLANK) {
            return DEFAULT_STATUS_CODE;
        }

        CellType cellTypeEnum = cell.getCellTypeEnum();
        if (cellTypeEnum == STRING) {
            String trimmedValue = cell.getStringCellValue().trim();
            if (trimmedValue.length() == 0) {
                return DEFAULT_STATUS_CODE;
            }
            return Integer.parseInt(trimmedValue);
        } else if (cellTypeEnum == NUMERIC) {
            return (int) cell.getNumericCellValue();
        }

        throw new IllegalArgumentException("Unable to handle status code with format: " + cellTypeEnum);
    }

    private Cell extractCell(Row row, int i, String cellName) {
        Cell cell = row.getCell(i);
        if (cell == null || cell.getStringCellValue().length() == 0) {
            throw new IllegalArgumentException("'" + cellName + "' parameter is invalid or missing.");
        }
        return cell;
    }


}
