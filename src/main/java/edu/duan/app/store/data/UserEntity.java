package edu.duan.app.store.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "users")
@SequenceGenerator(name = "users_generator", sequenceName = "users_seq",  allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    private int id;
    @Column(unique=true)
    private String login;
}
