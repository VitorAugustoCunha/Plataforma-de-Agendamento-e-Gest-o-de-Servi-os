package com.agenda.plataform.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.event.EventPublisher;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Testes Unitários")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private UserEntity user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = UserEntity.builder()
                .id(userId)
                .name("João Silva")
                .email("joao@test.com")
                .passwordHash("encodedPassword")
                .role(UserRole.CLIENT)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCreateUserSuccess() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);
        doNothing().when(eventPublisher).publishUserCreated(any());

        UserEntity created = userService.create(user);

        assertNotNull(created);
        assertEquals("João Silva", created.getName());
        assertEquals("joao@test.com", created.getEmail());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishUserCreated(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação com email duplicado")
    void testCreateUserWithDuplicateEmail() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.create(user)
        );

        assertEquals("Email já existe", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar usuário por ID")
    void testFindUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        UserEntity found = userService.findById(userId);

        assertNotNull(found);
        assertEquals(userId, found.getId());
        assertEquals("João Silva", found.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por ID")
    void testFindUserByIdNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.findById(userId)
        );

        assertTrue(exception.getMessage().contains("User não encontrado"));
    }

    @Test
    @DisplayName("Deve encontrar usuário por email")
    void testFindUserByEmail() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        UserEntity found = userService.findByEmail(user.getEmail());

        assertNotNull(found);
        assertEquals("joao@test.com", found.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Deve atualizar usuário")
    void testUpdateUser() {
        UserEntity updateData = UserEntity.builder()
                .name("João Silva Atualizado")
                .active(false)
                .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity updated = userService.update(userId, updateData);

        assertEquals("João Silva Atualizado", updated.getName());
        assertFalse(updated.getActive());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar usuário por ID")
    void testDeleteUserById() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteById(userId);

        verify(userRepository, times(1)).delete(user);
    }
}
