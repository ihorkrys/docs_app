package edu.duan.app.store.service;

import edu.duan.app.store.api.*;
import edu.duan.app.store.data.*;
import edu.duan.app.store.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Service
public class WarehouseService {
    private WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<WarehouseItem> getAll() {
        return warehouseRepository.findAll().stream().map(this::convertToApi).toList();
    }

    public WarehouseItem getById(int id) {
        return warehouseRepository.findById(id).map(this::convertToApi).orElseThrow(warehouseItemNotFoundException(id));
    }

    @Transactional
    public WarehouseItem addItemToWarehouse(WarehouseItem warehouseItem) {
        return convertToApi(warehouseRepository.save(convertToDomain(warehouseItem)));
    }

    @Transactional
    public WarehouseItem updateStock(int itemId, int count) {
        WarehouseEntity warehouseEntity = warehouseRepository.findFirstByItemId(itemId).orElseThrow(itemStockNotFoundException(itemId));
        warehouseEntity.setInStock(count);
        return convertToApi(warehouseEntity);
    }

    private WarehouseItem convertToApi(WarehouseEntity warehouseEntity) {
        WarehouseItem warehouseItem = new WarehouseItem();
        warehouseItem.setId(warehouseEntity.getId());
        warehouseItem.setName(warehouseEntity.getItem().getName());
        warehouseItem.setDescription(warehouseEntity.getItem().getDescription());
        warehouseItem.setPrice(warehouseEntity.getItem().getPrice());
        warehouseItem.setInStockCount(warehouseEntity.getInStock());
        return warehouseItem;
    }

    private WarehouseEntity convertToDomain(WarehouseItem item) {
        WarehouseEntity warehouseEntity = new WarehouseEntity();
        warehouseEntity.setInStock(item.getInStockCount());
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(item.getId());
        itemEntity.setName(item.getName());
        itemEntity.setDescription(item.getDescription());
        itemEntity.setPrice(item.getPrice());
        warehouseEntity.setItem(itemEntity);
        return warehouseEntity;
    }

    private static Supplier<WarehouseItemNotFoundException> warehouseItemNotFoundException(int id) {
        return () -> new WarehouseItemNotFoundException("Warehouse item with `" + id + "` not found");
    }

    private static Supplier<ItemStockNotFoundException> itemStockNotFoundException(int id) {
        return () -> new ItemStockNotFoundException("Item with `" + id + "` not found in warehouse");
    }
}
