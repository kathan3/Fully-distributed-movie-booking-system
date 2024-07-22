package com.podsProject.showService.showService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "bookingPK")
    @SequenceGenerator(name = "bookingPK",initialValue = 1,allocationSize = 1)
    private Integer id;
    @Column(name = "show_id")
    private Integer showId;
    @Column(name = "user_id")
    private Integer userId;
    private Integer seatsBooked;

}
