package com.hddev244.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hddev244.timetable.entity.SubjectEntity;

public interface SubjectRepository extends JpaRepository< SubjectEntity, String>{
}
