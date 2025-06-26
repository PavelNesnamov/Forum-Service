package ait.cohort5860.post.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "name")
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
