package edu.duan.app.store.executor;

import edu.duan.app.store.StoreApplication;
import edu.duan.app.store.api.Order;
import edu.duan.app.store.api.OrderItem;
import edu.duan.app.store.api.OrderState;
import edu.duan.app.store.api.User;
import edu.duan.app.store.data.*;
import edu.duan.app.store.exception.ItemStockNotFoundException;
import edu.duan.app.store.service.OrdersService;
import edu.duan.app.store.service.executor.NewOrderStateHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = StoreApplication.class)
class NewOrderStateHandlerTests {
    @Mock
    private WarehouseRepository warehouseRepository;
    @InjectMocks
    private NewOrderStateHandler orderStateHandler;

    private WarehouseEntity fullWarehouse;

    @BeforeEach
    public void init() {
        fullWarehouse = buildWarehouseEntity(1, 10);
        when(warehouseRepository.save(any(WarehouseEntity.class))).thenReturn(fullWarehouse);
    }

    @Test
    public void returnNewOrderState() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(fullWarehouse));
        OrderStateEntity actualOrderState = orderStateHandler.handle(buildOrder(OrderStateEntity.NEW, 1));

        assertEquals(OrderStateEntity.NEW, actualOrderState);
    }

    @Test
    public void callWarehouseRepositorySaveOnNewOrderState() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(fullWarehouse));
        orderStateHandler.handle(buildOrder(OrderStateEntity.NEW, 1));
        verify(warehouseRepository, times(1)).save(any(WarehouseEntity.class));
    }

    @Test
    public void returnCanceledOrderStateIfOutOfStock() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(buildWarehouseEntity(1, 0)));
        OrderStateEntity actualOrderState = orderStateHandler.handle(buildOrder(OrderStateEntity.NEW, 1));

        assertEquals(OrderStateEntity.CANCELLED, actualOrderState);
    }

    @Test
    public void throwExceptionIfWarehouseNotFound() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.empty());
        assertThrows(ItemStockNotFoundException.class, () -> orderStateHandler.handle(buildOrder(OrderStateEntity.NEW, 1)));
    }

    private OrderEntity buildOrder(OrderStateEntity orderStateEntity, int count) {
        OrderEntity.OrderEntityBuilder builder = OrderEntity.builder();
        ItemEntity.ItemEntityBuilder itemEntityBuilder = ItemEntity.builder();

        return builder.state(orderStateEntity)
                .item(itemEntityBuilder
                        .id(1)
                        .build()
                )
                .count(count)
                .build();
    }

    private WarehouseEntity buildWarehouseEntity(int id, int inStock) {
        return new WarehouseEntity(
                id,
                new ItemEntity(1, "test name", "test description", 10.1),
                inStock
        );
    }
}
