package com.inventory.inventory.event;

import com.base.base.dto.BaseEventDTO;
import com.inventory.inventory.dto.InventoryOrderItemDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreatedEvent {

    private BaseEventDTO baseEvent;
    private String orderId;
    private BigDecimal totalAmount;
    private List<InventoryOrderItemDTO> items;
}
