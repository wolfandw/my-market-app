package io.github.wolfandw.mymarket.test;

import io.github.wolfandw.mymarket.dto.UserInfoDto;
import io.github.wolfandw.mymarket.model.User;
import io.github.wolfandw.mymarket.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.TreeMap;

import static io.github.wolfandw.mymarket.service.impl.UserServiceImpl.ROLE_ADMIN;
import static io.github.wolfandw.mymarket.service.impl.UserServiceImpl.ROLE_USER;

/**
 * Абстрактный тест.
 */
public abstract class AbstractSecurityTest {
    /**
     * Идентификатор пользователя по-умолчанию.
     */
    public static final String PASSWORD_ADMIN = "$2a$12$m5dnhoX3cf2zjEph.H/42e7lEqbd/Dmdiqg5R/kyWUTELuVdHHfIW";
    public static final String PASSWORD_USER = "$2a$12$gSx1V/Q95xi8OjdfgrSx1.8bkYBjI75lKoY0SJZmMZbd0R6aY12Ky";

    /**
     * Тестовые данные пользователей.
     */
    public static Map<Long, User> USERS = new TreeMap<>();

    public static final Long ID_ADMIN = 1L;
    public static final Long ID_USER = 2L;
    public static final Long ID_GUEST = -1L;

    public static final String USERNAME_ADMIN = "admin";
    public static final String USERNAME_USER = "user";
    public static final String USERNAME_GUEST = "guest";

    /**
     * Инициализация перед каждым тестом.
     */
    @BeforeEach
    protected void setUp() {
        USERS.clear();

        User admin = createAdmin();
        USERS.put(admin.getId(), admin);

        User user = createUser();
        USERS.put(user.getId(), user);
    }

    protected User createAdmin() {
        User admin = new User();
        admin.setId(ID_ADMIN);
        admin.setUsername(USERNAME_ADMIN);
        admin.setPassword(PASSWORD_ADMIN);
        admin.setRoles(ROLE_USER + "," + ROLE_ADMIN);
        return admin;
    }

    protected User createUser() {
        User user = new User();
        user.setId(ID_USER);
        user.setUsername(USERNAME_USER);
        user.setPassword(PASSWORD_USER);
        user.setRoles(ROLE_USER);
        return user;
    }

    protected User getAdmin() {
        return USERS.get(ID_ADMIN);
    }

    protected User getUser() {
        return USERS.get(ID_USER);
    }

    protected Mono<User> getAdminMono() {
        return Mono.just(getAdmin());
    }

    protected Mono<User> getUserMono() {
        return  Mono.just(getUser());
    }

    protected Mono<User> getGuestMono() {
        return  Mono.empty();
    }

    protected UserInfoDto getUserInfo() {
        User user = getUser();
        return new UserInfoDto(user.getId(), user.getUsername(), true, false);
    }

    protected Mono<UserInfoDto> getUserInfoMono() {
        return Mono.just(getUserInfo());
    }

    protected UserInfoDto getAdminInfo() {
        User admin = getAdmin();
        return new UserInfoDto(admin.getId(), admin.getUsername(), true, true);
    }

    protected Mono<UserInfoDto> getAdminInfoMono() {
        return Mono.just(getAdminInfo());
    }

    protected UserInfoDto getGuestInfo() {
        return new UserInfoDto(ID_GUEST, USERNAME_GUEST, false, false);
    }

    protected Mono<UserInfoDto> getGuestInfoMono() {
        return Mono.just(getGuestInfo());
    }

    protected org.springframework.security.core.userdetails.User getUserUserDetails() {
        return  UserServiceImpl.fillUserDetails(getUser());
    }

    protected org.springframework.security.core.userdetails.User getAdminUserDetails() {
        return   UserServiceImpl.fillUserDetails(getAdmin());
    }
}
