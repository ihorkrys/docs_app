package edu.duan.app.store.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, Integer> {
    Optional<WarehouseEntity> findFirstByItemId(Integer id);
}
