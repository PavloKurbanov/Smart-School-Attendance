package org.example.smart_school_attendance.entity.school.classRoom;

import jakarta.persistence.*;
import lombok.Data;
import org.example.smart_school_attendance.entity.child.ChildDetails;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.User;

import java.util.List;
@Entity
@Data
@Table(name = "class_room")
public class ClassRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "id")
    private List<ChildDetails> children;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "id")
    private User teacher;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "id")
    private School school;

    public ClassRoom(String name, List<ChildDetails> children, User teacher, School school) {
        this.name = name;
        this.children = children;
        this.teacher = teacher;
        this.school = school;
    }

    public ClassRoom() {}
}
