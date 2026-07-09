package org.example.smart_school_attendance.entity.child;

import jakarta.persistence.*;
import lombok.Data;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.User;

import java.util.List;

@Data
@Entity
@Table(name = "child_details")
public class ChildDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "id")
    private User child;

    @OneToMany(cascade = CascadeType.ALL)
    private School school;

    @ManyToOne(cascade = CascadeType.ALL)
    private ClassRoom classRoom;

    private ChildLocationStatus status;

    @ManyToMany(mappedBy = "id")
    private List<User> parents;

    public ChildDetails(Long id, User child, School school, ClassRoom classRoom, List<User> parents) {
        this.id = id;
        this.child = child;
        this.school = school;
        this.classRoom = classRoom;
        this.parents = parents;
    }

    public ChildDetails() {}
}
