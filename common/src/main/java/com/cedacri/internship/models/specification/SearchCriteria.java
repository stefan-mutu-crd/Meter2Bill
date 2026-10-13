package com.cedacri.internship.models.specification;

import com.cedacri.internship.edums.specification.Operation;

public record SearchCriteria(String field, Operation operation, Object value) {
}
