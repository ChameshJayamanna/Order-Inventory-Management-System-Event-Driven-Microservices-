package com.sys.order.kafka;


import com.sys.order.event.OrderCancelledEvent;
import com.sys.order.event.OrderConfirmedEvent;
import com.sys.order.event.OrderCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topic.order-confirmed}")
    private String orderConfirmedTopic;

    @Value("${kafka.topic.order-cancelled}")
    private String orderCancelledTopic;


    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(orderCreatedTopic, event.getOrderId(), event);
    }

    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        kafkaTemplate.send(orderConfirmedTopic, event.getOrderId(), event);
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        kafkaTemplate.send(orderCancelledTopic, event.getOrderId(), event);
    }


}
