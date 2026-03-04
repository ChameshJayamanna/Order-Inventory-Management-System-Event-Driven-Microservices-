package com.inventory.inventory.event;

import com.base.base.dto.BaseEventDTO;
import com.inventory.inventory.dto.InventoryOrderItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderConfirmedEvent {

    private BaseEventDTO baseEvent;
    private String orderId;
    private List<InventoryOrderItemDTO> items;

}