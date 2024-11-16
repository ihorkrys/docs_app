package edu.duan.app.store.service.executor;

import edu.duan.app.store.api.OrderState;
import edu.duan.app.store.data.OrderEntity;
import edu.duan.app.store.data.OrderStateEntity;
import edu.duan.app.store.exception.UnsupportedStateOfOrderException;
import edu.duan.app.store.service.OrderStateHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompletedOrderStateHandler implements OrderStateHandler {

    @Override
    public OrderState getOrderState() {
        return OrderState.COMPLETED;
    }

    @Override
    public OrderStateEntity handle(OrderEntity orderEntity) {
        switch(orderEntity.getState()) {
            case NEW, FULFILLED -> orderEntity.setState(OrderStateEntity.COMPLETED);
            default -> throw new UnsupportedStateOfOrderException("Order state not supported. State: " + orderEntity.getState());
        }
        return orderEntity.getState();
    }
}
