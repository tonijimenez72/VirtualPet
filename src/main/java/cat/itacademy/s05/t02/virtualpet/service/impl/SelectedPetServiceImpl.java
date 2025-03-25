package cat.itacademy.s05.t02.virtualpet.service.impl;

import cat.itacademy.s05.t02.virtualpet.enums.Gadget;
import cat.itacademy.s05.t02.virtualpet.enums.Location;
import cat.itacademy.s05.t02.virtualpet.exception.custom.*;
import cat.itacademy.s05.t02.virtualpet.model.Pet;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.repository.PetRepository;
import cat.itacademy.s05.t02.virtualpet.service.SelectedPetService;
import cat.itacademy.s05.t02.virtualpet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SelectedPetServiceImpl implements SelectedPetService {

    private static final int DEFAULT_VARIATION = 1;
    private static final int MAX_VARIATION = 2;
    private static final int MIN_ENERGY = 2;

    private final PetRepository petRepository;
    private final UserService userService;

    private static final Map<Location, List<Gadget>> allowedGadgets = Map.of(
            Location.HOME, List.of(Gadget.BONE, Gadget.TOY),
            Location.PARK, List.of(Gadget.BONE, Gadget.BALL),
            Location.COUNTRY, List.of(Gadget.STICK),
            Location.BEACH, List.of(Gadget.BALL),
            Location.CAR, List.of(Gadget.NONE)
    );

    @Override
    public Pet getSelectedPet() {
        return findSelectedPet(userService.getCurrentUser());
    }

    @Override
    public Pet updateSelectedPet(Pet updatedPet) {
        Pet existing = getSelectedPet();
        updatedPet.setId(existing.getId());
        updatedPet.setUserId(existing.getUserId());
        return petRepository.save(updatedPet);
    }

    @Override
    public void deleteSelectedPet() {
        Pet pet = getSelectedPet();
        petRepository.deleteById(pet.getId());

        User user = userService.getCurrentUser();
        user.setSelectedPetId(null);
    }

    @Override
    public Pet play() {
        Pet pet = getSelectedPet();
        if (pet.getEnergy() < MIN_ENERGY)
            throw new NoEnergyException("to play");
        pet.setHappiness(pet.getHappiness() + DEFAULT_VARIATION);
        pet.setEnergy(pet.getEnergy() - DEFAULT_VARIATION);
        return petRepository.save(pet);
    }

    @Override
    public Pet feed() {
        Pet pet = getSelectedPet();
        pet.setEnergy(pet.getEnergy() + DEFAULT_VARIATION);
        return petRepository.save(pet);
    }

    @Override
    public Pet train() {
        Pet pet = getSelectedPet();
        if (pet.getEnergy() < MIN_ENERGY)
            throw new NoEnergyException("to train");
        pet.setWisdom(pet.getWisdom() + DEFAULT_VARIATION);
        pet.setEnergy(pet.getEnergy() - MAX_VARIATION);
        return petRepository.save(pet);
    }

    @Override
    public Pet playWithGadget(Gadget gadget) {
        Pet pet = getSelectedPet();

        if (pet.getEnergy() < MIN_ENERGY)
            throw new NoEnergyException("to play");

        List<Gadget> allowed = allowedGadgets.getOrDefault(pet.getLocation(), List.of());

        if (!allowed.contains(gadget))
            throw new InvalidGadgetException();

        pet.setGadget(gadget);
        pet.setHappiness(pet.getHappiness() + MAX_VARIATION);
        pet.setEnergy(pet.getEnergy() - DEFAULT_VARIATION);

        return petRepository.save(pet);
    }

    @Override
    public Pet moveToLocation(Location location) {
        Pet pet = getSelectedPet();
        if (pet.getEnergy() < MIN_ENERGY)
            throw new NoEnergyException("to move");

        pet.setLocation(location);
        pet.setGadget(Gadget.NONE);
        pet.setHappiness(pet.getHappiness() + DEFAULT_VARIATION);
        pet.setEnergy(pet.getEnergy() - DEFAULT_VARIATION);
        return petRepository.save(pet);
    }

    private Pet findSelectedPet(User user) {
        String selectedPetId = user.getSelectedPetId();
        if (selectedPetId == null)
            throw new NoSelectedPetException();

        return petRepository.findById(selectedPetId)
                .orElseThrow(PetNotFoundException::new);
    }
}
