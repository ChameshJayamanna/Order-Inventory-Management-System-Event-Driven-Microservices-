package com.inventory.inventory.kafka;

import com.inventory.inventory.event.InventoryRejectedEvent;
import com.inventory.inventory.event.InventoryReservedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.inventory-reserved}")
    private String inventoryReservedTopic;

    @Value("${kafka.topic.inventory-rejected}")
    private String inventoryRejectedTopic;

    public InventoryEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishInventoryReserved(InventoryReservedEvent event) {
        kafkaTemplate.send(inventoryReservedTopic, event.getOrderId(), event);
    }

    public void publishInventoryRejected(InventoryRejectedEvent event) {
        kafkaTemplate.send(inventoryRejectedTopic, event.getOrderId(), event);
    }
}
