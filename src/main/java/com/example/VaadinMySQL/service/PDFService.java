package com.example.VaadinMySQL.service;

import com.example.VaadinMySQL.model.reports.Reportable;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFService<E extends Reportable>{

    /**
     * Prints a pdf containing a table with the given data
     * @param dest the location of the resulting file
     * @param headerArray the list of headers of the table
     * @param headerWidth the list of the the header's width
     * @param arrayList the table row data
     * @throws IOException
     */

    public  void printTablePDF(String dest, String[] headerArray, float[] headerWidth, List<E> arrayList) throws IOException {
        //Create the PDF documents, define the page size and rotate it
        String destination = dest;
        PdfWriter writer = new PdfWriter(destination);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        //Define the page format
        document.setMargins(20, 20, 20, 20);
        PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
        PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);

        //Define a table
        Table table = new Table(headerWidth);
        table.setWidth(UnitValue.createPercentValue(100));

        //Process the table's headers
        writeHeaders(table, headerArray, bold);

        //Process the table's rows
        writeRows(table, arrayList, font);

        //Add the table and close the file
        document.add(table);
        document.close();

    }

    private void writeHeaders(Table table, String[] headers,PdfFont font){
        //Go through the string array printing them as headers
        for (String header: headers) {
            table.addHeaderCell(
                    new Cell().add(
                            new Paragraph(header).setFont(font)));
        }

    }

    private void writeRows(Table table, List<E> arrayList, PdfFont font) {

        // Go through a list of 'Reportable' objects and then print each cell for their
        // properties using the toStringArray() function.

        for (Reportable item: arrayList) {
            for (String property: item.toStringArray())
                table.addCell(  new Cell().add( new Paragraph(property)));
        }
    }
}
