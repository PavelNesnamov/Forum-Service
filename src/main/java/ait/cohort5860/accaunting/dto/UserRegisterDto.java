package ait.cohort5860.accaunting.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
}
