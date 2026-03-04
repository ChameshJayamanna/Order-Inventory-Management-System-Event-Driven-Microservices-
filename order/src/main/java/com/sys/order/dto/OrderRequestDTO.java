package com.sys.order.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private String customerId;
    private List<OrderItemDTO> items;
}
