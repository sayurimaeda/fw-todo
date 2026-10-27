package com.example.todoapp;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TodoMapper {

    List<Todo> search(@Param("keyword") String keyword,
            @Param("category") String category,
            @Param("order") String order,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    List<Todo> searchPage(@Param("keyword") String keyword,
            @Param("category") String category,
            @Param("order") String order,
            @Param("showCompleted") boolean showCompleted,
            @Param("limit") int limit,
            @Param("offset") int offset);

    int countSearch(@Param("keyword") String keyword,
            @Param("category") String category,
            @Param("showCompleted") boolean showCompleted);

    List<Todo> searchTrashPage(@Param("limit") int limit, @Param("offset") int offset);

    int countTrash();

    Todo findById(Long id);

    int insert(Todo todo);

    int update(Todo todo);

    int markDeletedById(Long id);

    int restoreById(Long id);

    int updatePinned(@Param("id") Long id, @Param("pinned") boolean pinned);

}
