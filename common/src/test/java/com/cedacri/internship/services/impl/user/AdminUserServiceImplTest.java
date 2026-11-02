package com.cedacri.internship.services.impl.user;

import com.cedacri.internship.TestData;
import com.cedacri.internship.dtos.user.AdminUserSearchDto;
import com.cedacri.internship.edums.AdminUserRole;
import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.services.user.AdminUserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AdminUserServiceImplTest {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private TestData data;

    @BeforeEach
    void setUp() {
        data.cleanDb();
    }

    @Test
    void findByUsernameReturnsUser() {
        AdminUser admin = data.adminUser();

        assertThat(adminUserService.findByUsername(admin.getUsername()).getId()).isEqualTo(admin.getId());
    }

    @Test
    void findByUsernameThrowsWhenMissing() {
        data.adminUser();

        assertThatThrownBy(() -> adminUserService.findByUsername("missing"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThatThrownBy(() -> adminUserService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("AdminUser");
    }

    @Test
    void createPersistsUser() {
        AdminUser admin = AdminUser.builder().fullName("New Admin").role(AdminUserRole.AUDIT).build();
        admin.setUsername("new.admin");
        admin.setPassword("secret");

        AdminUser created = adminUserService.create(admin);

        AdminUser found = adminUserService.findById(created.getId());
        assertThat(found.getUsername()).isEqualTo("new.admin");
    }

    @Test
    void updateAppliesChanges() {
        AdminUser admin = data.adminUser();

        adminUserService.update(admin.getId(), existing -> existing.setFullName("Renamed"));

        assertThat(adminUserService.findById(admin.getId()).getFullName()).isEqualTo("Renamed");
    }

    @Test
    void deleteRemovesUser() {
        AdminUser admin = data.adminUser();

        adminUserService.delete(admin.getId());

        assertThatThrownBy(() -> adminUserService.findById(admin.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void searchPageFiltersByUsername() {
        AdminUser admin = data.adminUser();
        data.adminUser();
        AdminUserSearchDto search = new AdminUserSearchDto();
        search.setUsername(admin.getUsername());

        assertThat(adminUserService.searchPage(search).getContent())
                .extracting(AdminUser::getId)
                .containsExactly(admin.getId());
        assertThat(adminUserService.count(new AdminUserSearchDto())).isEqualTo(2);
    }
}
