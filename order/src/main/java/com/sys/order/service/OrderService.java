package com.sys.order.service;

import com.base.base.dto.BaseEventDTO;
import com.sys.order.dto.OrderItemDTO;
import com.sys.order.dto.OrderRequestDTO;
import com.sys.order.dto.OrderResponseDTO;
import com.sys.order.event.*;
import com.sys.order.kafka.OrderEventProducer;
import com.sys.order.model.Order;
import com.sys.order.model.OrderItem;
import com.sys.order.model.OrderStatus;
import com.sys.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderEventProducer orderEventProducer;

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.PENDING);

        order.setItems(
                request.getItems().stream().map(dto -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(dto.getProductId());
                    item.setQuantity(dto.getQuantity());
                    item.setOrder(order); // important for @ManyToOne
                    return item;
                }).collect(Collectors.toList())
        );

        // 2️⃣ Calculate total amount (mock example, replace with real prices)
        BigDecimal totalAmount = request.getItems().stream()
                .map(i -> BigDecimal.valueOf(1000L * i.getQuantity())) // assuming price=1000 for demo
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());

        // 3️⃣ Save order to DB
        Order savedOrder = orderRepository.save(order);

        BaseEventDTO baseEvent = new BaseEventDTO();
        baseEvent.setEventId(UUID.randomUUID().toString());
        baseEvent.setEventType("ORDER_CREATED");

        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setBaseEvent(baseEvent);
        event.setOrderId(savedOrder.getOrderId());
        event.setCustomerId(savedOrder.getCustomerId());
        event.setTotalAmount(savedOrder.getTotalAmount());
        event.setItems(
                savedOrder.getItems().stream()
                        .map(i -> new OrderItemDTO(i.getProductId(), i.getQuantity()))
                        .collect(Collectors.toList())
        );

        orderEventProducer.publishOrderCreatedEvent(event);

        // 5️⃣ Map Entity → ResponseDTO
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(savedOrder.getOrderId());
        response.setStatus(savedOrder.getStatus());
        response.setTotalAmount(savedOrder.getTotalAmount());

        return response;
    }

    public void processInventoryReserved(InventoryReservedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + event.getOrderId())
                );

        if (order.getStatus() != OrderStatus.PENDING) {
            return; // already processed
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        publishOrderConfirmed(order,event.getItems());
    }

    public void processInventoryRejected(InventoryRejectedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + event.getOrderId())
                );

        if (order.getStatus() != OrderStatus.PENDING) {
            return;
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        publishOrderCancelled(order,event.getReason());
    }

    public void publishOrderConfirmed(Order order, List<OrderItemDTO> items) {

        OrderConfirmedEvent event = new OrderConfirmedEvent();

        BaseEventDTO base = new BaseEventDTO();
        base.setEventId(UUID.randomUUID().toString());
        base.setEventType("ORDER_CONFIRMED");

        event.setBaseEvent(base);
        event.setOrderId(order.getOrderId());
        event.setItems(items);

        orderEventProducer.publishOrderConfirmed(event);
    }

    public void publishOrderCancelled(Order order,String reason) {

        OrderCancelledEvent event = new OrderCancelledEvent();

        BaseEventDTO base = new BaseEventDTO();
        base.setEventId(UUID.randomUUID().toString());
        base.setEventType("ORDER_CANCELLED");

        event.setBaseEvent(base);
        event.setOrderId(order.getOrderId());
        event.setReason(reason);

        orderEventProducer.publishOrderCancelled(event);
    }



}