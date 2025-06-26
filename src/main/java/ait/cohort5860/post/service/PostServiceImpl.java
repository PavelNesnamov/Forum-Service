package ait.cohort5860.post.service;

import ait.cohort5860.post.dao.PostRepository;
import ait.cohort5860.post.dao.TagRepository;
import ait.cohort5860.post.dto.NewCommentDto;
import ait.cohort5860.post.dto.NewPostDto;
import ait.cohort5860.post.dto.PostDto;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import ait.cohort5860.post.dto.exception.PostNotFoundException;
import ait.cohort5860.post.model.Comment;
import ait.cohort5860.post.model.Post;
import ait.cohort5860.post.model.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public PostDto addNewPost(String author, NewPostDto newPostDto) {
        Post post = new Post(newPostDto.getTitle(), newPostDto.getContent(), author);
        Set<String> tags = newPostDto.getTags();
        if (tags != null) {
            for (String tagName : tags) {
                Tag tag = tagRepository.findById(tagName).orElseGet(() -> tagRepository.save(new Tag(tagName)));
                post.addTag(tag);
            }
        }
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto findPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public void addLike(Long id) {

    }

    @Override
    public PostDto updatePost(Long id, NewPostDto newPostDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        post.setTitle(newPostDto.getTitle());
        post.setContent(newPostDto.getContent());
        postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }


    @Override
    public PostDto deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        postRepository.delete(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto addComment(Long id, String author, NewCommentDto newCommendDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Comment comment = new Comment(newCommendDto.getMessage(), author);
        post.addComment(comment);
        postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public Iterable<PostDto> findPostsByAuthor(String author) {
        List<Post> posts = postRepository.findByAuthor(author);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
    }


    @Override
    public Iterable<PostDto> findPostsByTags(List<String> tag) {
        return null;
    }

    @Override
    public Iterable<PostDto> findPostsByPeriod(LocalDate dateFrom, LocalDate dateTo) {
        return null;
    }

    @Override
    public PostDto addComment(Long postId, NewCommentDto commentDto) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        Comment comment = new Comment(commentDto.getMessage(), commentDto.getAuthor());
        post.addComment(comment);
        postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }
}