package com.personalexpense.util;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.personalexpense.model.Category;
import com.personalexpense.model.Expense;

import java.awt.Color;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PdfReportExporter {

    private PdfReportExporter() {
        // Utility class
    }

    public static void exportToPdf(List<Expense> expenses, String username, OutputStream os) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, os);
        document.open();

        // Styles
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(0, 123, 255));

        // Header
        Paragraph title = new Paragraph("Expense Summary Report", titleFont);
        title.setAlignment(Element.ALIGN_LEFT);
        document.add(title);

        Paragraph subtitle = new Paragraph("Generated dynamically for user: " + username, subtitleFont);
        subtitle.setSpacingAfter(15);
        document.add(subtitle);

        // Process data
        double grandTotal = 0.0;
        Map<String, Double> categoryTotals = new TreeMap<>();
        double uncategorizedTotal = 0.0;

        for (Expense expense : expenses) {
            grandTotal += expense.getAmount();
            List<Category> categories = expense.getCategories();
            if (categories.isEmpty()) {
                uncategorizedTotal += expense.getAmount();
            } else {
                for (Category category : categories) {
                    categoryTotals.put(category.getName(),
                            categoryTotals.getOrDefault(category.getName(), 0.0) + expense.getAmount());
                }
            }
        }

        // Total card paragraph
        Paragraph totalParagraph = new Paragraph();
        totalParagraph.add(new Phrase("Total Accumulated Expenses: ", bodyFont));
        totalParagraph.add(new Phrase(String.format("$%.2f", grandTotal), totalFont));
        totalParagraph.setSpacingAfter(20);
        document.add(totalParagraph);

        // Spending Table
        Paragraph tableTitle = new Paragraph("Spending by Category", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);

        // Table Header
        PdfPCell h1 = new PdfPCell(new Phrase("Category", headerFont));
        h1.setBackgroundColor(new Color(0, 123, 255));
        h1.setPadding(6);
        h1.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell h2 = new PdfPCell(new Phrase("Amount", headerFont));
        h2.setBackgroundColor(new Color(0, 123, 255));
        h2.setPadding(6);
        h2.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(h1);
        table.addCell(h2);

        // Table Body
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            PdfPCell cellName = new PdfPCell(new Phrase(entry.getKey(), bodyFont));
            cellName.setPadding(6);
            cellName.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell cellVal = new PdfPCell(new Phrase(String.format("$%.2f", entry.getValue()), bodyFont));
            cellVal.setPadding(6);
            cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(cellName);
            table.addCell(cellVal);
        }

        if (uncategorizedTotal > 0.0) {
            PdfPCell cellName = new PdfPCell(new Phrase("Uncategorized", bodyFont));
            cellName.setPadding(6);
            cellName.setBackgroundColor(new Color(255, 248, 248));
            cellName.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell cellVal = new PdfPCell(new Phrase(String.format("$%.2f", uncategorizedTotal), bodyFont));
            cellVal.setPadding(6);
            cellVal.setBackgroundColor(new Color(255, 248, 248));
            cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);

            table.addCell(cellName);
            table.addCell(cellVal);
        }

        document.add(table);
        document.close();
    }
}
