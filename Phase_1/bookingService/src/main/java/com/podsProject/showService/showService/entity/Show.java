package com.podsProject.showService.showService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "bookingPK")
    @SequenceGenerator(name = "bookingPK",initialValue = 1,allocationSize = 1)
    private Integer id;
    @Column(name = "theatre_id")
    private Integer theatreId;
    private String title;
    private Integer price;
    private Integer seats_available;

}
