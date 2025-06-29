package ait.cohort5860.accaunting.dto;

import lombok.*;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "login")
public class UserResponseDto {
    private String login;
    private String firstName;
    private String lastName;
    @Setter
    private Set<String> roles;
}
