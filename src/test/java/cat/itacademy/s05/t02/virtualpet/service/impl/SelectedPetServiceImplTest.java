package cat.itacademy.s05.t02.virtualpet.service.impl;

import cat.itacademy.s05.t02.virtualpet.enums.Gadget;
import cat.itacademy.s05.t02.virtualpet.enums.Location;
import cat.itacademy.s05.t02.virtualpet.exception.custom.NoEnergyException;
import cat.itacademy.s05.t02.virtualpet.exception.custom.NoSelectedPetException;
import cat.itacademy.s05.t02.virtualpet.exception.custom.PetNotFoundException;
import cat.itacademy.s05.t02.virtualpet.model.Pet;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.repository.PetRepository;
import cat.itacademy.s05.t02.virtualpet.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SelectedPetServiceImplTest {

    @Mock private PetRepository petRepository;
    @Mock private UserService userService;

    @InjectMocks private SelectedPetServiceImpl selectedPetService;

    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder().id("user123").selectedPetId("pet123").build();
        pet = Pet.builder()
                .id("pet123")
                .userId("user123")
                .energy(5)
                .happiness(3)
                .wisdom(1)
                .location(Location.HOME)
                .gadget(Gadget.NONE)
                .build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(petRepository.findById("pet123")).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void play_ShouldIncreaseHappinessAndDecreaseEnergy() {
        Pet updated = selectedPetService.play();
        assertEquals(4, updated.getHappiness());
        assertEquals(4, updated.getEnergy());
    }

    @Test
    void feed_ShouldIncreaseEnergy() {
        Pet result = selectedPetService.feed();
        assertEquals(6, result.getEnergy());
    }

    @Test
    void train_ShouldIncreaseWisdomAndDecreaseEnergy() {
        Pet result = selectedPetService.train();
        assertEquals(2, result.getWisdom());
        assertEquals(3, result.getEnergy());
    }

    @Test
    void playWithGadget_ShouldUpdateGadgetAndStats() {
        Pet result = selectedPetService.playWithGadget(Gadget.TOY);
        assertEquals(Gadget.TOY, result.getGadget());
        assertEquals(5, result.getHappiness());
        assertEquals(4, result.getEnergy());
    }

    @Test
    void moveToLocation_ShouldChangeLocationAndResetGadget() {
        Pet result = selectedPetService.moveToLocation(Location.BEACH);
        assertEquals(Location.BEACH, result.getLocation());
        assertEquals(Gadget.NONE, result.getGadget());
        assertEquals(4, result.getEnergy());
        assertEquals(4, result.getHappiness());
    }

    @Test
    void play_ShouldThrowNoEnergyException_WhenEnergyTooLow() {
        pet.setEnergy(1);
        assertThrows(NoEnergyException.class, () -> selectedPetService.play());
    }

    @Test
    void train_ShouldThrowNoEnergyException_WhenEnergyTooLow() {
        pet.setEnergy(1);
        assertThrows(NoEnergyException.class, () -> selectedPetService.train());
    }

    @Test
    void moveToLocation_ShouldThrowNoEnergyException_WhenEnergyTooLow() {
        pet.setEnergy(1);
        assertThrows(NoEnergyException.class, () -> selectedPetService.moveToLocation(Location.PARK));
    }

    @Test
    void getSelectedPet_ShouldThrow_WhenNoSelectedPet() {
        user.setSelectedPetId(null);
        assertThrows(NoSelectedPetException.class, () -> selectedPetService.getSelectedPet());
    }

    @Test
    void getSelectedPet_ShouldThrow_WhenPetNotFound() {
        when(petRepository.findById("pet123")).thenReturn(Optional.empty());
        assertThrows(PetNotFoundException.class, () -> selectedPetService.getSelectedPet());
    }
}
