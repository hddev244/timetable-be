package com.hddev244.timetable.service;

// GroupStudentService.java
import java.util.List;

import com.hddev244.timetable.entity.GroupStudentEntity;

public interface GroupStudentService {
    GroupStudentEntity create(GroupStudentEntity groupStudent);
    GroupStudentEntity getById(String id);
    List<GroupStudentEntity> getAll();
    GroupStudentEntity update(GroupStudentEntity groupStudent);
    void delete(String id);
    List<GroupStudentEntity> getByMajorAndStudentYear(String majorId, Integer schoolYearId);
}