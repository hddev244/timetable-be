package com.hddev244.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hddev244.timetable.entity.MajorEntity;
import com.hddev244.timetable.entity.SubjectsOfMajorEntity;

public interface SubjectsOfMajorRepository extends JpaRepository<SubjectsOfMajorEntity,Long>  {
    @Query("SELECT s FROM SubjectsOfMajorEntity s WHERE s.major.id = :majorId AND s.subject.id = :subjectId")
    SubjectsOfMajorEntity findByMajorIdAndSubjectId(String majorId, String subjectId);

    List<SubjectsOfMajorEntity> findByMajor(MajorEntity majorEntity);

}
