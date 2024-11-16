package edu.duan.app.store.data;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "items")
@SequenceGenerator(name = "items_generator", sequenceName = "items_seq", allocationSize = 1)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_generator")
    private int id;
    private String name;
    private String description;
    private double price;
}
