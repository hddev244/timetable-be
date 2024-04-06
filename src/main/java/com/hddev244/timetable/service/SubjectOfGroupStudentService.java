// src/main/java/com/hddev244/timetable/service/SubjectOfGroupStudentService.java
package com.hddev244.timetable.service;

import com.hddev244.timetable.entity.GroupStudentEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectOfGroupStudentEntity;
import com.hddev244.timetable.model.SubjectOfGroupStudent;

import java.util.List;

public interface SubjectOfGroupStudentService {
    SubjectOfGroupStudentEntity create(SubjectOfGroupStudentEntity subjectOfGroupStudentEntity);
    SubjectOfGroupStudentEntity getById(Long id);
    List<SubjectOfGroupStudentEntity> getAll();
    SubjectOfGroupStudentEntity update(SubjectOfGroupStudentEntity subjectOfGroupStudentEntity);
    void delete(Long id);
    SubjectOfGroupStudentEntity getByGroupStudentIdAndSubjectId(String groupStudentId, String subjectId);
    List<SubjectEntity> getByBlockSemesterClass(Long blockId, Long semesterId, String groupStudentId);
    SubjectOfGroupStudentEntity change(SubjectOfGroupStudent requestBody);

    List<GroupStudentEntity> getBySubjectOfGroupStudentBySubject(Long blockId, Long semesterId, String subject_id, String major_id, Integer schoolYear_id);
}