package cat.itacademy.s05.t02.virtualpet.controller;

import cat.itacademy.s05.t02.virtualpet.dto.UserResponse;
import cat.itacademy.s05.t02.virtualpet.model.User;
import cat.itacademy.s05.t02.virtualpet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "Operations related to user profile and pet selection")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get current user", description = "Returns the authenticated user's profile including selected pet.")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(UserResponse.from(userService.getCurrentUser()));
    }

    @Operation(summary = "Get all users", description = "Returns all users (admin only).")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(UserResponse::from).toList());
    }

    @Operation(summary = "Select a pet", description = "Sets the selected pet for the current user.")
    @PutMapping("/selectpet")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> selectPet(@RequestParam String petId) {
        userService.selectPet(petId);
        return ResponseEntity.ok("Pet selected successfully.");
    }

    @Operation(summary = "Clear selected pet", description = "Removes the currently selected pet for the authenticated user.")
    @PutMapping("/clear-selected")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> clearSelectedPet() {
        userService.clearSelectedPet();
        return ResponseEntity.ok("Selected pet cleared.");
    }

    @Operation(summary = "Delete selected pet", description = "Deletes the currently selected pet for the authenticated user.")
    @DeleteMapping("/delete-selected")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deleteSelectedPet() {
        userService.deleteSelectedPet();
        return ResponseEntity.ok("Selected pet deleted.");
    }
}