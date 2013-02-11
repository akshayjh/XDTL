package org.mmx.xdtl.db;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelSource implements Source {
    private static final Logger logger = Logger.getLogger("xdtl.rt.db.excelSource");
    
    private Workbook m_workbook;
    private Sheet m_sheet;
    private InputStream m_stream;
    private int m_rowNum;
    private int m_cellNum;
    
    public ExcelSource(InputStream stream, String sheetName, boolean header,
            int skip) throws Exception {
        m_stream = stream;
        m_workbook = WorkbookFactory.create(m_stream);

        if (sheetName == null || sheetName.length() == 0) {
            m_sheet = m_workbook.getSheetAt(0);
            if (m_sheet == null) {
                throw new Exception("No sheets in workbook");
            }
        } else {
            m_sheet = m_workbook.getSheet(sheetName);
            if (m_sheet == null) {
                throw new Exception("Sheet '" + sheetName + "' not found in workbook");
            }
        }        
        
        if (header) ++skip;
        skipRows(skip);
    }
    
    private void skipRows(int rowCount) {
        m_rowNum = rowCount;
    }

    @Override
    public void fetchRows(RowHandler rowHandler) throws Exception {
        Object[] data;
        while ((data = readNext()) != null) {
            rowHandler.handleRow(data, null);
        }
    }

    private Object[] readNext() throws IOException {
        Row row = m_sheet.getRow(m_rowNum);
        if (row == null) return null;
        
        int cellCount = row.getLastCellNum();
        Object[] result = new Object[cellCount];
        boolean rowHasValues = false;

        for (m_cellNum = 0; m_cellNum < cellCount; m_cellNum++) {
            Cell cell = row.getCell(m_cellNum);
            Object value = getCellValue(cell);
            result[m_cellNum] = value;
            rowHasValues |= value != null;
        }
        
        // stop at first empty row
        if (!rowHasValues) return null;
        
        m_rowNum++;
        return result;
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) return null;

        int cellType = cell.getCellType();
        if (cellType == Cell.CELL_TYPE_FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }
        
        return getCellValue(cell, cellType);
    }

    private Object getCellValue(Cell cell, int cellType) {
        switch (cellType) {
        case Cell.CELL_TYPE_BLANK:
            return null;
        case Cell.CELL_TYPE_BOOLEAN:
            return Boolean.toString(cell.getBooleanCellValue());
        case Cell.CELL_TYPE_ERROR:
            logger.warn("Error in cell at row " + m_rowNum + ", cell " + m_cellNum);
            return null;
        case Cell.CELL_TYPE_NUMERIC:
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            }
            return cell.getNumericCellValue();
        case Cell.CELL_TYPE_STRING:
            return cell.getRichStringCellValue().toString();
        default:
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        m_stream.close();
    }
}
