package edu.duan.app.store.service;

import edu.duan.app.store.api.OrderState;
import edu.duan.app.store.data.OrderEntity;
import edu.duan.app.store.data.OrderStateEntity;

public interface OrderStateHandler {
    public OrderState getOrderState();
    public OrderStateEntity handle(OrderEntity orderEntity);
}
