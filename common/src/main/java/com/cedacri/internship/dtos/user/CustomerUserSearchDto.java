package com.cedacri.internship.dtos.user;

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
public class CustomerUserSearchDto extends BaseSearchDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;

    @Override
    public List<SearchCriteria> toCriteria() {
        return Arrays.asList(
                new SearchCriteria("username", LIKE, username),
                new SearchCriteria("person.firstName", LIKE, firstName),
                new SearchCriteria("person.lastName", LIKE, lastName),
                new SearchCriteria("person.email", LIKE, email),
                new SearchCriteria("enabled", EQ, enabled)
        );
    }
}
