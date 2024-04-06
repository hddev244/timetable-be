package com.hddev244.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hddev244.timetable.entity.GroupStudentEntity;

public interface GroupStudentRepository extends JpaRepository<GroupStudentEntity , String > {
     @Query("SELECT g FROM GroupStudentEntity g WHERE g.major.id = ?1 AND g.schoolYear.id = ?2")
    List<GroupStudentEntity> findByMajorIdAndStudentYearId(String majorId, Integer schoolYearId);
}
