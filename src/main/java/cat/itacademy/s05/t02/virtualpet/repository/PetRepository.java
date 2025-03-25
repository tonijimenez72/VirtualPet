package cat.itacademy.s05.t02.virtualpet.repository;

import cat.itacademy.s05.t02.virtualpet.model.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PetRepository extends MongoRepository<Pet, String> {
    List<Pet> findByUserId(String userId);
}