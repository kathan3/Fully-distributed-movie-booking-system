package com.podsProject.showService.showService.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor


public class ShowResponseDTO {

Integer id;
Integer theatre_id;
String title;
Integer price;
Integer seats_available;
}
