package ait.cohort5860.post.controller;

import ait.cohort5860.post.dto.NewCommentDto;
import ait.cohort5860.post.dto.NewPostDto;
import ait.cohort5860.post.dto.PostDto;
import ait.cohort5860.post.service.PostService;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor("/forum")
@RequestMapping
public class PostController {
    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto addNewPost(@RequestParam String author, @RequestBody NewPostDto newPostDto) {
        return postService.addNewPost(author, newPostDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> findPostById(@PathVariable Long id) {
        PostDto post  = postService.findPostById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(Long id) {

    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @RequestBody NewPostDto newPostDto) {
        PostDto post = postService.updatePost(id, newPostDto);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    public PostDto deletePost(Long id) {
        return null;
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<PostDto> addComment(@PathVariable Long id, @RequestBody NewCommentDto newCommentDto) {
        PostDto post = postService.addComment(id, newCommentDto);
        return post != null
                ? ResponseEntity.ok(post)
                : ResponseEntity.notFound().build();
    }



    public Iterable<PostDto> findPostsByAuthor(String author) {
        return null;
    }


    public Iterable<PostDto> findPostsByTags(List<String> tag) {
        return null;
    }


    public Iterable<PostDto> findPostsByPeriod(LocalDate dateFrom, LocalDate dateTo) {
        return null;
    }
}
