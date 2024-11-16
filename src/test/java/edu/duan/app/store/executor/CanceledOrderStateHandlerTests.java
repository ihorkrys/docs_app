package edu.duan.app.store.executor;

import edu.duan.app.store.StoreApplication;
import edu.duan.app.store.data.*;
import edu.duan.app.store.exception.ItemStockNotFoundException;
import edu.duan.app.store.exception.UnsupportedStateOfOrderException;
import edu.duan.app.store.service.executor.CanceledOrderStateHandler;
import edu.duan.app.store.service.executor.NewOrderStateHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = StoreApplication.class)
class CanceledOrderStateHandlerTests {
    @Mock
    private WarehouseRepository warehouseRepository;
    @InjectMocks
    private CanceledOrderStateHandler orderStateHandler;

    private WarehouseEntity fullWarehouse;

    @BeforeEach
    public void init() {
        fullWarehouse = buildWarehouseEntity(1, 10);
        when(warehouseRepository.save(any(WarehouseEntity.class))).thenReturn(fullWarehouse);
    }

    @Test
    public void returnCanceledOrderStateForNewOrder() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(fullWarehouse));
        OrderStateEntity actualOrderState = orderStateHandler.handle(buildOrder(OrderStateEntity.NEW, 1));

        assertEquals(OrderStateEntity.CANCELLED, actualOrderState);
    }

    @Test
    public void returnCanceledOrderStateForFilfilledOrder() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(fullWarehouse));
        OrderStateEntity actualOrderState = orderStateHandler.handle(buildOrder(OrderStateEntity.FULFILLED, 1));

        assertEquals(OrderStateEntity.CANCELLED, actualOrderState);
    }

    @Test
    public void callWarehouseRepositorySaveOnCanceledOrderState() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(fullWarehouse));
        orderStateHandler.handle(buildOrder(OrderStateEntity.NEW, 1));
        verify(warehouseRepository, times(1)).save(any(WarehouseEntity.class));
    }

    @Test
    public void callWarehouseRepositorySaveOnCanceledOrderStateAfterFulfilled() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(fullWarehouse));
        orderStateHandler.handle(buildOrder(OrderStateEntity.FULFILLED, 1));
        verify(warehouseRepository, times(1)).save(any(WarehouseEntity.class));
    }

    @Test
    public void returnCanceledOrderStateIfOutOfStock() {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(buildWarehouseEntity(1, 0)));
        OrderStateEntity actualOrderState = orderStateHandler.handle(buildOrder(OrderStateEntity.NEW, 1));

        assertEquals(OrderStateEntity.CANCELLED, actualOrderState);
    }

    @ParameterizedTest
    @MethodSource("orderStateValues")
    public void throwUnsupportedStateOfOrderException(OrderStateEntity state) {
        when(warehouseRepository.findFirstByItemId(1)).thenReturn(Optional.of(fullWarehouse));
        assertThrows(UnsupportedStateOfOrderException.class, () -> orderStateHandler.handle(buildOrder(state, 1)));
    }

    @Test
    public void throwItemStockNotFoundExceptionIfWarehouseNotFound() {
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

    private static Stream<Arguments> orderStateValues() {
        return Stream.of(
                Arguments.of(OrderStateEntity.CANCELLED),
                Arguments.of(OrderStateEntity.COMPLETED),
                Arguments.of(OrderStateEntity.REFUNDED)
        );
    }
}
