package com.sys.order.event;

import com.base.base.dto.BaseEventDTO;
import com.sys.order.dto.OrderItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedEvent {

    private BaseEventDTO baseEvent;
    private String orderId;
    private List<OrderItemDTO> items;
}

