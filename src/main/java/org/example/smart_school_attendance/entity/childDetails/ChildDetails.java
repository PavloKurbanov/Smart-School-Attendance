package org.example.smart_school_attendance.entity.childDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.smart_school_attendance.entity.school.School;
import org.example.smart_school_attendance.entity.school.classRoom.ClassRoom;
import org.example.smart_school_attendance.entity.user.User;

import java.time.LocalDateTime;
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
    private ChildLocationStatus status = ChildLocationStatus.INSIDE_SCHOOL;

    @ManyToMany
    @JoinTable(
            name = "child_parents",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id")
    )
    private List<User> parents;

    @Column(name = "create_date", nullable = false, updatable = false)
    @NotNull
    private LocalDateTime createDate;

    @Column(name = "update_date", nullable = false)
    @NotNull
    private LocalDateTime updateDate;

    public ChildDetails(User child, School school, ClassRoom classRoom, List<User> parents) {
        this.child = child;
        this.school = school;
        this.classRoom = classRoom;
        this.parents = parents;
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    public ChildDetails() {
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
