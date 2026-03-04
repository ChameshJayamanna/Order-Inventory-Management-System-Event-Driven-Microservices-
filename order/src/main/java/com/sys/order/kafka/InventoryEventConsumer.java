package com.sys.order.kafka;

import com.sys.order.event.InventoryRejectedEvent;
import com.sys.order.event.InventoryReservedEvent;
import com.sys.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventConsumer {

    @Autowired
    OrderService orderService;

    @KafkaListener(
            topics = "${kafka.topic.inventory-reserved}",
            groupId = "order",
            properties = {
                    "spring.json.value.default.type=com.sys.order.event.InventoryReservedEvent"
            }
    )
    public void consumeInventoryReserved(InventoryReservedEvent event) {

        System.out.println("Inventory Reserved Event received: " + event.getOrderId());

        orderService.processInventoryReserved(event);
    }

    @KafkaListener(
            topics = "${kafka.topic.inventory-rejected}",
            groupId = "order",
            properties = {
                    "spring.json.value.default.type=com.sys.order.event.InventoryRejectedEvent"
            }
    )
    public void consumeInventoryRejected(InventoryRejectedEvent event) {

        System.out.println("Inventory Rejected Event received: " + event.getOrderId());

        orderService.processInventoryRejected(event);
    }
}
