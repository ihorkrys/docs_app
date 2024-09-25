package edu.duan.app.docs.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;
import java.sql.Timestamp;

@Entity(name = "documents")
@Getter
@Setter
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Column(length = 1000)
    private String content;
    @Enumerated(value = EnumType.STRING)
    private DocumentTypeEntity documentType;
    @CreatedDate
    private Timestamp createdDate = new Timestamp(System.currentTimeMillis());
    private Timestamp signedDate;
    @ManyToOne(cascade=CascadeType.ALL)
    private UserEntity user;

}