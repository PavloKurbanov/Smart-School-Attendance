package org.example.smart_school_attendance.entity.child;

import jakarta.persistence.*;
import lombok.Data;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "child_details")
public class ChildDetails {

    @Id
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private User child;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne
    @JoinColumn(name = "class_room_id")
    private ClassRoom classRoom;

    @Enumerated(EnumType.STRING)
    private ChildLocationStatus status;

    @ManyToMany
    @JoinTable(
            name = "child_parents",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    private List<User> parents = new ArrayList<>();

    public ChildDetails(Long id, User child, School school, ClassRoom classRoom, List<User> parents) {
        this.id = id;
        this.child = child;
        this.school = school;
        this.classRoom = classRoom;
        this.parents = parents;
    }

    public ChildDetails() {}
}
