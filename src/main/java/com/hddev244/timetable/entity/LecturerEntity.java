package com.hddev244.timetable.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lecturers")
public class LecturerEntity {
    public LecturerEntity(String id) {
        this.id = id;
    }
    @Id
    private String id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "lecturer")
    private List<SubjectOfLecturerEntity> subjectOfLecturerEntities;

    @JsonIgnore
    @OneToMany(mappedBy = "lecturer")
    private List<SubjectOfGroupStudentEntity> subjectOfGroupStudentEntities;
}
