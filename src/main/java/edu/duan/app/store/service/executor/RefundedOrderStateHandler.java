package edu.duan.app.store.service.executor;

import edu.duan.app.store.api.OrderState;
import edu.duan.app.store.data.OrderEntity;
import edu.duan.app.store.data.OrderStateEntity;
import edu.duan.app.store.data.WarehouseRepository;
import edu.duan.app.store.exception.ItemStockNotFoundException;
import edu.duan.app.store.exception.UnsupportedStateOfOrderException;
import edu.duan.app.store.service.OrderStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundedOrderStateHandler implements OrderStateHandler {
    private WarehouseRepository warehouseRepository;

    @Override
    public OrderState getOrderState() {
        return OrderState.REFUNDED;
    }

    @Override
    public OrderStateEntity handle(OrderEntity orderEntity) {
        warehouseRepository.findFirstByItemId(orderEntity.getItem().getId()).ifPresentOrElse(
                warehouseEntity -> {
                    switch (orderEntity.getState()) {
                        case COMPLETED -> {
                            orderEntity.setState(OrderStateEntity.REFUNDED);
                            warehouseEntity.setInStock(warehouseEntity.getInStock() + orderEntity.getCount());
                        }
                        default -> throw new UnsupportedStateOfOrderException("Order state not supported. State: " + orderEntity.getState());
                    }
                },
                () -> {
                    throw new ItemStockNotFoundException("Item with `" + orderEntity.getItem().getId() + "` not found in warehouse");
                }
        );
        return orderEntity.getState();
    }
}
