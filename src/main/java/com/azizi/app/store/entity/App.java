package com.azizi.app.store.entity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "apps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String author;
    private String description;

    @ManyToOne
    private Category category;

    private LocalDateTime publishedAt;

}
