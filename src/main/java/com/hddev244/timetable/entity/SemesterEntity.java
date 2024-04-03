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
@Table(name = "Semesters")
public class SemesterEntity {
    @Id
    private Long id;

    private String name;

    private Integer  year;

    @JsonIgnore
    @OneToMany(mappedBy = "semester")
    private List<SubjectOfGroupStudentEntity> subjectOfGroupStudents ; 

}
