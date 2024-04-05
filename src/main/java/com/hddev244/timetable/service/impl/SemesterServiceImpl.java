// src/main/java/com/hddev244/timetable/service/impl/SemesterServiceImpl.java
package com.hddev244.timetable.service.impl;

import com.hddev244.timetable.entity.SemesterEntity;
import com.hddev244.timetable.repository.SemesterRepository;
import com.hddev244.timetable.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {
    @Autowired
    private SemesterRepository semesterRepository;

    @Override
    public SemesterEntity create(SemesterEntity semesterEntity) {
        return semesterRepository.save(semesterEntity);
    }

    @Override
    public SemesterEntity getById(Long id) {
        return semesterRepository.findById(id).orElse(null);
    }

    @Override
    public List<SemesterEntity> getAll() {
        return semesterRepository.findAll();
    }

    @Override
    public SemesterEntity update(SemesterEntity semesterEntity) {
        return semesterRepository.save(semesterEntity);
    }

    @Override
    public void delete(Long id) {
        semesterRepository.deleteById(id);
    }
}