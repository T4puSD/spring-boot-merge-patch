package com.tapusd.poc.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.tapusd.poc.domian.Post;
import com.tapusd.poc.repository.Repository;
import com.tapusd.poc.util.PatchUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final Repository repository;

    public PostController(Repository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Post save(@RequestBody Post post) {
        return repository.save(post);
    }

    @GetMapping
    public List<Post> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Post findById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PatchMapping("/{id}")
    public Post updatePartial(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Post post = repository.findById(id).orElseThrow();
        PatchUtil.applyPatch(post, body);
        return save(post);
    }
}
