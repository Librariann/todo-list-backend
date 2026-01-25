package com.librarian.todo_list.todos.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodosOrderUpdateRequest implements Serializable {
    private List<IndexItem> indexIds;
    LocalDate targetDate;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexItem {
        private Long id;
    }
}