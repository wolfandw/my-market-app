package io.github.wolfandw.mymarket;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.*;

import static io.github.wolfandw.mymarket.service.impl.UserServiceImpl.ROLE_GUEST;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithMockUser(roles = ROLE_GUEST)
public @interface IsRoleGuest {
}
