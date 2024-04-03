package com.hddev244.timetable.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Subject {
    private String  id;
    private String name;
    private Integer numOfPeriods;

    // private List<SubjectOfLecturerEntity> subjectOfLecturerEntities;

    // private List<SubjectOfGroupStudentEntity> subjectOfGroupStudentEntities ;

    // private List<SubjectsOfMajorEntity> subjectsOfMajors;
}
