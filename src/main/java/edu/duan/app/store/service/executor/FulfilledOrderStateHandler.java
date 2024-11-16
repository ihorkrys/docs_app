package edu.duan.app.store.service.executor;

import edu.duan.app.store.api.OrderState;
import edu.duan.app.store.data.OrderEntity;
import edu.duan.app.store.data.OrderStateEntity;
import edu.duan.app.store.exception.UnsupportedStateOfOrderException;
import edu.duan.app.store.service.OrderStateHandler;
import org.springframework.stereotype.Component;

@Component
public class FulfilledOrderStateHandler implements OrderStateHandler {

    @Override
    public OrderState getOrderState() {
        return OrderState.FULFILLED;
    }

    @Override
    public OrderStateEntity handle(OrderEntity orderEntity) {
        switch(orderEntity.getState()) {
            case NEW -> orderEntity.setState(OrderStateEntity.FULFILLED);
            default -> throw new UnsupportedStateOfOrderException("Order state not supported. State: " + orderEntity.getState());
        }
        return orderEntity.getState();
    }
}
