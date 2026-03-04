package com.sys.order.event;

import com.base.base.dto.BaseEventDTO;
import com.sys.order.dto.OrderItemDTO;
import lombok.Data;


import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreatedEvent {

    private BaseEventDTO baseEvent;
    private String orderId;
    private String customerId;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;
}
