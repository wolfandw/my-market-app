package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.model.User;
import io.github.wolfandw.mymarket.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static io.github.wolfandw.mymarket.service.impl.UserServiceImpl.ROLE_ROLE_ADMIN;
import static io.github.wolfandw.mymarket.service.impl.UserServiceImpl.ROLE_ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса пользователей.
 */
public class UserServiceTest extends AbstractServiceTest {
    @Test
    public void findByUsernameAdminTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getAdminMono());

        StepVerifier.create(userService.findByUsername(USERNAME_ADMIN)).
                assertNext(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo(getAdmin().getUsername());
                    assertThat(userDetails.getPassword()).isEqualTo(getAdmin().getPassword());
                    assertThat(userDetails.getAuthorities().stream().anyMatch(ga -> ROLE_ROLE_USER.equals(ga.getAuthority()))).isTrue();
                    assertThat(userDetails.getAuthorities().stream().anyMatch(ga -> ROLE_ROLE_ADMIN.equals(ga.getAuthority()))).isTrue();
                }).verifyComplete();
    }

    @Test
    public void findByUsernameUserTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getUserMono());

        StepVerifier.create(userService.findByUsername(USERNAME_USER)).
                assertNext(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo(getUser().getUsername());
                    assertThat(userDetails.getPassword()).isEqualTo(getUser().getPassword());
                    assertThat(userDetails.getAuthorities().stream().anyMatch(ga -> ROLE_ROLE_USER.equals(ga.getAuthority()))).isTrue();
                }).verifyComplete();
    }

    @Test
    public void findByUsernameGuestTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getGuestMono());
        StepVerifier.create(userService.findByUsername(USERNAME_GUEST)).verifyComplete();
    }

    @Test
    public void updatePasswordTest() {
        String newPassword = "new-password";
        User adminToUpdatePassword = createAdmin();

        when(userRepository.findByUsername(any(String.class))).thenReturn(Mono.just(adminToUpdatePassword));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(adminToUpdatePassword));

        UserDetails userDetailsToUpdatePassword = UserServiceImpl.fillUserDetails(getAdmin());
        StepVerifier.create(userService.updatePassword(userDetailsToUpdatePassword, newPassword)).
                assertNext(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo(adminToUpdatePassword.getUsername());
                    assertThat(userDetails.getPassword()).isEqualTo(passwordEncoder.encode(newPassword));
                    assertThat(userDetails.getAuthorities().stream().anyMatch(ga -> ROLE_ROLE_USER.equals(ga.getAuthority()))).isTrue();
                    assertThat(userDetails.getAuthorities().stream().anyMatch(ga -> ROLE_ROLE_ADMIN.equals(ga.getAuthority()))).isTrue();

                }).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void getCurrentUserNameTest() {
        StepVerifier.create(userService.getCurrentUserName()).assertNext(username ->
                assertThat(username).isEqualTo(getUser().getUsername())).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    public void getCurrentUserIdAdminTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getAdminMono());
        StepVerifier.create(userService.getCurrentUserId()).assertNext(userId ->
                assertThat(userId).isEqualTo(getAdmin().getId())).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void getCurrentUserIdUserTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getUserMono());

        StepVerifier.create(userService.getCurrentUserId()).assertNext(userId ->
                assertThat(userId).isEqualTo(getUser().getId())).verifyComplete();
    }

    @Test
    @IsRoleGuest
    public void getCurrentUserIdGuestTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getGuestMono());
        StepVerifier.create(userService.getCurrentUserId()).assertNext(userId ->
                assertThat(userId).isEqualTo(ID_GUEST)).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    public void isCurrentUserAdminTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getAdminMono());

        StepVerifier.create(userService.isCurrentUserAdmin()).assertNext(is -> assertThat(is).isTrue()).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void isCurrentUserUserTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getUserMono());

        StepVerifier.create(userService.isCurrentUserUser()).assertNext(is -> assertThat(is).isTrue()).verifyComplete();
    }

    @Test
    @IsRoleGuest
    public void isCurrentUserGuestTest() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(getGuestMono());

        StepVerifier.create(userService.isCurrentUserUser()).assertNext(is -> assertThat(is).isFalse()).verifyComplete();
        StepVerifier.create(userService.isCurrentUserAdmin()).assertNext(is -> assertThat(is).isFalse()).verifyComplete();
    }
}
