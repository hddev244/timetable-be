package com.hddev244.timetable.service;

// SchoolYearService.java
import java.util.List;

import com.hddev244.timetable.entity.SchoolYearEntity;

public interface SchoolYearService {
    SchoolYearEntity create(SchoolYearEntity schoolYear);
    SchoolYearEntity getById(Integer id);
    List<SchoolYearEntity> getAll();
    SchoolYearEntity update(SchoolYearEntity schoolYear);
    void delete(Integer id);
}
