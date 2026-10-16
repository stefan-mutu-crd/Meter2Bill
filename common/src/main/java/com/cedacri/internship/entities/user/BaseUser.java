package com.cedacri.internship.entities.user;

import com.cedacri.internship.entities.AbstractAuditable;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@MappedSuperclass
@ToString(onlyExplicitlyIncluded = true)
public class BaseUser extends AbstractAuditable {

    @Column(nullable = false, unique = true, length = 50)
    @ToString.Include
    protected String username;

    @Column(nullable = false, length = 100)
    protected String password;

    @ToString.Include
    @Column(nullable = false)
    protected boolean enabled = true;
}
