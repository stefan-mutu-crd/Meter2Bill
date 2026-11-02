package com.cedacri.internship.dtos.contract;

import com.cedacri.internship.dtos.BaseSearchDto;
import com.cedacri.internship.edums.PaymentPlanType;
import com.cedacri.internship.models.specification.SearchCriteria;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import static com.cedacri.internship.edums.specification.Operation.EQ;

@Getter
@Setter
public class ContractTemplateSearchDto extends BaseSearchDto {
    private PaymentPlanType paymentPlanType;
    private Boolean automaticRenewal;

    @Override
    public List<SearchCriteria> toCriteria() {
        return Arrays.asList(
                new SearchCriteria("paymentPlanType", EQ, paymentPlanType),
                new SearchCriteria("automaticRenewal", EQ, automaticRenewal)
        );
    }
}
