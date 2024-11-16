package edu.duan.app.store.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemsRepository extends JpaRepository<ItemEntity, Integer> {
}
