package ait.cohort5860.accounting.dto;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String login;
    private String firstName;
    private String lastName;
    @Singular
    private Set<String> roles;
}
