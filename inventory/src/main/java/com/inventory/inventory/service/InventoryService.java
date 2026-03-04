package com.inventory.inventory.service;

import com.base.base.dto.BaseEventDTO;
import com.inventory.inventory.dto.InventoryCreateRequestDTO;
import com.inventory.inventory.dto.InventoryOrderItemDTO;
import com.inventory.inventory.event.InventoryRejectedEvent;
import com.inventory.inventory.event.InventoryReservedEvent;
import com.inventory.inventory.event.OrderConfirmedEvent;
import com.inventory.inventory.event.OrderCreatedEvent;
import com.inventory.inventory.kafka.InventoryEventProducer;
import com.inventory.inventory.model.InventoryItem;
import com.inventory.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryEventProducer inventoryEventProducer) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryEventProducer = inventoryEventProducer;
    }

    public InventoryItem addItem(InventoryCreateRequestDTO inventoryCreateRequestDTO){

        if(inventoryRepository.existsById(inventoryCreateRequestDTO.getProductId())){
            throw new RuntimeException(
                    "Product already exist in the inventory"
            );
        }

        InventoryItem item = new InventoryItem();
        item.setProductId(inventoryCreateRequestDTO.getProductId());
        item.setProductName(inventoryCreateRequestDTO.getProductName());
        item.setTotalQuantity(inventoryCreateRequestDTO.getTotalQuantity());
        item.setReservedQuantity(0);

        return inventoryRepository.save(item);
    }

    public void processOrderCreated(OrderCreatedEvent event) {

        //Check availability for ALL items first
        for (InventoryOrderItemDTO item : event.getItems()) {

            Optional<InventoryItem> optionalItem =
                    inventoryRepository.findById(item.getProductId());

            if (optionalItem.isEmpty()) {
                publishInventoryRejected(
                        event.getOrderId(),
                        "PRODUCT_NOT_FOUND: " + item.getProductId()
                );
                return;
            }

            InventoryItem inventoryItem = optionalItem.get();

            int available =
                    inventoryItem.getTotalQuantity() - inventoryItem.getReservedQuantity();

            if (available < item.getQuantity()) {
                publishInventoryRejected(
                        event.getOrderId(),
                        "INSUFFICIENT_STOCK: " + item.getProductId()
                );
                return;
            }
        }

        //Reserve stock (only if ALL items are valid)
        for (InventoryOrderItemDTO item : event.getItems()) {

            InventoryItem inventoryItem =
                    inventoryRepository.findById(item.getProductId()).get();

            // DEDUCT TOTAL QUANTITY
            inventoryItem.setTotalQuantity(
                    inventoryItem.getTotalQuantity() - item.getQuantity()
            );

            // INCREASE RESERVED QUANTITY
            inventoryItem.setReservedQuantity(
                    inventoryItem.getReservedQuantity() + item.getQuantity()
            );

            inventoryRepository.save(inventoryItem);
        }

        //Publish INVENTORY_RESERVED
        publishInventoryReserved(event);
    }

    private void publishInventoryReserved(OrderCreatedEvent orderEvent) {

        InventoryReservedEvent event = new InventoryReservedEvent();

        BaseEventDTO base = new BaseEventDTO();
        base.setEventId(UUID.randomUUID().toString());
        base.setEventType("INVENTORY_RESERVED");

        event.setBaseEvent(base);
        event.setOrderId(orderEvent.getOrderId());
        event.setTotalAmount(orderEvent.getTotalAmount());
        event.setItems(orderEvent.getItems());

        inventoryEventProducer.publishInventoryReserved(event);
    }

    private void publishInventoryRejected(String orderId, String reason) {

        InventoryRejectedEvent event = new InventoryRejectedEvent();

        BaseEventDTO base = new BaseEventDTO();
        base.setEventId(UUID.randomUUID().toString());
        base.setEventType("INVENTORY_REJECTED");

        event.setBaseEvent(base);
        event.setOrderId(orderId);
        event.setReason(reason);

        inventoryEventProducer.publishInventoryRejected(event);
    }

    public void processOrderConfirmed(OrderConfirmedEvent event) {
        for (InventoryOrderItemDTO item : event.getItems()) {

            InventoryItem inventoryItem = inventoryRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found: " + item.getProductId())
                    );

            inventoryItem.setReservedQuantity(
                    inventoryItem.getReservedQuantity() - item.getQuantity()
            );

            inventoryRepository.save(inventoryItem);
        }
    }


}
