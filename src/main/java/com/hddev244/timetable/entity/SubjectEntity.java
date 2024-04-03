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

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "subjects")
public class SubjectEntity {
    @Id
    private String  id;
    private String name;
    private Integer numOfPeriods = 3;

    @JsonIgnore
    @OneToMany(mappedBy = "subject")
    private List<SubjectOfLecturerEntity> subjectOfLecturerEntities;

    @JsonIgnore
    @OneToMany(mappedBy = "subject")
    private List<SubjectOfGroupStudentEntity> subjectOfGroupStudentEntities ;

    @JsonIgnore
    @OneToMany(mappedBy = "subject")
    private List<SubjectsOfMajorEntity> subjectsOfMajors;

}
