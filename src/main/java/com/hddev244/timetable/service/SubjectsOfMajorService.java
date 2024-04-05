package com.hddev244.timetable.service;

import java.util.List;

import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.entity.SubjectsOfMajorEntity;

public interface SubjectsOfMajorService {
    SubjectsOfMajorEntity create(SubjectsOfMajorEntity subjectsOfMajorEntity);
    SubjectsOfMajorEntity getById(Long id);
    List<SubjectsOfMajorEntity> getAll();
    SubjectsOfMajorEntity update(SubjectsOfMajorEntity subjectsOfMajorEntity);
    void delete(Long id);
    SubjectsOfMajorEntity getByMajorIdAndSubjectId(String majorId, String subjectId);
}