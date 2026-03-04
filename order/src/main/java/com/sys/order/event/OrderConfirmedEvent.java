package com.sys.order.event;


import com.base.base.dto.BaseEventDTO;
import com.sys.order.dto.OrderItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class OrderConfirmedEvent {

    private BaseEventDTO baseEvent;
    private String orderId;
    private List<OrderItemDTO> items;

}
