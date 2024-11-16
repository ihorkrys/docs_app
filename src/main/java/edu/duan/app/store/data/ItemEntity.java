package edu.duan.app.store.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity(name = "items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    private String name;
    private String description;
    private double price;
}
