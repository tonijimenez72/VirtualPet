package cat.itacademy.s05.t02.virtualpet.controller;

import cat.itacademy.s05.t02.virtualpet.dto.PetRequest;
import cat.itacademy.s05.t02.virtualpet.dto.PetResponse;
import cat.itacademy.s05.t02.virtualpet.enums.Gadget;
import cat.itacademy.s05.t02.virtualpet.enums.Location;
import cat.itacademy.s05.t02.virtualpet.model.Pet;
import cat.itacademy.s05.t02.virtualpet.service.PetService;
import cat.itacademy.s05.t02.virtualpet.service.SelectedPetService;
import cat.itacademy.s05.t02.virtualpet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pets", description = "Operations related to virtual pets")
@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final SelectedPetService selectedPetService;
    private final UserService userService;

    // 🔹 CRUD general
    @Operation(summary = "Create a new pet", description = "Creates a new virtual pet for the authenticated user.")
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> createPet(@RequestBody PetRequest petRequest) {
        Pet pet = petService.createPet(petRequest);
        return ResponseEntity.ok(PetResponse.from(pet));
    }

    @Operation(summary = "Get all pets", description = "Returns all pets in the system (admin only).")
    @GetMapping("/allpets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PetResponse>> getAllPets() {
        return ResponseEntity.ok(
                petService.getAllPets().stream()
                        .map(PetResponse::from)
                        .toList()
        );
    }

    @Operation(summary = "Get my pets", description = "Returns all pets belonging to the authenticated user.")
    @GetMapping("/mypets")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PetResponse>> getMyPets() {
        return ResponseEntity.ok(
                petService.getPetsForCurrentUser().stream()
                        .map(PetResponse::from)
                        .toList()
        );
    }

    @Operation(summary = "Get pet by ID", description = "Returns a pet by its ID.")
    @GetMapping("/{petId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> getPetById(@PathVariable String petId) {
        return ResponseEntity.ok(PetResponse.from(petService.getPetById(petId)));
    }

    @Operation(summary = "Get pets by user ID", description = "Returns all pets owned by the given user (admin only).")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PetResponse>> getPetsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(
                petService.getPetsByUserId(userId).stream()
                        .map(PetResponse::from)
                        .toList()
        );
    }

    // 🔹 Operaciones sobre la mascota seleccionada
    @Operation(summary = "Update selected pet", description = "Updates the currently selected pet for the authenticated user.")
    @PutMapping("/selected/update")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> updateSelectedPet(@RequestBody Pet updatedPet) {
        return ResponseEntity.ok(PetResponse.from(selectedPetService.updateSelectedPet(updatedPet)));
    }

    @Operation(summary = "Delete selected pet", description = "Deletes the currently selected pet for the authenticated user.")
    @DeleteMapping("/selected/delete")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteSelectedPet() {
        selectedPetService.deleteSelectedPet();
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Select and delete a pet",
            description = "Selects a pet by its ID and deletes it if owned by current user."
    )
    @DeleteMapping("/select-delete")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> selectAndDeletePet(@RequestParam String petId) {
        userService.selectAndDeletePet(petId);
        return ResponseEntity.ok("Selected pet deleted successfully.");
    }

    @Operation(summary = "Play with selected pet", description = "Increases happiness of the currently selected pet.")
    @PutMapping("/selected/play")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> play() {
        return ResponseEntity.ok(PetResponse.from(selectedPetService.play()));
    }

    @Operation(summary = "Feed selected pet", description = "Restores energy of the currently selected pet.")
    @PutMapping("/selected/feed")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> feed() {
        return ResponseEntity.ok(PetResponse.from(selectedPetService.feed()));
    }

    @Operation(summary = "Train selected pet", description = "Increases wisdom of the currently selected pet.")
    @PutMapping("/selected/train")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> train() {
        return ResponseEntity.ok(PetResponse.from(selectedPetService.train()));
    }

    @Operation(summary = "Move selected pet", description = "Moves the selected pet to a new location.")
    @PutMapping("/selected/move")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> move(@RequestParam Location location) {
        return ResponseEntity.ok(PetResponse.from(selectedPetService.moveToLocation(location)));
    }

    @Operation(summary = "Play with gadget", description = "Uses a gadget with the selected pet to increase happiness.")
    @PutMapping("/selected/gadget")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PetResponse> playWithGadget(@RequestParam Gadget gadget) {
        return ResponseEntity.ok(PetResponse.from(selectedPetService.playWithGadget(gadget)));
    }
}