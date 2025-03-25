package cat.itacademy.s05.t02.virtualpet.service.impl;

import cat.itacademy.s05.t02.virtualpet.dto.PetRequest;
import cat.itacademy.s05.t02.virtualpet.enums.Gadget;
import cat.itacademy.s05.t02.virtualpet.enums.Location;
import cat.itacademy.s05.t02.virtualpet.exception.custom.PetNotFoundException;
import cat.itacademy.s05.t02.virtualpet.model.Pet;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.repository.PetRepository;
import cat.itacademy.s05.t02.virtualpet.service.PetService;
import cat.itacademy.s05.t02.virtualpet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {
    private static final int DEFAULT_HAPPINESS = 5;
    private static final int DEFAULT_ENERGY = 5;
    private static final int DEFAULT_WISDOM = 0;
    private final PetRepository petRepository;
    private final UserService userService;

    @Override
    public Pet createPet(PetRequest petRequest) {
        User user = userService.getCurrentUser();

        Pet pet = Pet.builder()
                .name(petRequest.getName())
                .variety(petRequest.getVariety())
                .color(petRequest.getColor())
                .userId(user.getId())
                .happiness(DEFAULT_HAPPINESS)
                .energy(DEFAULT_ENERGY)
                .wisdom(DEFAULT_WISDOM)
                .gadget(Gadget.NONE)
                .location(Location.HOME)
                .build();

        return petRepository.save(pet);
    }

    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public List<Pet> getPetsForCurrentUser() {
        return petRepository.findByUserId(userService.getCurrentUser().getId());
    }

    @Override
    public List<Pet> getPetsByUserId(String userId) {
        return petRepository.findByUserId(userId);
    }

    @Override
    public Pet getPetById(String petId) {
        return petRepository.findById(petId)
                .orElseThrow(PetNotFoundException::new);
    }
}