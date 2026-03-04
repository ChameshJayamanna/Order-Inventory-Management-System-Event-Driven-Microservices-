package com.inventory.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItem {
    @Id
    private String productId;
    private String productName;
    private int totalQuantity;
    private int reservedQuantity;
}
