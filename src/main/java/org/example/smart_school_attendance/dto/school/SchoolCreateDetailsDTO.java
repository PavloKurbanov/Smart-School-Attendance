package org.example.smart_school_attendance.dto.school;

import jakarta.validation.constraints.*;

public record SchoolCreateDetailsDTO(

        @NotBlank(message = "School name is required")
        String name,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Phone number is required")
        String phoneNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude cannot be less than -90")
        @DecimalMax(value = "90.0", message = "Latitude cannot be greater than 90")
        @Digits(
                integer = 2,
                fraction = 6,
                message = "Latitude must have no more than 6 decimal places"
        )
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude cannot be less than -180")
        @DecimalMax(value = "180.0", message = "Longitude cannot be greater than 180")
        @Digits(
                integer = 3,
                fraction = 6,
                message = "Longitude must have no more than 6 decimal places"
        )
        Double longitude,

        @NotNull(message = "Radius is required")
        @Min(value = 1, message = "Radius must be greater than 0 meters")
        Integer radius
) {
}
