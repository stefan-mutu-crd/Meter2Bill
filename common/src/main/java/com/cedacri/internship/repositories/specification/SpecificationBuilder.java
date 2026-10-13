package com.cedacri.internship.repositories.specification;

import com.cedacri.internship.models.specification.SearchCriteria;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

public final class SpecificationBuilder {

    private SpecificationBuilder() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Specification<T> of(SearchCriteria sc) {
        return (root, query, cb) -> {

            Path<Object> path = root.get(sc.field());
            Object value = sc.value();

            return switch (sc.operation()) {
                case EQ -> cb.equal(path, value);
                case NOT_EQ -> cb.notEqual(path, value);
                // exceptie Path trebuie sa fie <String>
                case LIKE -> cb.like(cb.lower(root.get(sc.field())), "%" + sc.value().toString().toLowerCase() + "%");
                case GT -> cb.greaterThan((Expression<Comparable>) (Expression) path, (Comparable) value);
                case GTE -> cb.greaterThanOrEqualTo((Expression<Comparable>) (Expression) path, (Comparable) value);
                case LT -> cb.lessThan((Expression<Comparable>) (Expression) path, (Comparable) value);
                case LTE -> cb.lessThanOrEqualTo((Expression<Comparable>) (Expression) path, (Comparable) value);
                case IN -> path.in((Collection<?>) value);
                case IS_NULL -> cb.isNull(path);
                case IS_NOT_NULL -> cb.isNotNull(path);
            };
        };
    }

    public static <T> Specification<T> allOf(List<SearchCriteria> criteria) {
        Specification<T> result = (root, q, cb) -> cb.conjunction();
        if (criteria == null) return result;
        for (SearchCriteria c : criteria) {
            result = result.and(of(c));
        }
        return result;
    }
}
