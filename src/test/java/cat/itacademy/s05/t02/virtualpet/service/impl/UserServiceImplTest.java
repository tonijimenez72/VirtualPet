package cat.itacademy.s05.t02.virtualpet.service.impl;

import cat.itacademy.s05.t02.virtualpet.exception.custom.*;
import cat.itacademy.s05.t02.virtualpet.model.Pet;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.repository.PetRepository;
import cat.itacademy.s05.t02.virtualpet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PetRepository petRepository;

    @InjectMocks private UserServiceImpl userService;

    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);

        // User and pet
        user = User.builder()
                .id("user123")
                .email("test@example.com")
                .build();

        pet = Pet.builder()
                .id("pet123")
                .userId("user123")
                .build();
    }

    @Test
    void getCurrentUser_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        User result = userService.getCurrentUser();
        assertEquals("user123", result.getId());
    }

    @Test
    void getCurrentUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
    }

    @Test
    void selectPet_ShouldAssignPetToUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(pet));

        userService.selectPet("pet123");

        assertEquals("pet123", user.getSelectedPetId());
        verify(userRepository).save(user);
    }

    @Test
    void selectPet_ShouldThrow_WhenPetNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById("pet123")).thenReturn(Optional.empty());

        assertThrows(PetNotFoundException.class, () -> userService.selectPet("pet123"));
    }

    @Test
    void selectPet_ShouldThrow_WhenPetNotOwned() {
        Pet otherPet = Pet.builder().id("pet456").userId("otherUser").build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById("pet456")).thenReturn(Optional.of(otherPet));

        assertThrows(UnauthorizedPetAccessException.class, () -> userService.selectPet("pet456"));
    }

    @Test
    void clearSelectedPet_ShouldClearSelection() {
        user.setSelectedPetId("pet123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        userService.clearSelectedPet();

        assertNull(user.getSelectedPetId());
        verify(userRepository).save(user);
    }

    @Test
    void deleteSelectedPet_ShouldDeletePetAndClearSelection() {
        user.setSelectedPetId("pet123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(pet));

        userService.deleteSelectedPet();

        verify(petRepository).delete(pet);
        verify(userRepository).save(user);
        assertNull(user.getSelectedPetId());
    }

    @Test
    void deleteSelectedPet_ShouldThrow_WhenNoSelection() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        assertThrows(NoSelectedPetException.class, () -> userService.deleteSelectedPet());
    }

    @Test
    void deleteSelectedPet_ShouldThrow_WhenPetNotFound() {
        user.setSelectedPetId("pet123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById("pet123")).thenReturn(Optional.empty());

        assertThrows(PetNotFoundException.class, () -> userService.deleteSelectedPet());
    }

    @Test
    void deleteSelectedPet_ShouldThrow_WhenNotOwner() {
        Pet otherPet = Pet.builder().id("pet123").userId("anotherUser").build();
        user.setSelectedPetId("pet123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(otherPet));

        assertThrows(UnauthorizedPetAccessException.class, () -> userService.deleteSelectedPet());
    }

    @Test
    void selectAndDeletePet_ShouldWorkCorrectly() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(petRepository.findById("pet123")).thenReturn(Optional.of(pet));

        userService.selectAndDeletePet("pet123");

        verify(userRepository, times(2)).save(user); // One for selection, one for clear
        verify(petRepository).delete(pet);
    }
}