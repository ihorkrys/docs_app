package edu.duan.app.store.service;

import edu.duan.app.store.api.OrderState;
import edu.duan.app.store.service.executor.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderStateProvider {
    private final NewOrderStateHandler newOrderStateHandler;
    private final CompletedOrderStateHandler completedOrderStateHandler;
    private final CanceledOrderStateHandler canceledOrderStateHandler;
    private final FulfilledOrderStateHandler fulfilledOrderStateHandler;
    private final RefundedOrderStateHandler refundedOrderStateHandler;

    public OrderStateHandler getOrderStateHandler(OrderState orderState) {
        return switch (orderState) {
            case NEW -> newOrderStateHandler;
            case COMPLETED -> completedOrderStateHandler;
            case FULFILLED -> fulfilledOrderStateHandler;
            case REFUNDED -> refundedOrderStateHandler;
            case CANCELLED -> canceledOrderStateHandler;
        };
    }
}
