package edu.duan.app.store.service.executor;

import edu.duan.app.store.api.OrderState;
import edu.duan.app.store.data.OrderEntity;
import edu.duan.app.store.data.OrderStateEntity;
import edu.duan.app.store.data.WarehouseRepository;
import edu.duan.app.store.exception.ItemStockNotFoundException;
import edu.duan.app.store.service.OrderStateHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class NewOrderStateHandler implements OrderStateHandler {
    private WarehouseRepository warehouseRepository;

    @Override
    public OrderState getOrderState() {
        return OrderState.NEW;
    }

    @Override
    @Transactional
    public OrderStateEntity handle(OrderEntity orderEntity) {
        warehouseRepository.findFirstByItemId(orderEntity.getItem().getId()).ifPresentOrElse(
                warehouseEntity -> {
                    if (warehouseEntity.getInStock() <= 0 || warehouseEntity.getInStock() < orderEntity.getCount()) {
                        orderEntity.setState(OrderStateEntity.CANCELLED);
                    } else {
                        orderEntity.setState(OrderStateEntity.NEW);
                        warehouseEntity.setInStock(warehouseEntity.getInStock() - orderEntity.getCount());
                        warehouseRepository.save(warehouseEntity);
                    }
                },
                () -> {
                    throw new ItemStockNotFoundException("Item with `" + orderEntity.getItem().getId() + "` not found in warehouse");
                }
        );
        return orderEntity.getState();
    }
}
