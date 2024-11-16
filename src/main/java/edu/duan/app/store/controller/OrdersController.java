package edu.duan.app.store.controller;

import edu.duan.app.store.api.*;
import edu.duan.app.store.service.OrdersService;
import edu.duan.app.store.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping(path = "/{id}")
    public Order get(@PathVariable int id) {
        return ordersService.get(id);
    }

    @GetMapping("/for/user")
    public List<Order> getAllForUser(@RequestParam String userLogin) {
        return ordersService.getAllForUser(userLogin);
    }

    @GetMapping(path = "/for/user/by/state")
    public List<Order> getAllForUserByState(@RequestParam String userLogin, @RequestParam OrderState state) {
        return ordersService.getAllForUserByState(userLogin, state);
    }

    @GetMapping(path = "/by/date")
    public List<Order> getByCreatedDate(@RequestParam long from, @RequestParam long to) {
        return ordersService.getByCreatedDate(from, to);
    }

    @PostMapping()
    public @ResponseBody OrderState placeOrder(@RequestBody OrderRequest order) {
        return ordersService.placeOrder(order);
    }

    @PutMapping()
    public @ResponseBody OrderState processOrder(@RequestBody ProcessingOrder processingOrder) {
        return ordersService.processOrder(processingOrder.getId(), processingOrder.getOrderState(), processingOrder.getNotes(), processingOrder.getFulfillmentNotes());
    }
}
