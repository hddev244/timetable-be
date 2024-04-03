package com.hddev244.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hddev244.timetable.entity.LecturerEntity;

@Repository
public interface LecturerRepository extends JpaRepository< LecturerEntity,String >{
}
