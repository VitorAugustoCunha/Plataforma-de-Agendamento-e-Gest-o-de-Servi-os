package com.agenda.plataform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService - Testes Unitários")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(userId)
                .name("João Silva")
                .email("joao@test.com")
                .passwordHash("$2a$10$encoded.password.hash")
                .role(UserRole.CLIENT)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Deve carregar usuário por email com sucesso")
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        assertNotNull(userDetails);
        assertEquals("joao@test.com", userDetails.getUsername());
        assertEquals("$2a$10$encoded.password.hash", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por email")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("naoexiste@test.com"))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("naoexiste@test.com")
        );

        assertTrue(exception.getMessage().contains("User não encontrado com email"));
    }

    @Test
    @DisplayName("Deve carregar usuário por ID com sucesso")
    void testLoadUserByIdSuccess() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        assertNotNull(userDetails);
        assertEquals("joao@test.com", userDetails.getUsername());
        assertEquals("$2a$10$encoded.password.hash", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por ID")
    void testLoadUserByIdNotFound() {
        UUID fakeId = UUID.randomUUID();
        when(userRepository.findById(fakeId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserById(fakeId)
        );

        assertTrue(exception.getMessage().contains("User não encontrado com ID"));
    }

    @Test
    @DisplayName("Deve carregar usuário PROVIDER com role correto")
    void testLoadProviderWithCorrectRole() {
        UserEntity provider = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("provider@test.com")
                .passwordHash("$2a$10$encoded")
                .role(UserRole.PROVIDER)
                .build();

        when(userRepository.findByEmail(provider.getEmail()))
                .thenReturn(Optional.of(provider));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(provider.getEmail());

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PROVIDER")));
    }

    @Test
    @DisplayName("Deve carregar usuário ADMIN com role correto")
    void testLoadAdminWithCorrectRole() {
        UserEntity admin = UserEntity.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .passwordHash("$2a$10$encoded")
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findByEmail(admin.getEmail()))
                .thenReturn(Optional.of(admin));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(admin.getEmail());

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
}
