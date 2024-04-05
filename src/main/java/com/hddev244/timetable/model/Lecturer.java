package com.hddev244.timetable.model;

import java.util.List;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Lecturer {
    @Id
    private String id;
    private String name;
    private List<Subject> subjects;
    private List<SubjectOfGroupStudent> subjectOfGroupStudent;
}
