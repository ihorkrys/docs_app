package edu.duan.app.store.controller;

import edu.duan.app.store.api.WarehouseItem;
import edu.duan.app.store.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    private WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping(path = "/{id}")
    public WarehouseItem get(@PathVariable int id) {
        return warehouseService.getById(id);
    }

    @GetMapping("/")
    public List<WarehouseItem> getAll() {
        return warehouseService.getAll();
    }

    @PostMapping()
    public @ResponseBody WarehouseItem addItemStock(@RequestBody WarehouseItem warehouseItem) {
        return warehouseService.addItemToWarehouse(warehouseItem);
    }

    @PutMapping("/{itemId}/{count}")
    public @ResponseBody WarehouseItem updateItem(@PathVariable int itemId, @PathVariable int count) {
        return warehouseService.updateStock(itemId, count);
    }
}
