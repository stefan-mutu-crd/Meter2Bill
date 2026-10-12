package com.cedacri.internship.entities.user;

import com.cedacri.internship.entities.AbstractAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.ToString;

@MappedSuperclass
@ToString(onlyExplicitlyIncluded = true)
public class BaseUser extends AbstractAuditable {

    @Column(nullable = false, unique = true, length = 50)
    @ToString.Include
    private String username;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String password;

    @ToString.Include
    @Column(nullable = false)
    private boolean enabled = true;
}
