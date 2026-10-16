package com.cedacri.internship.dtos;

import com.cedacri.internship.models.specification.SearchCriteria;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
public abstract class BaseSearchDto {

    private int page = 0;
    private int size = 20;
    private String sort = "id";
    Sort.Direction direction = Sort.Direction.ASC;

    public abstract List<SearchCriteria> toCriteria();

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, sort));
    }

}
