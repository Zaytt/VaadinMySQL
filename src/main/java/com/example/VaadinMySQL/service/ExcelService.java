package com.example.VaadinMySQL.service;

import com.example.VaadinMySQL.model.reports.Reportable;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelService <E extends Reportable>{

    public void createExcelFile(String name, String[] headerArray, List<E> dataList) throws IOException {
        // Create a Workbook
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Sheet sheet = workbook.createSheet();

        //Create a font for the headers
        Font headerFont = createFont(true, 14, IndexedColors.BLACK.index, "Arial", workbook);

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        //Write the headers into the sheet
        sheet = writeHeaders(sheet, headerArray, headerCellStyle);

        //Write the data
        sheet = writeData(sheet, dataList);

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("reports/excel/"+name+".xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }

    private Sheet writeHeaders(Sheet sheet, String[] headers){

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        return sheet;
    }

    private Sheet writeHeaders(Sheet sheet, String[] headers, CellStyle cellStyle){

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(cellStyle);
        }

        return sheet;
    }

    private Sheet writeData(Sheet sheet, List<E> dataList){
        // Create Other rows and cells with employees data
        int rowNum = 1;
        int colNum = 0;
        for(E item: dataList) {
            Row row = sheet.createRow(rowNum++);
            for (String property: item.toStringArray())
                row.createCell(colNum++).setCellValue(property);

            colNum = 0;
        }

        // Resize all columns to fit the content size
        for(int i = 0; i < dataList.size(); i++)
            sheet.autoSizeColumn(i);

        return sheet;
    }


    private Font createFont(Boolean bold, int size, int color, String fontName, Workbook workbook){
        Font font = workbook.createFont();
        font.setBold(bold);
        font.setFontHeightInPoints((short)size);
        font.setColor((short)color);
        font.setFontName(fontName);

        return font;
    }
}
