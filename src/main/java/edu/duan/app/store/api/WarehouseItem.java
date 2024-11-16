package edu.duan.app.store.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseItem {
    private int id;
    private String name;
    private String description;
    private double price;
    private int inStockCount;
}
