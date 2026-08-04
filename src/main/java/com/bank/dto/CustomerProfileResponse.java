package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerProfileResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
}