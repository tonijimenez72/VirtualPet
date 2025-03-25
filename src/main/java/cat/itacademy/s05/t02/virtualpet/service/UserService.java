package cat.itacademy.s05.t02.virtualpet.service;

import cat.itacademy.s05.t02.virtualpet.exception.custom.*;
import cat.itacademy.s05.t02.virtualpet.model.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User getCurrentUser() throws UserNotFoundException;
    void selectPet(String petId) throws PetNotFoundException, UnauthorizedPetAccessException;
    void clearSelectedPet() throws UserNotFoundException;
    void deleteSelectedPet() throws PetNotFoundException, NoSelectedPetException, UnauthorizedPetAccessException;
    void selectAndDeletePet(String petId) throws PetNotFoundException, NoSelectedPetException, UnauthorizedPetAccessException;
}