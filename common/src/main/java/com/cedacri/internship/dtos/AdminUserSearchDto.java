package com.cedacri.internship.dtos;

import com.cedacri.internship.edums.AdminUserRole;
import com.cedacri.internship.models.specification.SearchCriteria;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.cedacri.internship.edums.specification.Operation.EQ;
import static com.cedacri.internship.edums.specification.Operation.LIKE;

@Getter
@Setter
public class AdminUserSearchDto extends BaseSearchDto {

    private String username;

    private String fullName;

    private AdminUserRole role;

    private boolean isEnabled = true;

    @Override
    public List<SearchCriteria> toCriteria() {
        return List.of(
                new SearchCriteria("username", LIKE, username),
                new SearchCriteria("fullName", LIKE, fullName),
                new SearchCriteria("role", EQ, role),
                new SearchCriteria("enabled", EQ, isEnabled)
        );
    }
}
