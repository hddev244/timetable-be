package com.hddev244.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hddev244.timetable.entity.GroupStudentEntity;

public interface GroupStudentRepository extends JpaRepository<GroupStudentEntity , String > {
}
