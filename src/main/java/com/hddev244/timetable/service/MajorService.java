package com.hddev244.timetable.service;
// MajorService.java
import java.util.List;

import com.hddev244.timetable.entity.MajorEntity;
import com.hddev244.timetable.model.Major;

public interface MajorService {
    MajorEntity create(MajorEntity major);
    Major getById(String id);
    List<MajorEntity> getAll();
    MajorEntity update(MajorEntity major);
    void delete(String id);
}