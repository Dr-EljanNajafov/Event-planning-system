package com.github.eventplanningsystem.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


//Need only for test
@Component
public class DefaultAdminLoader {
    @Autowired
    public DefaultAdminLoader(AdminUserService service) {
        service.registerUser(new RegisterByAdminRequest("admin", "admin", true));
    }
}