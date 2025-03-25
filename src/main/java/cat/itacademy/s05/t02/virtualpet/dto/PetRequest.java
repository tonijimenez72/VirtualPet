package cat.itacademy.s05.t02.virtualpet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PetRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String variety;
    @NotBlank
    private String color;
}