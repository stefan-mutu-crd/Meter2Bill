package com.cedacri.internship.repositories;

import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.repositories.specification.BaseRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends BaseRepository<Unit, Long> {

    @EntityGraph(attributePaths = "tenant")
    List<Unit> findByParentIdOrderByNameAsc(Long parentId);

    boolean existsByParentId(Long parentId);

    boolean existsByNameIgnoreCaseAndParentId(String name, Long parentId);

    boolean existsByNameIgnoreCaseAndParentIdAndIdNot(String name, Long parentId, Long id);

    boolean existsByNameIgnoreCaseAndParentIsNull(String name);

    boolean existsByNameIgnoreCaseAndParentIsNullAndIdNot(String name, Long id);
}
