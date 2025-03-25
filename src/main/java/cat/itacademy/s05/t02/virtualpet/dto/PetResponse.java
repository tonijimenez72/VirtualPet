package cat.itacademy.s05.t02.virtualpet.dto;

import cat.itacademy.s05.t02.virtualpet.enums.Gadget;
import cat.itacademy.s05.t02.virtualpet.enums.Location;
import cat.itacademy.s05.t02.virtualpet.model.Pet;

public record PetResponse(
        String id,
        String name,
        String color,
        int happiness,
        int energy,
        int wisdom,
        Gadget gadget,
        Location location
) {
    public static PetResponse from(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getName(),
                pet.getColor(),
                pet.getHappiness(),
                pet.getEnergy(),
                pet.getWisdom(),
                pet.getGadget(),
                pet.getLocation()
        );
    }
}