package cat.itacademy.s05.t02.virtualpet.service.impl;

import cat.itacademy.s05.t02.virtualpet.exception.custom.*;
import cat.itacademy.s05.t02.virtualpet.model.Pet;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.repository.PetRepository;
import cat.itacademy.s05.t02.virtualpet.repository.UserRepository;
import cat.itacademy.s05.t02.virtualpet.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void selectPet(String petId) {
        User user = getCurrentUser();
        Pet pet = petRepository.findById(petId)
                .orElseThrow(PetNotFoundException::new);

        if (!pet.getUserId().equals(user.getId())) {
            throw new UnauthorizedPetAccessException();
        }

        user.setSelectedPetId(petId);
        userRepository.save(user);
    }

    @Override
    public void clearSelectedPet() {
        User user = getCurrentUser();
        user.setSelectedPetId(null);
        userRepository.save(user);
    }

    @Override
    public void deleteSelectedPet() {
        User user = getCurrentUser();
        String selectedPetId = user.getSelectedPetId();

        if (selectedPetId == null) {
            throw new NoSelectedPetException();
        }

        Pet pet = petRepository.findById(selectedPetId)
                .orElseThrow(PetNotFoundException::new);

        if (!pet.getUserId().equals(user.getId())) {
            throw new UnauthorizedPetAccessException();
        }

        petRepository.delete(pet);
        clearSelectedPet();
    }

    @Override
    public void selectAndDeletePet(String petId) {
        selectPet(petId);
        deleteSelectedPet();
    }
}