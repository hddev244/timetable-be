package com.hddev244.timetable.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "GroupStudentEntity")
public class GroupStudentEntity {
    @Id
    private String id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "groupStudent")
    private List<SubjectOfGroupStudentEntity> subjectOfGroupStudents;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private MajorEntity major;
}
