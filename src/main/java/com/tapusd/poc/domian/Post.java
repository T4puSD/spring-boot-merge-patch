package com.tapusd.poc.domian;

import java.util.Set;

public class Post implements Cloneable {
    private Long id;
    private String author;
    private String title;
    private String content;
    private Set<String> tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public Post clone() {
        try {
            Post post = (Post) super.clone();
            post.setId(id);
            post.setAuthor(author);
            post.setTitle(title);
            post.setContent(content);
            post.setTags(tags);
            return post;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
