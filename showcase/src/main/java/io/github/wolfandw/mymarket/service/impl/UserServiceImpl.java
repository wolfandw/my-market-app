package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.dto.UserInfoDto;
import io.github.wolfandw.mymarket.model.User;
import io.github.wolfandw.mymarket.repository.UserRepository;
import io.github.wolfandw.mymarket.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Реализация {@link UserService}.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return  userRepository.findByUsername(username).map(fillUserDetailsFunction());
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return  userRepository.findByUsername(user.getUsername()).flatMap(existingUser -> {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(existingUser);
        }).map(fillUserDetailsFunction());
    }

    @Override
    public Mono<Long> getCurrentUserId() {
        return getCurrentUserName().flatMap(userRepository::findByUsername).map(User::getId).defaultIfEmpty(-1L);
    }

    @Override
    public Mono<String> getCurrentUserName() {
        return getAuthenticationMono().flatMap(UserServiceImpl::getUsername);
    }

    @Override
    public Mono<Boolean> isCurrentUserUser() {
        return getAuthenticationMono().flatMap(UserServiceImpl::isUserUser);
    }

    @Override
    public Mono<Boolean> isCurrentUserAdmin() {
        return getAuthenticationMono().flatMap(UserServiceImpl::isUserAdmin);
    }

    @Override
    public Mono<UserInfoDto> getCurrentUserInfo() {
        return Mono.zip(getCurrentUserId(), getCurrentUserName(), isCurrentUserUser(), isCurrentUserAdmin()).
                map(tuple -> new UserInfoDto(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4()));
    }

    private static Mono<Authentication> getAuthenticationMono() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext ->
                        securityContext.getAuthentication() == null ?
                                Mono.empty() : Mono.just(securityContext.getAuthentication()));
    }

    private static Function<User, UserDetails> fillUserDetailsFunction() {
        return user ->
                org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRoles().split(","))
                        .build();
    }

    private static Mono<Boolean> isUserUser(Authentication auth) {
        return  hasRole(auth, ROLE_USER);
    }

    private static Mono<Boolean> isUserAdmin(Authentication auth) {
        return  hasRole(auth, ROLE_ADMIN);
    }

    private static Mono<Boolean> hasRole(Authentication auth, String role) {
        return getUserDetails(auth).map(userDetails -> userDetails.getAuthorities().stream().
                anyMatch(grantedAuthority -> role.equals(grantedAuthority.getAuthority()))).defaultIfEmpty(false);
    }

    private static Mono<String> getUsername(Authentication auth) {
        return Mono.just(auth.getName());
    }

    private static Mono<org.springframework.security.core.userdetails.User> getUserDetails(Authentication auth) {
        if (auth instanceof UsernamePasswordAuthenticationToken token)
        {
            Object principal = token.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
                return Mono.just(userDetails);
            }
        }
        return Mono.empty();
    }
}
