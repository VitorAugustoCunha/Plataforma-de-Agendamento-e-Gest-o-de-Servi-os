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

import com.agenda.plataform.entity.ProviderProfileEntity;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.UserRole;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.ProviderProfileRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderProfileService - Testes Unitários")
class ProviderProfileServiceTest {

    @Mock
    private ProviderProfileRepository providerProfileRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProviderProfileService providerProfileService;

    private ProviderProfileEntity providerProfile;
    private UserEntity providerUser;
    private UUID userId;
    private UUID profileId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        providerUser = UserEntity.builder()
                .id(userId)
                .name("Maria Barbeira")
                .email("maria@test.com")
                .role(UserRole.PROVIDER)
                .active(true)
                .build();

        providerProfile = ProviderProfileEntity.builder()
                .id(profileId)
                .user(providerUser)
                .bio("Barbeira profissional")
                .locationText("Centro")
                .minAdvanceMinutes(30)
                .cancelWindowMinutes(120)
                .slotStepMinutes(30)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar perfil de provider com sucesso")
    void testCreateProviderProfileSuccess() {
        when(userService.findById(userId))
                .thenReturn(providerUser);
        when(providerProfileRepository.findByUserId(userId))
                .thenReturn(Optional.empty());
        when(providerProfileRepository.save(any(ProviderProfileEntity.class)))
                .thenReturn(providerProfile);

        ProviderProfileEntity created = providerProfileService.create(userId, providerProfile);

        assertNotNull(created);
        assertEquals("Barbeira profissional", created.getBio());
        assertEquals("Centro", created.getLocationText());
        verify(userService, times(1)).findById(userId);
        verify(providerProfileRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação quando usuário não é PROVIDER")
    void testCreateProfileForNonProvider() {
        UserEntity clientUser = UserEntity.builder()
                .id(userId)
                .role(UserRole.CLIENT)
                .build();

        when(userService.findById(userId))
                .thenReturn(clientUser);

        Exception exception = assertThrows(InvalidBusinessRuleException.class, () ->
                providerProfileService.create(userId, providerProfile)
        );

        assertEquals("Usuário não é um PROVIDER", exception.getMessage());
        verify(providerProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar criação quando provider já possui perfil")
    void testCreateDuplicateProfile() {
        when(userService.findById(userId))
                .thenReturn(providerUser);
        when(providerProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(providerProfile));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                providerProfileService.create(userId, providerProfile)
        );

        assertEquals("Provider já possui perfil", exception.getMessage());
        verify(providerProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar perfil por ID")
    void testFindProfileById() {
        when(providerProfileRepository.findById(profileId))
                .thenReturn(Optional.of(providerProfile));

        ProviderProfileEntity found = providerProfileService.findById(profileId);

        assertNotNull(found);
        assertEquals(profileId, found.getId());
        verify(providerProfileRepository, times(1)).findById(profileId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando perfil não encontrado")
    void testFindProfileByIdNotFound() {
        when(providerProfileRepository.findById(profileId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                providerProfileService.findById(profileId)
        );

        assertTrue(exception.getMessage().contains("ProviderProfile não encontrado"));
    }

    @Test
    @DisplayName("Deve encontrar perfil por User ID")
    void testFindProfileByUserId() {
        when(providerProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(providerProfile));

        ProviderProfileEntity found = providerProfileService.findByUserId(userId);

        assertNotNull(found);
        assertEquals(userId, found.getUser().getId());
        verify(providerProfileRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve atualizar perfil de provider")
    void testUpdateProviderProfile() {
        ProviderProfileEntity updateData = ProviderProfileEntity.builder()
                .bio("Nova bio atualizada")
                .locationText("Zona Sul")
                .minAdvanceMinutes(60)
                .build();

        when(providerProfileRepository.findById(profileId))
                .thenReturn(Optional.of(providerProfile));
        when(providerProfileRepository.save(any(ProviderProfileEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProviderProfileEntity updated = providerProfileService.update(profileId, updateData);

        assertEquals("Nova bio atualizada", updated.getBio());
        assertEquals("Zona Sul", updated.getLocationText());
        assertEquals(60, updated.getMinAdvanceMinutes());
        verify(providerProfileRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar perfil de provider")
    void testDeleteProviderProfile() {
        when(providerProfileRepository.findById(profileId))
                .thenReturn(Optional.of(providerProfile));
        doNothing().when(providerProfileRepository).delete(providerProfile);

        providerProfileService.delete(profileId);

        verify(providerProfileRepository, times(1)).delete(providerProfile);
    }
}
