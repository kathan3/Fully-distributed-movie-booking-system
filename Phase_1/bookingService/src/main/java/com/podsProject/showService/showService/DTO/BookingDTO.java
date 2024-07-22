package com.podsProject.showService.showService.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class BookingDTO {
    private Integer show_id;
    private Integer user_id;
    private Integer seats_booked;
}
