package com.podsProject.showService.showService.entity;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "bookingPK")
    @SequenceGenerator(name = "bookingPK",initialValue = 1,allocationSize = 1)
    @Column(updatable = false, insertable = false)

    private Integer id;
    @Column(updatable = false, insertable = false)
    private String name;
    @Column(updatable = false, insertable = false)
    private String location;
}
