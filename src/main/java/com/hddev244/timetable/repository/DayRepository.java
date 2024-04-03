package com.hddev244.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hddev244.timetable.entity.DayEntity;

public interface DayRepository extends JpaRepository< DayEntity, Integer> {
 

}
