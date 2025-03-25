package cat.itacademy.s05.t02.virtualpet.service;

import cat.itacademy.s05.t02.virtualpet.enums.Gadget;
import cat.itacademy.s05.t02.virtualpet.enums.Location;
import cat.itacademy.s05.t02.virtualpet.exception.custom.*;
import cat.itacademy.s05.t02.virtualpet.model.Pet;

public interface SelectedPetService {
    Pet getSelectedPet() throws NoSelectedPetException, PetNotFoundException;
    Pet updateSelectedPet(Pet updatedPet) throws NoSelectedPetException, PetNotFoundException;
    void deleteSelectedPet() throws NoSelectedPetException, PetNotFoundException;
    Pet play() throws NoSelectedPetException, PetNotFoundException, NoEnergyException;
    Pet feed() throws NoSelectedPetException, PetNotFoundException;
    Pet train() throws NoSelectedPetException, PetNotFoundException, NoEnergyException;
    Pet playWithGadget(Gadget gadget) throws NoSelectedPetException, PetNotFoundException, NoEnergyException, InvalidGadgetException;
    Pet moveToLocation(Location location) throws NoSelectedPetException, PetNotFoundException, NoEnergyException;
}