package ait.cohort5860.post.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewPostDto {
    private String title;
    private String content;
    private Set<String> tags;

}
