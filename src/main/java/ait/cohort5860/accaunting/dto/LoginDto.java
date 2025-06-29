package ait.cohort5860.accaunting.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "login")
public class LoginDto {
    private String login;
    @Setter
    private String password;
}


