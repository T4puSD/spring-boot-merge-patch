package com.tapusd.poc.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PatchUtilTest {

    @Test
    void applyPatch() {
        Book book = new Book(1L, "Book 1", "T4pusd", "Akota Publishers", 1, 100.5, 4.5);

        Map<String, Object> patchRequest = new HashMap<>();
        patchRequest.put("title", "bookuyaaa");
        patchRequest.put("author", "Nolan Jr.");
        patchRequest.put("publisher", null);
        patchRequest.put("isbn", null);
        patchRequest.put("price", "null");
        patchRequest.put("rating", "");

        PatchUtil.applyPatch(book, patchRequest);

        assertThat(book)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", "bookuyaaa")
                .hasFieldOrPropertyWithValue("author", "Nolan Jr.")
                .hasFieldOrPropertyWithValue("publisher", null)
                .hasFieldOrPropertyWithValue("isbn",0)
                .hasFieldOrPropertyWithValue("price", null)
                .hasFieldOrPropertyWithValue("rating", 0D);
    }

    static class Book {
        private Long id;
        private String title;
        private String author;
        private String publisher;
        private int isbn;
        private Double price;
        private double rating;

        public Book(Long id, String title, String author, String publisher, int isbn, Double price, double rating) {
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

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
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

    @Test
    void defaultValue() {
        Object stringDefaultValue = PatchUtil.getDefaultValueForType(String.class);
        assertThat(stringDefaultValue).isNull();

        Object bookDefaultValue = PatchUtil.getDefaultValueForType(Book.class);
        assertThat(bookDefaultValue).isNull();

        Object integerDefaultValue = PatchUtil.getDefaultValueForType(Integer.class);
        assertThat(integerDefaultValue).isNull();

        Object primitiveIntDefaultValue = PatchUtil.getDefaultValueForType(int.class);
        assertThat(primitiveIntDefaultValue).isEqualTo(0);

        Object longDefaultValue = PatchUtil.getDefaultValueForType(Long.class);
        assertThat(longDefaultValue).isNull();

        Object primitiveLongDefaultValue = PatchUtil.getDefaultValueForType(long.class);
        assertThat(primitiveLongDefaultValue).isEqualTo(0L);

        Object shortDefaultValue = PatchUtil.getDefaultValueForType(short.class);
        assertThat(shortDefaultValue).isEqualTo(0);

        Object byteDefaultValue = PatchUtil.getDefaultValueForType(byte.class);
        assertThat(byteDefaultValue).isEqualTo(0);

        Object floatDefaultValue = PatchUtil.getDefaultValueForType(float.class);
        assertThat(floatDefaultValue).isEqualTo(0F);

        Object doubleDefaultValue = PatchUtil.getDefaultValueForType(double.class);
        assertThat(doubleDefaultValue).isEqualTo(0D);

        Object booleanDefaultValue = PatchUtil.getDefaultValueForType(boolean.class);
        assertThat(booleanDefaultValue).isEqualTo(false);

        Object primitiveBooleanDefaultValue = PatchUtil.getDefaultValueForType(Boolean.class);
        assertThat(primitiveBooleanDefaultValue).isNull();;

        Object charDefaultValue = PatchUtil.getDefaultValueForType(char.class);
        assertThat(charDefaultValue).isEqualTo('\u0000');
    }
}