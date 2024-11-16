package edu.duan.app.store.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "warehouse")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @OneToOne(cascade=CascadeType.ALL)
    private ItemEntity item;
    private int inStock;
}
