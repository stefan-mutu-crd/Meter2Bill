package com.cedacri.internship.dtos.contract;

import com.cedacri.internship.dtos.BaseSearchDto;
import com.cedacri.internship.models.specification.SearchCriteria;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import static com.cedacri.internship.edums.specification.Operation.EQ;
import static com.cedacri.internship.edums.specification.Operation.LIKE;

@Getter
@Setter
public class ContractorSearchDto extends BaseSearchDto {
    private String companyName;
    private String legalName;
    private String fiscalCode;
    private Boolean active;

    @Override
    public List<SearchCriteria> toCriteria() {
        return Arrays.asList(
                new SearchCriteria("companyName", LIKE, companyName),
                new SearchCriteria("legalName", LIKE, legalName),
                new SearchCriteria("fiscalCode", LIKE, fiscalCode),
                new SearchCriteria("active", EQ, active)
        );
    }
}
