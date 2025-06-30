package ait.cohort5860.accounting.dto;

import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class RolesDto {
    private String login;

    @Singular
    private Set<String> roles;
}
