package com.tapusd.poc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PatchUtilTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void applyPatch_invalid_key_not_present_in_class_should_throw_exception() throws JsonProcessingException {
        Book book = new Book(1L, "Book 1", new Author("Nolan Jr.", "Renowned Poet of the modern era!"), "Akota Publishers", 1, 100.5, 4.5);

        String patchValue = """
                {
                  "ababaab": "abababba"
                }
                """;

        JsonNode patchRequest = OBJECT_MAPPER.readTree(patchValue);

        assertThatThrownBy(() -> {
            PatchUtil.applyPatch(book, patchRequest);
        }).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void applyPatch_patch_request_contains_id_should_throw_exception() throws JsonProcessingException {
        Book book = new Book(1L, "Book 1", new Author("Nolan Jr.", "Renowned Poet of the modern era!"), "Akota Publishers", 1, 100.5, 4.5);

        String patchValue = """
                {
                  "id": 20,
                  "title": "bookuyaaa"
                }
                """;

        JsonNode patchRequest = OBJECT_MAPPER.readTree(patchValue);

        assertThatThrownBy(() -> {
            PatchUtil.applyPatch(book, patchRequest);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void applyPatch() throws JsonProcessingException {
        Book book = new Book(1L, "Book 1", new Author("Nolan Jr.", "Renowned Poet of the modern era!"), "Akota Publishers", 1, 100.5, 4.5);

        String patchValue = """
                {
                  "title": "bookuyaaa",
                  "author": {"name": "Jaksmith Rigdbi","description": null},
                  "publisher": null,
                  "rating": ""
                }
                """;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patchRequest = objectMapper.readTree(patchValue);
        Book updatedBook = PatchUtil.applyPatch(book, patchRequest);

        assertThat(updatedBook)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", "bookuyaaa")
                .hasFieldOrPropertyWithValue("author.name", "Jaksmith Rigdbi")
                .hasFieldOrPropertyWithValue("author.description", null)
                .hasFieldOrPropertyWithValue("publisher", null)
                .hasFieldOrPropertyWithValue("isbn",1)
                .hasFieldOrPropertyWithValue("price", 100.5)
                .hasFieldOrPropertyWithValue("rating", 0D);
    }

    static class Book {
        private Long id;
        private String title;
        private Author author;
        private String publisher;
        private int isbn;
        private Double price;
        private double rating;

        public Book() {
        }

        public Book(Long id, String title, Author author, String publisher, int isbn, Double price, double rating) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.isbn = isbn;
            this.price = price;
            this.rating = rating;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public int getIsbn() {
            return isbn;
        }

        public void setIsbn(int isbn) {
            this.isbn = isbn;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }
    }

    static class Author {
        private String name;
        private String description;

        public Author() {}

        public Author(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}