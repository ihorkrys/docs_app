package edu.duan.app.store.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity, Integer> {

    Optional<OrderEntity> findById(Integer id);
    List<OrderEntity> findAllByUserLogin(String userLogin);
    List<OrderEntity> findAllByCreatedDateBetween(Date startDate, Date endDate);
    List<OrderEntity> findAllByUserLoginAndState(String userLogin, OrderStateEntity state);
    List<OrderEntity> findAllByState(OrderStateEntity state);
}
