package com.tapusd.poc.repository;

import com.tapusd.poc.domian.Post;

import java.util.List;
import java.util.Optional;

public interface Repository {
    Post save(Post entity);
    List<Post> findAll();
    Optional<Post> findById(Long id);
    void deleteById(Long id);
}
