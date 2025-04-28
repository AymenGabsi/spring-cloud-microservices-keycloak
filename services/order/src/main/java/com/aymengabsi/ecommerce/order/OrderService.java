package com.aymengabsi.ecommerce.order;


import com.aymengabsi.ecommerce.customer.CustomerClient;
import com.aymengabsi.ecommerce.exception.BusinessException;
import com.aymengabsi.ecommerce.kafka.OrderConfirmation;
import com.aymengabsi.ecommerce.kafka.OrderProducer;
import com.aymengabsi.ecommerce.orderline.OrderLineRequest;
import com.aymengabsi.ecommerce.orderline.OrderLineService;
import com.aymengabsi.ecommerce.product.ProductClient;
import com.aymengabsi.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final CustomerClient customerClient;
//    private final PaymentClient paymentClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    @Transactional
    public Integer createOrder(OrderRequest request) {
        // Check The Customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

        // Purchase The Products --> product-ms (RestTemplate)
        var purchasedProducts = productClient.purchaseProducts(request.products());

        //Persist Order
        var order = this.repository.save(mapper.toOrder(request));

        //Persist Order Lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //Start Payment Process
//        var paymentRequest = new PaymentRequest(
//                request.amount(),
//                request.paymentMethod(),
//                order.getId(),
//                order.getReference(),
//                customer
//        );
//        paymentClient.requestOrderPayment(paymentRequest);


        //Send The Order Confirmation --> notification-ms (Kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAllOrders() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(this.mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
    }
}
