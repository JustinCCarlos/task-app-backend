package com.example.Task.Management.System.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private Long categoryId;

    @Setter
    @Column(nullable = false, unique = true)
    @Size(min = 3, max =50, message =  "Name must be between 3 and 50 characters")
    private String name;

}
