package com.sys.order.event;

import com.base.base.dto.BaseEventDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRejectedEvent {
    private BaseEventDTO baseEvent;
    private String orderId;
    private String reason;
}

