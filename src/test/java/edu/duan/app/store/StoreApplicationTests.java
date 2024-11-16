package edu.duan.app.store;

import edu.duan.app.store.api.*;
import edu.duan.app.store.data.*;
import edu.duan.app.store.service.executor.NewOrderStateHandler;
import edu.duan.app.store.service.OrderStateProvider;
import edu.duan.app.store.service.OrdersService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = StoreApplication.class)
class StoreApplicationTests {
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private ItemsRepository itemsRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private OrderStateProvider orderStateProvider;
    @InjectMocks
    private NewOrderStateHandler orderStateHandler;
    @InjectMocks
    private OrdersService ordersService;

    @Test
    public void createOrderMustSaveOrderInNewState() {
        when(usersRepository.findByLogin("test@mail.com")).thenReturn(Optional.of(new UserEntity(1, "test@mail.com")));
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(warehouseEntityStub));
        when(itemsRepository.findById(1)).thenReturn(Optional.of(warehouseEntityStub.getItem()));
        when(ordersRepository.save(any())).thenReturn(new OrderEntity());
        when(orderStateProvider.getOrderStateHandler(OrderState.NEW)).thenReturn(orderStateHandler);
        OrderState actualOrderState = ordersService.placeOrder(buildOrderRequest("test@mail.com", 1, 1));

        assertEquals(OrderState.NEW, actualOrderState);
        verify(ordersRepository, times(1)).save(any());
    }

    private OrderRequest buildOrderRequest(String login, Integer itemId, Integer count) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItemId(itemId);
        orderRequest.setCount(count);
        orderRequest.setLogin(login);
        return orderRequest;
    }

    private WarehouseEntity warehouseEntityStub = new WarehouseEntity(
            1,
            new ItemEntity(1, "test name", "test description", 10.1),
            10
    );
}
