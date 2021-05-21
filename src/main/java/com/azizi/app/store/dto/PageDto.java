package com.azizi.app.store.dto;

import static java.util.Collections.emptyList;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@AllArgsConstructor
public class PageDto<T> {

    private List<T> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;

    public static <T> PageDto<T> of(Page<T> page) {
        return new PageDto<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements());
    }

    public static <T> PageDto<T> of(List<T> content, Pageable pageable, Long totalElements) {
        return new PageDto<>(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements);
    }

    public static <T> PageDto<T> of(List<T> content, Integer pageNumber, Integer pageSize, Long totalElements) {
        return new PageDto<>(content, pageNumber, pageSize, totalElements);
    }

    public static <T> PageDto<T> empty() {
        return new PageDto<>(emptyList(), 0, 0, 0L);
    }

}
