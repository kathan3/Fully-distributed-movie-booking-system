package com.podsProject.showService.showService.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor

public class WalletRequestDTO {

    private String action;
    private Integer amount;

}
