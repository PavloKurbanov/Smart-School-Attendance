package org.example.smart_school_attendance.entity.school.classRoom;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Data
@Table(name = "class_room")
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classRoom")
    private List<ChildDetails> children = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @Column(name = "capacity")
    @NotNull
    private Integer capacity;

    @Column(name = "create_date", nullable = false, updatable = false)
    @NotNull
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = false)
    @NotNull
    private LocalDateTime updateDate;


    public ClassRoom(String name, List<ChildDetails> children, User teacher, School school, Integer capacity) {
        this.name = name;
        this.children = children;
        this.teacher = teacher;
        this.school = school;
        this.capacity = capacity;
    }

    public ClassRoom() {}

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
