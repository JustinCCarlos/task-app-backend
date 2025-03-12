package com.example.Task.Management.System.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Builder
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Setter
    @Column(nullable = false, unique = true)
    @Size(min = 3, max =50, message =  "Name must be between 3 and 50 characters")
    private String name;
}
