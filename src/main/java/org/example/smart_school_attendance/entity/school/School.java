package org.example.smart_school_attendance.entity.school;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "school")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public School(String name, String address, String phoneNumber, String email, Double latitude, Double longitude, Integer radius) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    public School() {
    }

    @PrePersist
    public void prePersist() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
