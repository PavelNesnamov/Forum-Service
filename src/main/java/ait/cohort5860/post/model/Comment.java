package ait.cohort5860.post.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(of="id")
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(generator = "comment_id_seq")
    private Long id;
    private String username;
    private String message;
    private LocalDateTime dateCreated = LocalDateTime.now();
    private int likes;
    @ManyToOne
    private Post post;

    public Comment(String user, String message) {
        this.username = user;
        this.message = message;
    }

    public void addLike() {
        likes++;
    }
}
