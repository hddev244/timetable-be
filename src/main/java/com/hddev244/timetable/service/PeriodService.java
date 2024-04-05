package com.hddev244.timetable.service;

import com.hddev244.timetable.entity.PeriodEntity;
import java.util.List;

public interface PeriodService {
    PeriodEntity create(PeriodEntity periodEntity);
    PeriodEntity getById(Long id);
    List<PeriodEntity> getAll();
    PeriodEntity update(PeriodEntity periodEntity);
    void delete(Long id);
}
