package com.hddev244.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hddev244.timetable.entity.BlockEntity;

public interface BlockRepository extends JpaRepository< BlockEntity, Integer> {

}
