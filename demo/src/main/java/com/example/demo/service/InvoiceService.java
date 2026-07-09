package com.example.demo.service;

import com.example.demo.Mapper.InvoiceMapper;
import com.example.demo.config.CompanyConfig;
import com.example.demo.dto.InvoiceDTO;
import com.example.demo.entity.Invoice;
import com.example.demo.entity.InvoiceItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.Status;
import com.example.demo.exception.InvalidTransitionException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;

@Slf4j
@Service
public class InvoiceService {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private CompanyConfig companyConfig;
    private InvoiceRepository invoiceRepository;
    private InvoiceItemService invoiceItemService;
    private JdbcTemplate jdbcTemplate;
    private InvoiceMapper invoiceMapper;
    InvoiceService(InvoiceMapper invoiceMapper,JdbcTemplate jdbcTemplate,InvoiceItemService invoiceItemService,CompanyConfig companyConfig,InvoiceRepository invoiceRepository,OrderRepository orderRepository,ProductRepository productRepository){
        this.invoiceMapper=invoiceMapper;
        this.jdbcTemplate=jdbcTemplate;
        this.invoiceItemService=invoiceItemService;
        this.orderRepository=orderRepository;
        this.companyConfig=companyConfig;
        this.invoiceRepository=invoiceRepository;
        this.productRepository=productRepository;
    }

    @Transactional
    public InvoiceDTO generateInvoice(Long orderId) {


        Invoice invoice=new Invoice();
        Order lockOrder=orderRepository.findWithIdAndLock(orderId).orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));;

        if (lockOrder.getStatus() != Status.DELIVERED) {
            throw new InvalidTransitionException("Invoice cannot be generated for an order that is not DELIVERED. Current status: " + lockOrder.getStatus());
        }
        Optional<Invoice> result=invoiceRepository.getInvoiceWithItemsByOrderId(orderId);
        log.info("INvoice finding for order "+orderId);
        if(!result.isEmpty()){
            return invoiceMapper.mapToInvoiceResponse(result.get());
        }
        invoice.setGeneratedAt(LocalDateTime.now());
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.SUCCESS);
        invoice.setPaymentStatus("PAID");
        Order order = orderRepository.findByIdWithItemsAndUser(orderId).orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " not found"));


        invoice.setBuyerName(order.getUser().getUsername());
        invoice.setOrderId(order.getOrderId());
        invoice.setSellerAddress(companyConfig.getAddress());
        invoice.setSellerGstin(companyConfig.getGstin());
        invoice.setSignatoryName(companyConfig.getSignatoryName());
        invoice.setSellerName(companyConfig.getName());
        invoice.setGstRate(companyConfig.getGstRate());
        for(OrderItem orderItem: order.getOrderItemList()){
             InvoiceItem invoiceItem=invoiceItemService.generateInvoiceItem(orderItem,invoice);
             invoice.addItem(invoiceItem);
        }
        BigDecimal cost=BigDecimal.ZERO;
        for(InvoiceItem invoiceItem:invoice.getItems()){
            cost=cost.add(invoiceItem.getLineTotal());
        }
        invoice.setSubtotal(cost);
        BigDecimal gstOnTotal=cost.multiply(companyConfig.getGstRate());
        invoice.setGstAmount(gstOnTotal);
        invoice.setGrandTotal(cost.add(gstOnTotal));
        invoice.setInvoiceNumber(InvoiceNumberGenerator());
        invoice.setStatus(InvoiceStatus.SUCCESS);
        Invoice saved = invoiceRepository.saveAndFlush(invoice);
        order.setIsInvoiceGenerated(true);
         orderRepository.save(order);
        log.info("new invoice generated invoice number"+invoice.getInvoiceNumber());
        return invoiceMapper.mapToInvoiceResponse(saved);
    }
    public String InvoiceNumberGenerator(){
        Long nextVal = jdbcTemplate.queryForObject("SELECT nextval('invoice_number_seq')", Long.class);
        int year = Year.now().getValue();
        return String.format("INV-%d-%06d", year, nextVal);

    }

}
