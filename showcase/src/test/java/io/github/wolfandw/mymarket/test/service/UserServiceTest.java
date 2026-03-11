package io.github.wolfandw.mymarket.test.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.wolfandw.mymarket.model.User;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульный тест сервиса пользователей.
 */
public class UserServiceTest extends AbstractServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    protected PasswordEncoder passwordEncoder;

    @Test
    void findByUsernameTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");
        user.setRoles("USER");
        when(userRepository.findByUsername(any(String.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.findByUsername("user")).
                assertNext(userDetails -> {
                    Assertions.assertThat(userDetails.getUsername()).isEqualTo("user");
                    Assertions.assertThat(userDetails.getPassword()).isEqualTo("password");
                    Assertions.assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
                }).verifyComplete();
    }

    @Test
    void updatePasswordTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("old-password");
        user.setRoles("USER");
        when(userRepository.findByUsername(any(String.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.encode(any(String.class))).thenReturn("coded-new-password");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        UserDetails mockUserDetails = fillUserDetailsFunction(user);

        StepVerifier.create(userService.updatePassword(mockUserDetails, "new-password")).
                assertNext(userDetails -> {
                    Assertions.assertThat(userDetails.getUsername()).isEqualTo("user");
                    Assertions.assertThat(userDetails.getPassword()).isEqualTo("coded-new-password");
                    Assertions.assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
                }).verifyComplete();
    }

    @Test
    void getCurrentUserNameTest() {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("user");

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        try (MockedStatic<ReactiveSecurityContextHolder> mockContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            mockContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(mockSecurityContext));

            StepVerifier.create(userService.getCurrentUserName()).
                    assertNext(username -> {
                        Assertions.assertThat(username).isEqualTo("user");
                    }).verifyComplete();
        }
    }

    @Test
    void getCurrentUserIdTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");
        user.setRoles("USER");
        when(userRepository.findByUsername(any(String.class))).thenReturn(Mono.just(user));

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn("user");

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);

        try (MockedStatic<ReactiveSecurityContextHolder> mockContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            mockContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(mockSecurityContext));

            StepVerifier.create(userService.getCurrentUserId()).
                    assertNext(userId -> {
                        Assertions.assertThat(userId).isEqualTo(1L);
                    }).verifyComplete();
        }
    }

    @Test
    void isCurrentUserUserTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");
        user.setRoles("USER");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Mono.just(user));

        UserDetails mockUserDetails = fillUserDetailsFunction(user);

        UsernamePasswordAuthenticationToken mockToken = mock(UsernamePasswordAuthenticationToken.class);
        when(mockToken.getPrincipal()).thenReturn(mockUserDetails);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockToken);

        try (MockedStatic<ReactiveSecurityContextHolder> mockContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            mockContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(mockSecurityContext));

            StepVerifier.create(userService.isCurrentUserUser()).
                    assertNext(is -> {
                        Assertions.assertThat(is).isTrue();
                    }).verifyComplete();
        }
    }

    @Test
    void isCurrentUserAdminTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("password");
        user.setRoles("ADMIN");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Mono.just(user));

        UserDetails mockUserDetails = fillUserDetailsFunction(user);

        UsernamePasswordAuthenticationToken mockToken = mock(UsernamePasswordAuthenticationToken.class);
        when(mockToken.getPrincipal()).thenReturn(mockUserDetails);

        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockToken);

        try (MockedStatic<ReactiveSecurityContextHolder> mockContextHolder = Mockito.mockStatic(ReactiveSecurityContextHolder.class)) {
            mockContextHolder.when(ReactiveSecurityContextHolder::getContext).thenReturn(Mono.just(mockSecurityContext));

            StepVerifier.create(userService.isCurrentUserAdmin()).
                    assertNext(is -> {
                        Assertions.assertThat(is).isTrue();
                    }).verifyComplete();
        }
    }

    private static UserDetails fillUserDetailsFunction(User user) {
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles())
                .build();
    }
}
