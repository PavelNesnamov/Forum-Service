package ait.cohort5860.post.dao;

import ait.cohort5860.post.model.Post;
import ait.cohort5860.post.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class demonstrates how to test the PostRepository methods.
 * In a real application, you would use @DataJpaTest or @SpringBootTest with a test database,
 * but for demonstration purposes, we're using Mockito to mock the repository behavior.
 */
@ExtendWith(MockitoExtension.class)
public class PostRepositoryTest {

    @Mock
    private PostRepository postRepository;

    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void setUp() {
        // Create tags
        Tag tag1 = new Tag("java");
        Tag tag2 = new Tag("spring");
        Tag tag3 = new Tag("testing");

        // Create posts with different authors, tags, and dates
        post1 = new Post("Java Basics", "Content about Java basics", "author1");
        post1.addTag(tag1);

        post2 = new Post("Spring Framework", "Content about Spring", "author2");
        post2.addTag(tag2);

        post3 = new Post("Testing in Java", "Content about testing", "author1");
        post3.addTag(tag1);
        post3.addTag(tag3);

        // Set different creation dates
        LocalDateTime now = LocalDateTime.now();
        post1.setDateCreated(now.minusDays(5));
        post2.setDateCreated(now.minusDays(3));
        post3.setDateCreated(now.minusDays(1));

        // Set IDs for the posts
        setId(post1, 1L);
        setId(post2, 2L);
        setId(post3, 3L);
    }

