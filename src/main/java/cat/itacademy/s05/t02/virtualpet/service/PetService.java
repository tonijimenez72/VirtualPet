package cat.itacademy.s05.t02.virtualpet.service;

import cat.itacademy.s05.t02.virtualpet.dto.PetRequest;
import cat.itacademy.s05.t02.virtualpet.exception.custom.PetNotFoundException;
import cat.itacademy.s05.t02.virtualpet.model.Pet;

import java.util.List;

public interface PetService {

    Pet createPet(PetRequest petRequest);
    List<Pet> getAllPets();
    List<Pet> getPetsForCurrentUser();
    List<Pet> getPetsByUserId(String userId);
    Pet getPetById(String petId) throws PetNotFoundException;
}