package com.inventory.inventory.controller;

import com.inventory.inventory.dto.InventoryCreateRequestDTO;
import com.inventory.inventory.model.InventoryItem;
import com.inventory.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="inventory/")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @PostMapping("/addItem")
    public ResponseEntity<InventoryItem> addItem
            (@RequestBody InventoryCreateRequestDTO inventoryCreateRequestDTO){
        InventoryItem savedItem = inventoryService.addItem(inventoryCreateRequestDTO);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }
}
