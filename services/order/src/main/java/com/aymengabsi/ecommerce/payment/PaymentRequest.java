package com.aymengabsi.ecommerce.payment;

import com.aymengabsi.ecommerce.customer.CustomerResponse;
import com.aymengabsi.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
