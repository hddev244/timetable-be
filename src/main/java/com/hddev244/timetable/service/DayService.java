package com.hddev244.timetable.service;

import java.util.List;

import com.hddev244.timetable.entity.DayEntity;

public interface DayService {
    DayEntity create(DayEntity daysEntity);
    DayEntity getById(Integer id);
    List<DayEntity> getAll();
    DayEntity update(DayEntity daysEntity);
    void delete(Integer id);
}