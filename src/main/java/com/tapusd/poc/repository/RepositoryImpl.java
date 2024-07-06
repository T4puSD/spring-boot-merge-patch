package com.tapusd.poc.repository;

import com.tapusd.poc.domian.Post;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class RepositoryImpl implements Repository {
    List<Post> lists = new LinkedList<>();

    @Override
    public Post save(Post entity) {
        Long id = getId(entity);
        if (Objects.nonNull(id)) {
            entity.setId(id);
            lists.set((int) (id - 1), entity);
        } else {
            setId(entity, (lists.size() + 1L));
            lists.add(entity);
        }
        return entity;
    }

    @Override
    public List<Post> findAll() {
        return lists;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return lists.stream().filter(e -> getId(e).equals(id)).findFirst();
    }

    @Override
    public void deleteById(Long id) {
        lists = lists.stream().filter(e -> !getId(e).equals(id)).toList();
    }

    @SuppressWarnings("unchecked")
    private Long getId(Post entity) {
        PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);
        return (Long) accessor.getPropertyValue("id");
    }

    private void setId(Post entity, long id) {
        PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(entity);
        accessor.setPropertyValue("id", id);
    }
}
