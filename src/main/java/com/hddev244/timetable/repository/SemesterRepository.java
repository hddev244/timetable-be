package com.hddev244.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hddev244.timetable.entity.SemesterEntity;

public interface SemesterRepository extends JpaRepository<SemesterEntity, Long> {

}
