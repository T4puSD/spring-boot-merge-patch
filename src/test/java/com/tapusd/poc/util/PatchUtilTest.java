package com.tapusd.poc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PatchUtilTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void applyPatch_empty_string_and_null_value_both_should_replace_with_null_after_patch () throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("a", "b");
        map.put("c", "d");

        String patchValue = """
                {
                  "a": "",
                  "c": null
                }
                """;

        JsonNode jsonNode = OBJECT_MAPPER.convertValue(map, JsonNode.class);
        JsonNode patch = OBJECT_MAPPER.readTree(patchValue);
        Map<String, Object> updatedMap = PatchUtil.applyPatch(map, patch);

        assertThat(updatedMap)
                .isNotNull()
                .hasFieldOrPropertyWithValue("a", null)
                .hasFieldOrPropertyWithValue("c", null);
    }

    @Test
    void applyPatch_test_on_multiple_collection_type_should_replace_collection_with_updated_value() throws JsonProcessingException {
        Coll coll = new Coll();
        coll.setArray(new int[]{1, 2, 3});
        coll.setList(List.of("a", "b", "c"));
        coll.setSet(Set.of(1,2,3));

        Coll2 coll2 = new Coll2();
        coll2.setA("b");
        coll2.setC("d");
        coll2.setE("f");
        coll2.setG("h");

        coll.setMap(coll2);

        String patchValue = """
                {
                  "array": [5, 6],
                  "list": [6, 7],
                  "set": [2, 2, 3, 4],
                  "map": {
                    "a": "It's A",
                    "c": "",
                    "e": null
                  }
                }
                """;

        JsonNode patch = OBJECT_MAPPER.readTree(patchValue);
        Coll updatedCol = PatchUtil.applyPatch(coll, patch);

        assertThat(updatedCol).isNotNull()
                .hasFieldOrPropertyWithValue("array", new int[]{5, 6})
                .hasFieldOrPropertyWithValue("list", List.of("6", "7"))
                .hasFieldOrPropertyWithValue("set", Set.of(2, 3, 4));

        Coll2 coll2Updated = updatedCol.getMap();

        assertThat(coll2Updated).isNotNull()
                .hasFieldOrPropertyWithValue("a", "It's A")
                .hasFieldOrPropertyWithValue("c", null)
                .hasFieldOrPropertyWithValue("e", null)
                .hasFieldOrPropertyWithValue("g", "h");

    }

    static class Coll {
        private int[] array;
        private List<String> list;
        private Set<Integer> set;
        private Coll2 map;

        public int[] getArray() {
            return array;
        }

        public void setArray(int[] array) {
            this.array = array;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public Set<Integer> getSet() {
            return set;
        }

        public void setSet(Set<Integer> set) {
            this.set = set;
        }

        public Coll2 getMap() {
            return map;
        }

        public void setMap(Coll2 map) {
            this.map = map;
        }
    }

    static class Coll2 {
        private String a;
        private String c;
        private String e;
        private String g;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getE() {
            return e;
        }

        public void setE(String e) {
            this.e = e;
        }

        public String getG() {
            return g;
        }

        public void setG(String g) {
            this.g = g;
        }
    }

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
    void applyPatch_nested_objects_should_pass() throws JsonProcessingException {
        Book book = new Book(1L, "Book 1",
                new Author("Nolan Jr.", "Renowned Poet of the modern era!"),
                List.of(new Author("Nolan Jr.", "Renowned Poet of the modern era!"),
                        new Author("William Smith", "All time best romantic author!")
                ),
                "Akota Publishers", 1, 100.5, 4.5);

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
                .hasFieldOrPropertyWithValue("isbn", 1)
                .hasFieldOrPropertyWithValue("price", 100.5)
                .hasFieldOrPropertyWithValue("rating", 0D);
    }

    @Test
    void applyPatch_on_list_should_add_to_existing_array_and_increment_size_instead_of_replacing_existing_list() throws JsonProcessingException {
        Book book = new Book(1L, "Book 1",
                new Author("Nolan Jr.", "Renowned Poet of the modern era!"),
                List.of(new Author("Nolan Jr.", "Renowned Poet of the modern era!"),
                        new Author("William Smith", "All time best romantic author!")
                ),
                "Akota Publishers", 1, 100.5, 4.5);

        String patchValue = """
                {
                  "title": "bookuyaaa",
                  "author": {"name": "Jaksmith Rigdbi","description": null},
                  "secondaryAuthors": [{"name": "Nolan Jr 2"}],
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
                .hasFieldOrPropertyWithValue("isbn", 1)
                .hasFieldOrPropertyWithValue("price", 100.5)
                .hasFieldOrPropertyWithValue("rating", 0D);

        List<Author> secondaryAuthors = updatedBook.getSecondaryAuthors();
        assertThat(secondaryAuthors).isNotEmpty()
                .hasSize(1);

        Author sa1 = secondaryAuthors.get(0);
        assertThat(sa1)
                .hasFieldOrPropertyWithValue("name", "Nolan Jr 2");
    }

    static class Book {
        private Long id;
        private String title;
        private Author author;
        private List<Author> secondaryAuthors;
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

        public Book(Long id, String title, Author author, List<Author> secondaryAuthors, String publisher, int isbn, Double price, double rating) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.secondaryAuthors = secondaryAuthors;
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

        public List<Author> getSecondaryAuthors() {
            return secondaryAuthors;
        }

        public void setSecondaryAuthors(List<Author> secondaryAuthors) {
            this.secondaryAuthors = secondaryAuthors;
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

        public Author() {
        }

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