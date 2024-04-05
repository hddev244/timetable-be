// src/main/java/com/hddev244/timetable/service/SemesterService.java
package com.hddev244.timetable.service;

import com.hddev244.timetable.entity.SemesterEntity;
import java.util.List;

public interface SemesterService {
    SemesterEntity create(SemesterEntity semesterEntity);
    SemesterEntity getById(Long id);
    List<SemesterEntity> getAll();
    SemesterEntity update(SemesterEntity semesterEntity);
    void delete(Long id);
}