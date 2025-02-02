package com.hddev244.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.entity.SubjectOfLecturerEntity;


public interface SubjectOfLecturerRepository  extends JpaRepository<SubjectOfLecturerEntity ,Long > {
    @Query("SELECT o.lecturer FROM SubjectOfLecturerEntity o WHERE o.subject.id LIKE ?1")
    List<LecturerEntity> findBySubjectId(String id);
    
    @Query("SELECT o FROM SubjectOfLecturerEntity o WHERE o.lecturer.id LIKE ?1 AND o.subject.id LIKE ?2")
    SubjectOfLecturerEntity findByLecturerIdAndSubjectId(String lecturerId, String subjectId);

}
