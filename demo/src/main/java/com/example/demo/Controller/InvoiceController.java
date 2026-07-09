package com.example.demo.Controller;

import com.example.demo.dto.InvoiceDTO;
import com.example.demo.dto.OrderResponseRequest;
import com.example.demo.entity.Invoice;
import com.example.demo.exception.ApiResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.service.InvoicePdfService;
import com.example.demo.service.InvoiceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private InvoicePdfService invoicePdfService;
    private InvoiceRepository invoiceRepository;

    public InvoiceController(InvoicePdfService invoicePdfService, InvoiceRepository invoiceRepository,InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.invoicePdfService=invoicePdfService;
        this.invoiceRepository=invoiceRepository;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoice(@PathVariable Long orderId) {
        return ResponseEntity.status(200).body(new ApiResponse<>(true,"Invoice for orderId"+orderId+" sucessfully fetched",invoiceService.generateInvoice(orderId)));
    }
    @GetMapping("/{orderId}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long orderId) {
        Invoice invoice = invoiceRepository.getInvoiceWithItemsByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for order " + orderId));

        byte[] pdfBytes = invoicePdfService.generatePdf(invoice);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=" + invoice.getInvoiceNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}