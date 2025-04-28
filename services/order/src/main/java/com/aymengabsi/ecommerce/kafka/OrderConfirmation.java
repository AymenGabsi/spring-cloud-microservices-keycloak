package com.aymengabsi.ecommerce.kafka;

import com.aymengabsi.ecommerce.customer.CustomerResponse;
import com.aymengabsi.ecommerce.order.PaymentMethod;
import com.aymengabsi.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
