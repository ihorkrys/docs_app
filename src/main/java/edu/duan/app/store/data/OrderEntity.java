package edu.duan.app.store.data;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;

@Entity(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @ManyToOne(cascade=CascadeType.ALL)
    private UserEntity user;
    @ManyToOne(cascade=CascadeType.ALL)
    private ItemEntity item;
    private int count;
    private double total;
    @Enumerated(value = EnumType.STRING)
    private OrderStateEntity state;
    private String fulfillmentNotes;
    private String notes;
    @CreatedDate
    private Timestamp createdDate = new Timestamp(System.currentTimeMillis());
    private Timestamp updatedDate;
}
