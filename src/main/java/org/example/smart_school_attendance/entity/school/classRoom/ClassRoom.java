package org.example.smart_school_attendance.entity.school.classRoom;

import jakarta.persistence.*;
import lombok.Data;
import org.example.smart_school_attendance.entity.childDetails.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.User;

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

    private Integer capacity;

    public ClassRoom(String name, List<ChildDetails> children, User teacher, School school, Integer capacity) {
        this.name = name;
        this.children = children;
        this.teacher = teacher;
        this.school = school;
        this.capacity = capacity;
    }

    public ClassRoom() {}
}
