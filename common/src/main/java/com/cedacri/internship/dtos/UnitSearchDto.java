package com.cedacri.internship.dtos;

import com.cedacri.internship.edums.TenantType;
import com.cedacri.internship.edums.UnitType;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.models.specification.SearchCriteria;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import static com.cedacri.internship.edums.specification.Operation.EQ;
import static com.cedacri.internship.edums.specification.Operation.IS_NULL;
import static com.cedacri.internship.edums.specification.Operation.LIKE;

@Getter
@Setter
public class UnitSearchDto extends BaseSearchDto {
    private String name;
    private UnitType unitType;
    private TenantType tenantType;
    private Unit parent;
    private boolean topLevel;

    @Override
    public List<SearchCriteria> toCriteria() {
        return Arrays.asList(
                new SearchCriteria("name", LIKE, name),
                new SearchCriteria("unitType", EQ, unitType),
                new SearchCriteria("tenantType", EQ, tenantType),
                new SearchCriteria("parent", EQ, parent),
                new SearchCriteria("parent", IS_NULL, topLevel ? Boolean.TRUE : null)
        );
    }
}
