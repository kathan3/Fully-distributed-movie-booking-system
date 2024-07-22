package com.microservice.h2db.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Theatre {
    @Id
    @Column(updatable = false, insertable = false)
    private Integer id;
    @Column(updatable = false, insertable = false)
    private String name;
    @Column(updatable = false, insertable = false)
    private String location;
}
