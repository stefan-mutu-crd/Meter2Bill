package com.cedacri.internship.dtos;

import com.cedacri.internship.edums.UnitOfMeasure;
import com.cedacri.internship.edums.UtilityType;
import com.cedacri.internship.models.specification.SearchCriteria;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import static com.cedacri.internship.edums.specification.Operation.EQ;

@Getter
@Setter
public class UtilitySearchDto extends BaseSearchDto {
    private UtilityType type;
    private UnitOfMeasure unitOfMeasure;

    @Override
    public List<SearchCriteria> toCriteria() {
        return Arrays.asList(
                new SearchCriteria("type", EQ, type),
                new SearchCriteria("unitOfMeasure", EQ, unitOfMeasure)
        );
    }
}
