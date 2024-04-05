package com.hddev244.timetable.service;

import com.hddev244.timetable.entity.SubjectOfLecturerEntity;

import java.util.List;

public interface SubjectOfLecturerService {
    List<SubjectOfLecturerEntity> getAll();
    SubjectOfLecturerEntity getById(Long id);
    SubjectOfLecturerEntity create(SubjectOfLecturerEntity subjectOfLecturerEntity);
    SubjectOfLecturerEntity getByLecturerIdAndSubjectId(String lecturerId, String subjectId);
    void delete(Long id);
}