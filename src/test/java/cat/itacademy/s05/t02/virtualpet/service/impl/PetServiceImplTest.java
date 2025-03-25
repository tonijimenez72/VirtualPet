package cat.itacademy.s05.t02.virtualpet.service.impl;

import cat.itacademy.s05.t02.virtualpet.dto.PetRequest;
import cat.itacademy.s05.t02.virtualpet.enums.Gadget;
import cat.itacademy.s05.t02.virtualpet.enums.Location;
import cat.itacademy.s05.t02.virtualpet.exception.custom.PetNotFoundException;
import cat.itacademy.s05.t02.virtualpet.model.Pet;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.repository.PetRepository;
import cat.itacademy.s05.t02.virtualpet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceImplTest {

    @Mock private PetRepository petRepository;
    @Mock private UserService userService;
    @InjectMocks private PetServiceImpl petService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id("user123")
                .email("test@example.com")
                .build();
    }

    @Test
    void createPet_ShouldReturnSavedPet() {
        PetRequest request = new PetRequest();
        request.setName("Kira");
        request.setVariety("Dog");
        request.setColor("White");

        when(userService.getCurrentUser()).thenReturn(user);

        Pet petToSave = Pet.builder()
                .name("Kira")
                .variety("Dog")
                .color("White")
                .userId("user123")
                .happiness(5)
                .energy(5)
                .wisdom(0)
                .gadget(Gadget.NONE)
                .location(Location.HOME)
                .build();

        petToSave.setId("pet123");

        when(petRepository.save(any(Pet.class))).thenReturn(petToSave);

        Pet result = petService.createPet(request);

        assertNotNull(result);
        assertEquals("pet123", result.getId());
        assertEquals("Kira", result.getName());
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void getAllPets_ShouldReturnAllPets() {
        when(petRepository.findAll()).thenReturn(List.of(new Pet(), new Pet()));
        List<Pet> pets = petService.getAllPets();
        assertEquals(2, pets.size());
    }

    @Test
    void getPetsForCurrentUser_ShouldReturnUserPets() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(petRepository.findByUserId("user123")).thenReturn(List.of(new Pet(), new Pet()));

        List<Pet> pets = petService.getPetsForCurrentUser();
        assertEquals(2, pets.size());
    }

    @Test
    void getPetsByUserId_ShouldReturnUserPets() {
        when(petRepository.findByUserId("user123")).thenReturn(List.of(new Pet()));
        List<Pet> pets = petService.getPetsByUserId("user123");
        assertEquals(1, pets.size());
    }

    @Test
    void getPetById_ShouldReturnPet_WhenFound() {
        Pet pet = new Pet();
        pet.setId("pet123");

        when(petRepository.findById("pet123")).thenReturn(Optional.of(pet));
        Pet result = petService.getPetById("pet123");

        assertNotNull(result);
        assertEquals("pet123", result.getId());
    }

    @Test
    void getPetById_ShouldThrow_WhenNotFound() {
        when(petRepository.findById("pet123")).thenReturn(Optional.empty());
        assertThrows(PetNotFoundException.class, () -> petService.getPetById("pet123"));
    }
}