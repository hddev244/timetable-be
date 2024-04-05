// SubjectService.java
package com.hddev244.timetable.service;

import com.hddev244.timetable.model.Subject;

import java.util.List;

public interface SubjectService {
    List<Subject> getAllSubjects();
    Subject getSubjectById(String id);
    Subject createSubject(Subject subject);
    List<Subject> getSubjectByClassId(String id);
}