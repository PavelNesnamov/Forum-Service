package ait.cohort5860.post.service;

import ait.cohort5860.post.dao.CommentRepository;
import ait.cohort5860.post.dao.PostRepository;
import ait.cohort5860.post.dao.TagRepository;
import ait.cohort5860.post.dto.CommentDto;
import ait.cohort5860.post.dto.NewCommentDto;
import ait.cohort5860.post.dto.NewPostDto;
import ait.cohort5860.post.dto.PostDto;
import ait.cohort5860.post.dto.exception.PostNotFoundException;
import ait.cohort5860.post.model.Comment;
import ait.cohort5860.post.model.Post;
import ait.cohort5860.post.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private PostDto postDto;
    private NewPostDto newPostDto;
    private NewCommentDto newCommentDto;
    private Comment comment;
    private CommentDto commentDto;
    private Tag tag;

    @BeforeEach
    void setUp() {
        // Setup test data
        tag = new Tag("java");

        post = new Post("Test Title", "Test Content", "testAuthor");
        post.addTag(tag);
        setId(post, 1L);

        comment = new Comment("testUser", "Test Comment");
        comment.setPost(post);

        commentDto = CommentDto.builder()
                .username("testUser")
                .message("Test Comment")
                .dateCreated(LocalDateTime.now())
                .likes(0)
                .build();

        postDto = PostDto.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("testAuthor")
                .dateCreated(LocalDateTime.now())
                .tag("java")
                .likes(0)
                .comment(commentDto)
                .build();

        newPostDto = new NewPostDto("Test Title", "Test Content", new HashSet<>(Collections.singletonList("java")));
        newCommentDto = new NewCommentDto("Test Comment");
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
    void testAddNewPost() {
        // Arrange
        when(tagRepository.findById("java")).thenReturn(Optional.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        PostDto result = postService.addNewPost("testAuthor", newPostDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertEquals("testAuthor", result.getAuthor());
        assertTrue(result.getTags().contains("java"));

        // Verify
        verify(postRepository).save(any(Post.class));
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void testFindPostById() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        PostDto result = postService.findPostById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("testAuthor", result.getAuthor());

        // Verify
        verify(postRepository).findById(1L);
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void testFindPostById_NotFound() {
        // Arrange
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PostNotFoundException.class, () -> postService.findPostById(999L));

        // Verify
        verify(postRepository).findById(999L);
    }

    @Test
    void testAddLike() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // Act
        postService.addLike(1L);

        // Assert
        assertEquals(1, post.getLikes());

        // Verify
        verify(postRepository).findById(1L);
    }

    @Test
    void testUpdatePost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(tagRepository.findById("java")).thenReturn(Optional.of(tag));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        PostDto result = postService.updatePost(1L, newPostDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Content", result.getContent());
        assertTrue(result.getTags().contains("java"));

        // Verify
        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void testDeletePost() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        PostDto result = postService.deletePost(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Title", result.getTitle());

        // Verify
        verify(postRepository).findById(1L);
        verify(postRepository).delete(post);
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void testAddComment() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        PostDto result = postService.addComment(1L, "testUser", newCommentDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        assertEquals("Test Comment", result.getComments().get(0).getMessage());

        // Verify
        verify(postRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void testFindPostsByAuthor() {
        // Arrange
        when(postRepository.findByAuthorIgnoreCase("testAuthor")).thenReturn(Stream.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        Iterable<PostDto> results = postService.findPostsByAuthor("testAuthor");

        // Assert
        assertNotNull(results);
        List<PostDto> resultList = new ArrayList<>();
        results.forEach(resultList::add);
        assertEquals(1, resultList.size());
        assertEquals("testAuthor", resultList.get(0).getAuthor());

        // Verify
        verify(postRepository).findByAuthorIgnoreCase("testAuthor");
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void testFindPostsByTags() {
        // Arrange
        List<String> tags = Collections.singletonList("java");
        when(postRepository.findDistinctByTagsNameInIgnoreCase(tags)).thenReturn(Stream.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        Iterable<PostDto> results = postService.findPostsByTags(tags);

        // Assert
        assertNotNull(results);
        List<PostDto> resultList = new ArrayList<>();
        results.forEach(resultList::add);
        assertEquals(1, resultList.size());
        assertTrue(resultList.get(0).getTags().contains("java"));

        // Verify
        verify(postRepository).findDistinctByTagsNameInIgnoreCase(tags);
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void testFindPostsByPeriod() {
        // Arrange
        LocalDate dateFrom = LocalDate.now().minusDays(7);
        LocalDate dateTo = LocalDate.now();
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);

        when(postRepository.findByDateCreatedBetween(from, to)).thenReturn(Stream.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Act
        Iterable<PostDto> results = postService.findPostsByPeriod(dateFrom, dateTo);

        // Assert
        assertNotNull(results);
        List<PostDto> resultList = new ArrayList<>();
        results.forEach(resultList::add);
        assertEquals(1, resultList.size());
        assertEquals(1L, resultList.get(0).getId());

        // Verify
        verify(postRepository).findByDateCreatedBetween(from, to);
        verify(modelMapper).map(post, PostDto.class);
    }
}
