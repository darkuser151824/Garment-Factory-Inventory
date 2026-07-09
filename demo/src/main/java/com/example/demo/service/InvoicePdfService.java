package com.example.demo.service;

import com.example.demo.entity.Invoice;
import com.example.demo.entity.InvoiceItem;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class InvoicePdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private static final DeviceRgb ACCENT = new DeviceRgb(31, 78, 92);
    private static final DeviceRgb LIGHT_SHADE = new DeviceRgb(234, 241, 242);
    private static final DeviceRgb BORDER_GREY = new DeviceRgb(200, 200, 200);

    public byte[] generatePdf(Invoice invoice) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.setMargins(36, 36, 36, 36);

            addHeader(document, invoice);
            addPartiesSection(document, invoice);
            addItemsTable(document, invoice);
            addTotalsSection(document, invoice);
            addFooter(document, invoice);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("PDF generation failed for invoice {}: {}", invoice.getInvoiceNumber(), e.getMessage(), e);
            throw new RuntimeException("PDF generation failed for invoice " + invoice.getInvoiceNumber(), e);
        }
    }

    private void addHeader(Document document, Invoice invoice) {
        // Company name, big and bold, accent color
        document.add(new Paragraph(invoice.getSellerName())
                .setBold().setFontSize(20).setFontColor(ACCENT).setMarginBottom(2));
        document.add(new Paragraph(invoice.getSellerAddress())
                .setFontSize(10).setFontColor(ColorConstants.DARK_GRAY).setMarginBottom(1));
        document.add(new Paragraph("GSTIN: " + invoice.getSellerGstin())
                .setFontSize(10).setFontColor(ColorConstants.DARK_GRAY).setMarginBottom(10));

        // Divider line
        document.add(new Paragraph(" ").setBorderBottom(new SolidBorder(BORDER_GREY, 1)).setMarginBottom(10));

        // "INVOICE" title + number/date/status in a clean two-column table
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setBorder(Border.NO_BORDER);

        Cell left = new Cell().setBorder(Border.NO_BORDER);
        left.add(new Paragraph("INVOICE").setBold().setFontSize(16));
        headerTable.addCell(left);

        Cell right = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        right.add(new Paragraph("Invoice No: " + invoice.getInvoiceNumber()).setFontSize(10).setBold());
        right.add(new Paragraph("Date: " + invoice.getGeneratedAt().format(DATE_FORMAT)).setFontSize(10));
        right.add(new Paragraph("Order Ref: #" + invoice.getOrderId()).setFontSize(10));
        headerTable.addCell(right);

        document.add(headerTable);
        document.add(new Paragraph(" ").setMarginBottom(6));
    }

    private void addPartiesSection(Document document, Invoice invoice) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setBorder(Border.NO_BORDER);
        table.setMarginBottom(14);

        Cell billTo = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        billTo.add(new Paragraph("BILL TO").setBold().setFontSize(9).setFontColor(ACCENT));
        billTo.add(new Paragraph(invoice.getBuyerName()).setFontSize(11));
        table.addCell(billTo);

        Cell status = new Cell().setBorder(Border.NO_BORDER).setPadding(0).setTextAlignment(TextAlignment.RIGHT);
        status.add(new Paragraph("PAYMENT STATUS").setBold().setFontSize(9).setFontColor(ACCENT));
        status.add(new Paragraph(invoice.getPaymentStatus()).setFontSize(11));
        table.addCell(status);

        document.add(table);
    }

    private void addItemsTable(Document document, Invoice invoice) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(14);

        table.addHeaderCell(headerCell("Description"));
        table.addHeaderCell(headerCell("Qty"));
        table.addHeaderCell(headerCell("Unit Price"));
        table.addHeaderCell(headerCell("Line Total"));

        boolean shaded = false;
        for (InvoiceItem item : invoice.getItems()) {
            DeviceRgb bg = shaded ? LIGHT_SHADE : ColorConstants.WHITE.equals(ColorConstants.WHITE) ? null : null;
            table.addCell(bodyCell(item.getDescription(), TextAlignment.LEFT, shaded));
            table.addCell(bodyCell(String.valueOf(item.getQuantity()), TextAlignment.CENTER, shaded));
            table.addCell(bodyCell("Rs. " + item.getUnitPrice(), TextAlignment.RIGHT, shaded));
            table.addCell(bodyCell("Rs. " + item.getLineTotal(), TextAlignment.RIGHT, shaded));
            shaded = !shaded;
        }

        document.add(table);
    }

    private Cell headerCell(String text) {
        Cell cell = new Cell().setBackgroundColor(ACCENT).setPadding(6);
        cell.add(new Paragraph(text).setFontColor(ColorConstants.WHITE).setBold().setFontSize(10));
        return cell;
    }

    private Cell bodyCell(String text, TextAlignment align, boolean shaded) {
        Cell cell = new Cell().setPadding(6).setTextAlignment(align)
                .setBorder(new SolidBorder(BORDER_GREY, 0.5f));
        if (shaded) {
            cell.setBackgroundColor(LIGHT_SHADE);
        }
        cell.add(new Paragraph(text).setFontSize(10));
        return cell;
    }

    private void addTotalsSection(Document document, Invoice invoice) {
        // Single table, full width, 4 columns: [spacer][spacer][label][value].
        // No nested tables this time - that was what caused "Grand Total" / "GST (18%)" to wrap
        // onto two lines, since a table-inside-a-table halves the available width twice.
        Table totals = new Table(UnitValue.createPercentArray(new float[]{2, 2, 1, 1}));
        totals.setWidth(UnitValue.createPercentValue(100));
        totals.setBorder(Border.NO_BORDER);
        totals.setMarginBottom(14);

        addTotalsRow(totals, "Subtotal", "Rs. " + invoice.getSubtotal(), false);
        String gstPercent = invoice.getGstRate().multiply(java.math.BigDecimal.valueOf(100))
                .stripTrailingZeros().toPlainString();
        addTotalsRow(totals, "GST (" + gstPercent + "%)", "Rs. " + invoice.getGstAmount(), false);
        addTotalsRow(totals, "Grand Total", "Rs. " + invoice.getGrandTotal(), true);

        document.add(totals);
    }

    // Adds one label/value row spanning the last two columns of the 4-column totals table,
    // leaving the first two columns empty so the row visually sits on the right half of the page.
    private void addTotalsRow(Table totals, String label, String value, boolean emphasize) {
        totals.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER)); // spacer, spans first 2 columns

        Paragraph labelPara = new Paragraph(label).setFontSize(emphasize ? 13 : 10);
        if (emphasize) labelPara.setBold();
        Cell labelCell = new Cell().setPadding(4).setTextAlignment(TextAlignment.LEFT);
        labelCell.setBorder(emphasize ? new SolidBorder(BORDER_GREY, 1) : Border.NO_BORDER);
        labelCell.add(labelPara);
        totals.addCell(labelCell);

        Paragraph valuePara = new Paragraph(value).setFontSize(emphasize ? 13 : 10)
                .setFontColor(emphasize ? ACCENT : ColorConstants.BLACK);
        if (emphasize) valuePara.setBold();
        Cell valueCell = new Cell().setPadding(4).setTextAlignment(TextAlignment.RIGHT);
        valueCell.setBorder(emphasize ? new SolidBorder(BORDER_GREY, 1) : Border.NO_BORDER);
        valueCell.add(valuePara);
        totals.addCell(valueCell);
    }

    private void addFooter(Document document, Invoice invoice) {
        document.add(new Paragraph(" ").setMarginTop(30));
        document.add(new Paragraph(" ").setBorderBottom(new SolidBorder(BORDER_GREY, 1)).setMarginBottom(30));

        Table sigTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        sigTable.setWidth(UnitValue.createPercentValue(100));
        sigTable.setBorder(Border.NO_BORDER);

        Cell left = new Cell().setBorder(Border.NO_BORDER);
        left.add(new Paragraph("This is a system-generated invoice.").setFontSize(8).setFontColor(ColorConstants.GRAY));
        left.add(new Paragraph("Invoice Status: " + invoice.getStatus()).setFontSize(8).setFontColor(ColorConstants.GRAY));
        sigTable.addCell(left);

        Cell right = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        right.add(new Paragraph(" ").setMarginBottom(20));
        right.add(new Paragraph("_______________________").setFontSize(10));
        right.add(new Paragraph(invoice.getSignatoryName()).setBold().setFontSize(10));
        right.add(new Paragraph("Authorized Signatory").setFontSize(8).setFontColor(ColorConstants.GRAY));
        sigTable.addCell(right);

        document.add(sigTable);
    }
}