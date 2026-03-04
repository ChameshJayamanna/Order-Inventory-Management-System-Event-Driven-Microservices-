package com.inventory.inventory.kafka;


import com.inventory.inventory.event.OrderConfirmedEvent;
import com.inventory.inventory.event.OrderCreatedEvent;
import com.inventory.inventory.service.InventoryService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {
    private final InventoryService inventoryService;

    public OrderEventConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(
            topics = "${kafka.topic.order-created}",
            groupId = "inventory",
            properties = {
                    "spring.json.value.default.type=com.inventory.inventory.event.OrderCreatedEvent"
            }
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {

        System.out.println("ORDER_CREATED received: " + event.getOrderId());

        inventoryService.processOrderCreated(event);
    }

    @KafkaListener(
            topics = "${kafka.topic.order-confirmed}",
            groupId = "inventory",
            properties = {
                    "spring.json.value.default.type=com.inventory.inventory.event.OrderConfirmedEvent"
            }
    )
    public void consumeOrderConfirmed(OrderConfirmedEvent event) {

        System.out.println("ORDER_CONFIRMED received: " + event.getOrderId());

        inventoryService.processOrderConfirmed(event);
    }
}
