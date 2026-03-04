package com.sys.order.event;

import com.base.base.dto.BaseEventDTO;
import lombok.Data;

@Data
public class OrderCancelledEvent {


    private BaseEventDTO baseEvent;
    private String orderId;
    private String reason;

}
