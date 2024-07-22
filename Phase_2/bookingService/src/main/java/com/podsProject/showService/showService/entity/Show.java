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
    private Integer id;
    @Column(name = "theatre_id")
    private Integer theatreId;
    private String title;
    private Integer price;
    private Integer seats_available;

}
