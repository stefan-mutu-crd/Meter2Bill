package com.cedacri.internship.dtos.contract;

import com.cedacri.internship.dtos.BaseSearchDto;
import com.cedacri.internship.entities.Unit;
import com.cedacri.internship.entities.contract.ContractTemplate;
import com.cedacri.internship.entities.user.CustomerUser;
import com.cedacri.internship.models.specification.SearchCriteria;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.cedacri.internship.edums.specification.Operation.EQ;
import static com.cedacri.internship.edums.specification.Operation.GTE;
import static com.cedacri.internship.edums.specification.Operation.LTE;

@Getter
@Setter
public class ContractSearchDto extends BaseSearchDto {
    private CustomerUser customer;
    private Unit unit;
    private ContractTemplate contractTemplate;
    private LocalDate activeOn;

    @Override
    public List<SearchCriteria> toCriteria() {
        return Arrays.asList(
                new SearchCriteria("customer", EQ, customer),
                new SearchCriteria("unit", EQ, unit),
                new SearchCriteria("contractTemplate", EQ, contractTemplate),
                new SearchCriteria("dateStart", LTE, activeOn),
                new SearchCriteria("dateEnd", GTE, activeOn)
        );
    }
}
