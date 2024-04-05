package com.hddev244.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hddev244.timetable.entity.SubjectOfGroupStudentEntity;

public interface SubjectOfGroupStudentRepository extends JpaRepository<SubjectOfGroupStudentEntity , Long> {
    @Query("SELECT s FROM SubjectOfGroupStudentEntity s WHERE s.groupStudent.id = ?1 AND s.subject.id = ?2")
    SubjectOfGroupStudentEntity findByGroupStudentIdAndSubjectId(String groupStudentId, String subjectId);
    @Query("SELECT s FROM SubjectOfGroupStudentEntity s WHERE s.block.id = ?1 AND s.semester.id = ?2 AND s.groupStudent.id = ?3")
    List<SubjectOfGroupStudentEntity> findByBlockIdAndSemesterIdAndGroupStudentId(Long blockId, Long semesterId,
            String groupStudentId);
    @Query("SELECT s FROM SubjectOfGroupStudentEntity s WHERE s.block.id = ?1 AND s.semester.id = ?2 AND s.groupStudent.id = ?3 AND s.subject.id = ?4")
    SubjectOfGroupStudentEntity findForChange(Long block_id, Long semester_id, String groupStudent_id,
            String subject_id);

    @Query("SELECT s FROM SubjectOfGroupStudentEntity s WHERE s.block.id = ?1 AND s.semester.id = ?2")        
    List<SubjectOfGroupStudentEntity> findByBlockIdAndSemesterId(Long block_id, Long semester_id);
}
