package edu.duan.app.store.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "warehouse")
@SequenceGenerator(name = "warehouse_generator", sequenceName = "warehouse_seq", allocationSize = 1)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "warehouse_generator")
    private int id;
    @OneToOne(cascade=CascadeType.ALL)
    private ItemEntity item;
    private int inStock;
}
