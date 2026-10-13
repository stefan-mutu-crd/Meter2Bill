package com.cedacri.internship.repositories.specification;

import com.cedacri.internship.models.specification.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BaseRepository<T, ID>
        extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    default List<T> search(List<SearchCriteria> criteria) {
        return findAll(SpecificationBuilder.allOf(criteria));
    }

    default Page<T> search(List<SearchCriteria> criteria, Pageable pageable) {
        return findAll(SpecificationBuilder.allOf(criteria), pageable);
    }

    default long count(List<SearchCriteria> criteria) {
        return count(SpecificationBuilder.allOf(criteria));
    }
}
