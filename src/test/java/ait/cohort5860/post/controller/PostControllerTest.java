package ait.cohort5860.post.controller;

import ait.cohort5860.post.dto.CommentDto;
import ait.cohort5860.post.dto.NewCommentDto;
import ait.cohort5860.post.dto.NewPostDto;
import ait.cohort5860.post.dto.PostDto;
import ait.cohort5860.post.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private PostDto postDto;
    private NewPostDto newPostDto;
    private NewCommentDto newCommentDto;

    @BeforeEach
    void setUp() {
        // Setup MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        // Configure ObjectMapper for date/time serialization
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup test data
        newPostDto = new NewPostDto("Test Title", "Test Content", new HashSet<>(Arrays.asList("tag1", "tag2")));

        CommentDto commentDto = CommentDto.builder()
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
                .tag("tag1")
                .tag("tag2")
                .likes(0)
                .comment(commentDto)
                .build();

        newCommentDto = new NewCommentDto("New Comment");
    }

    @Test
    void testAddNewPost() throws Exception {
        when(postService.addNewPost(anyString(), any(NewPostDto.class))).thenReturn(postDto);

        mockMvc.perform(post("/forum/post/testAuthor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPostDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.author").value("testAuthor"));
    }

    @Test
    void testFindPostById() throws Exception {
        when(postService.findPostById(anyLong())).thenReturn(postDto);

        mockMvc.perform(get("/forum/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"))
                .andExpect(jsonPath("$.author").value("testAuthor"));
    }

    @Test
    void testAddLike() throws Exception {
        doNothing().when(postService).addLike(anyLong());

        mockMvc.perform(patch("/forum/post/1/like"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdatePost() throws Exception {
        when(postService.updatePost(anyLong(), any(NewPostDto.class))).thenReturn(postDto);

        mockMvc.perform(patch("/forum/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.content").value("Test Content"));
    }

    @Test
    void testDeletePost() throws Exception {
        when(postService.deletePost(anyLong())).thenReturn(postDto);

        mockMvc.perform(delete("/forum/post/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void testAddComment() throws Exception {
        when(postService.addComment(anyLong(), anyString(), any(NewCommentDto.class))).thenReturn(postDto);

        mockMvc.perform(patch("/forum/post/1/comment/testUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.comments[0].message").value("Test Comment"));
    }

    @Test
    void testFindPostsByAuthor() throws Exception {
        when(postService.findPostsByAuthor(anyString())).thenReturn(Collections.singletonList(postDto));

        mockMvc.perform(get("/forum/posts/author/testAuthor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].author").value("testAuthor"));
    }

    @Test
    void testFindPostsByTags() throws Exception {
        when(postService.findPostsByTags(any())).thenReturn(Collections.singletonList(postDto));

        mockMvc.perform(get("/forum/posts/tags")
                .param("values", "tag1", "tag2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tags[0]").value("tag1"));
    }

    @Test
    void testFindPostsByPeriod() throws Exception {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        when(postService.findPostsByPeriod(eq(from), eq(to))).thenReturn(Collections.singletonList(postDto));

        mockMvc.perform(get("/forum/posts/period")
                .param("dateFrom", from.toString())
                .param("dateTo", to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
