package com.cedacri.internship.dtos;

import com.cedacri.internship.models.specification.SearchCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public abstract class BaseSearchDto {

    private int page;
    private int size;
    private String sort = "id";
    Sort.Direction direction = Sort.Direction.ASC;
    //TODO add sort direction

    public abstract List<SearchCriteria> toCriteria();

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sort));
    }

}