    // Helper method to set ID using reflection (since id is private and has no setter)
    private void setId(Post post, long id) {
        try {
            java.lang.reflect.Field field = Post.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(post, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    void testFindByAuthorIgnoreCase() {
        // Mock repository behavior for findByAuthorIgnoreCase
        when(postRepository.findByAuthorIgnoreCase("author1")).thenReturn(Stream.of(post1, post3));
        when(postRepository.findByAuthorIgnoreCase("AUTHOR1")).thenReturn(Stream.of(post1, post3));

        // Test finding posts by author (case insensitive)
        List<Post> posts = postRepository.findByAuthorIgnoreCase("author1").collect(Collectors.toList());

        assertEquals(2, posts.size());
        assertTrue(posts.contains(post1));
        assertTrue(posts.contains(post3));
        assertFalse(posts.contains(post2));

        // Test case insensitivity
        List<Post> postsUpperCase = postRepository.findByAuthorIgnoreCase("AUTHOR1").collect(Collectors.toList());
        assertEquals(2, postsUpperCase.size());
        assertTrue(postsUpperCase.contains(post1));
        assertTrue(postsUpperCase.contains(post3));

        // Verify the repository method was called
        verify(postRepository).findByAuthorIgnoreCase("author1");
        verify(postRepository).findByAuthorIgnoreCase("AUTHOR1");
    }

    @Test
    void testFindDistinctByTagsNameInIgnoreCase() {
        // Mock repository behavior for findDistinctByTagsNameInIgnoreCase
        when(postRepository.findDistinctByTagsNameInIgnoreCase(Arrays.asList("java")))
            .thenReturn(Stream.of(post1, post3));
        when(postRepository.findDistinctByTagsNameInIgnoreCase(Arrays.asList("java", "testing")))
            .thenReturn(Stream.of(post1, post3));
        when(postRepository.findDistinctByTagsNameInIgnoreCase(Arrays.asList("JAVA")))
            .thenReturn(Stream.of(post1, post3));

        // Test finding posts by tags
        List<Post> posts = postRepository.findDistinctByTagsNameInIgnoreCase(Arrays.asList("java")).collect(Collectors.toList());

        assertEquals(2, posts.size());
        assertTrue(posts.contains(post1));
        assertTrue(posts.contains(post3));

        // Test with multiple tags
        List<Post> multiTagPosts = postRepository.findDistinctByTagsNameInIgnoreCase(Arrays.asList("java", "testing")).collect(Collectors.toList());
        assertEquals(2, multiTagPosts.size());
        assertTrue(multiTagPosts.contains(post1));
        assertTrue(multiTagPosts.contains(post3));

        // Test case insensitivity
        List<Post> caseInsensitivePosts = postRepository.findDistinctByTagsNameInIgnoreCase(Arrays.asList("JAVA")).collect(Collectors.toList());
        assertEquals(2, caseInsensitivePosts.size());

        // Verify the repository method was called
        verify(postRepository).findDistinctByTagsNameInIgnoreCase(Arrays.asList("java"));
        verify(postRepository).findDistinctByTagsNameInIgnoreCase(Arrays.asList("java", "testing"));
        verify(postRepository).findDistinctByTagsNameInIgnoreCase(Arrays.asList("JAVA"));
    }

    @Test
    void testFindByDateCreatedBetween() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveDaysAgo = now.minusDays(5);
        LocalDateTime twoDaysAgo = now.minusDays(2);

        // Mock repository behavior for findByDateCreatedBetween
        when(postRepository.findByDateCreatedBetween(twoDaysAgo, now))
            .thenReturn(Stream.of(post3));
        when(postRepository.findByDateCreatedBetween(fiveDaysAgo, now))
            .thenReturn(Stream.of(post1, post2, post3));

        // Test finding posts between dates
        List<Post> recentPosts = postRepository.findByDateCreatedBetween(twoDaysAgo, now).collect(Collectors.toList());
        assertEquals(1, recentPosts.size());
        assertTrue(recentPosts.contains(post3));

        // Test finding all posts in a wider date range
        List<Post> allPosts = postRepository.findByDateCreatedBetween(fiveDaysAgo, now).collect(Collectors.toList());
        assertEquals(3, allPosts.size());
        assertTrue(allPosts.contains(post1));
        assertTrue(allPosts.contains(post2));
        assertTrue(allPosts.contains(post3));

        // Verify the repository method was called
        verify(postRepository).findByDateCreatedBetween(twoDaysAgo, now);
        verify(postRepository).findByDateCreatedBetween(fiveDaysAgo, now);
    }

    @Test
    void testBasicCrudOperations() {
        // Create a new post
        Post newPost = new Post("New Post", "New Content", "newAuthor");
        setId(newPost, 4L);

        // Mock repository behavior for save, findById, findAll, and delete
        when(postRepository.save(any(Post.class))).thenReturn(newPost);
        when(postRepository.findById(4L)).thenReturn(Optional.of(newPost));
        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2, post3));
        doNothing().when(postRepository).delete(any(Post.class));

        // Test save
        Post savedPost = postRepository.save(newPost);
        assertNotNull(savedPost);
        assertEquals(4L, savedPost.getId());
        assertEquals("New Post", savedPost.getTitle());

        // Test findById
        Post foundPost = postRepository.findById(4L).orElse(null);
        assertNotNull(foundPost);
        assertEquals("New Post", foundPost.getTitle());

        // Test update
        foundPost.setTitle("Updated Title");
        when(postRepository.save(foundPost)).thenReturn(foundPost);
        when(postRepository.findById(4L)).thenReturn(Optional.of(foundPost));

        Post updatedPost = postRepository.save(foundPost);
        assertEquals("Updated Title", updatedPost.getTitle());

        Post retrievedPost = postRepository.findById(4L).orElse(null);
        assertNotNull(retrievedPost);
        assertEquals("Updated Title", retrievedPost.getTitle());

        // Test delete
        postRepository.delete(foundPost);
        when(postRepository.findById(4L)).thenReturn(Optional.empty());
        assertTrue(postRepository.findById(4L).isEmpty());

        // Test findAll
        List<Post> allPosts = postRepository.findAll();
        assertEquals(3, allPosts.size());

        // Verify the repository methods were called
        verify(postRepository, times(2)).save(any(Post.class));
        verify(postRepository, times(3)).findById(4L);
        verify(postRepository).delete(any(Post.class));
        verify(postRepository).findAll();
    }
}
