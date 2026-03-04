package com.sys.order.dto;

import com.sys.order.model.OrderStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {
    private String orderId;
    private OrderStatus status;
    private BigDecimal totalAmount;


}
