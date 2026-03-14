package io.github.wolfandw.mymarket;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.*;

import static io.github.wolfandw.mymarket.service.impl.UserServiceImpl.ROLE_ADMIN;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithMockUser(roles = ROLE_ADMIN)
public @interface IsRoleAdmin {
}
