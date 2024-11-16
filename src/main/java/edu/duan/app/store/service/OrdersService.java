package edu.duan.app.store.service;

import edu.duan.app.store.api.*;
import edu.duan.app.store.data.*;
import edu.duan.app.store.exception.ItemNotFoundException;
import edu.duan.app.store.exception.OrderNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class OrdersService {
    private OrdersRepository ordersRepository;
    private UsersRepository usersRepository;
    private ItemsRepository itemsRepository;
    private WarehouseRepository warehouseRepository;
    private OrderStateProvider orderStateProvider;

    public Order get(int id) {
        return ordersRepository.findById(id).map(this::convertToApi).orElseThrow(orderNotFoundException(id));
    }

    public List<Order> getAllForUser(String userLogin) {
        return ordersRepository.findAllByUserLogin(userLogin).stream().map(this::convertToApi).toList();
    }

    public List<Order> getByCreatedDate(long from, long to) {
        return ordersRepository.findAllByCreatedDateBetween(new java.sql.Date(from), new java.sql.Date(to))
                .stream().map(this::convertToApi).toList();
    }

    public List<Order> getAllForUserByState(String userLogin, OrderState orderState) {
        return ordersRepository.findAllByUserLoginAndState(userLogin, OrderStateEntity.valueOf(orderState.name()))
                .stream().map(this::convertToApi).toList();
    }

    public List<Order> getAllByState(OrderState orderState) {
        return ordersRepository.findAllByState(OrderStateEntity.valueOf(orderState.name()))
                .stream().map(this::convertToApi).toList();
    }


    @Transactional
    public OrderState placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        User user = new User();
        user.setLogin(orderRequest.getLogin());
        orderItem.setId(orderRequest.getItemId());
        order.setOrderItem(orderItem);
        order.setOrderState(OrderState.NEW);
        order.setCount(orderRequest.getCount());
        order.setUser(user);
        OrderEntity orderEntity = convertToDomain(order, getItemEntity(order.getOrderItem()), getUserEntity(order.getUser()));
        OrderStateEntity orderStateEntity = orderStateProvider.getOrderStateHandler(order.getOrderState()).handle(orderEntity);
        ordersRepository.save(orderEntity);
        return OrderState.valueOf(orderStateEntity.name());
    }

    @Transactional
    public OrderState processOrder(int orderId, OrderState newOrderState, String notes, String fulfillmentNotes) {
        OrderEntity orderEntity = ordersRepository.findById(orderId).orElseThrow(orderNotFoundException(orderId));

        if (notes != null && !notes.isBlank()) {
            orderEntity.setNotes(notes);
        }
        if (newOrderState == OrderState.FULFILLED && fulfillmentNotes != null && !fulfillmentNotes.isBlank()) {
            orderEntity.setFulfillmentNotes(fulfillmentNotes);
        }
        OrderStateEntity orderStateEntity = orderStateProvider.getOrderStateHandler(newOrderState).handle(orderEntity);
        return OrderState.valueOf(orderStateEntity.name());
    }

    private ItemEntity getItemEntity(OrderItem orderItem) {
        return itemsRepository
                .findById(orderItem.getId())
                .orElseThrow(itemNotFoundException(orderItem.getId()));
    }

    private UserEntity getUserEntity(User user) {
        return usersRepository
                .findByLogin(user.getLogin())
                .orElse(createUserEntity(user.getLogin()));
    }

    private UserEntity createUserEntity(String login) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(login);
        return userEntity;
    }

    private Order convertToApi(OrderEntity orderEntity) {
        Order order = new Order();
        order.setId(orderEntity.getId());
        order.setCount(orderEntity.getCount());
        order.setTotal(orderEntity.getTotal());
        order.setNotes(orderEntity.getNotes());
        order.setFulfillmentNotes(orderEntity.getFulfillmentNotes());


        if (orderEntity.getCreatedDate() != null) {
            order.setCreatedDate(new Date(orderEntity.getCreatedDate().getTime()));
        }
        if (orderEntity.getUpdatedDate() != null) {
            order.setUpdatedDate(new Date(orderEntity.getUpdatedDate().getTime()));
        }
        if (orderEntity.getState() != null) {
            order.setOrderState(OrderState.valueOf(orderEntity.getState().name()));
        }
        if (orderEntity.getUser() != null) {
            User user = new User();
            user.setLogin(orderEntity.getUser().getLogin());
            order.setUser(user);
        }
        if (orderEntity.getItem() != null) {
            OrderItem orderItem = new OrderItem();
            ItemEntity itemEntity = orderEntity.getItem();
            orderItem.setId(itemEntity.getId());
            orderItem.setName(itemEntity.getName());
            orderItem.setDescription(itemEntity.getDescription());
            orderItem.setPrice(itemEntity.getPrice());
            order.setOrderItem(orderItem);
        }
        return order;
    }

    private OrderEntity convertToDomain(Order order, ItemEntity itemEntity, UserEntity user) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setItem(itemEntity);
        orderEntity.setUser(user);
        orderEntity.setCount(order.getCount());
        orderEntity.setTotal(itemEntity.getPrice() * order.getCount());
        orderEntity.setNotes(order.getNotes());
        orderEntity.setFulfillmentNotes(order.getFulfillmentNotes());
        orderEntity.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        orderEntity.setState(OrderStateEntity.valueOf(order.getOrderState().name()));
        return orderEntity;
    }

    private static Supplier<OrderNotFoundException> orderNotFoundException(int id) {
        return () -> new OrderNotFoundException("Order with `" + id + "` not found");
    }

    private static Supplier<ItemNotFoundException> itemNotFoundException(int id) {
        return () -> new ItemNotFoundException("Order with `" + id + "` not found");
    }
}
