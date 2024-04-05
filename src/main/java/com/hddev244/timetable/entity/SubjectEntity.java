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
@Table(name = "subjects")
public class SubjectEntity {
    public SubjectEntity(String id) {
        this.id = id;
    }
    @Id
    private String  id;
    private String name;
    private Integer numOfPeriods;

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
