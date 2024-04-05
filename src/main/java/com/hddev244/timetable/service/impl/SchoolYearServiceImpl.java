package com.hddev244.timetable.service.impl;

import java.util.List;

// SchoolYearServiceImpl.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.SchoolYearEntity;
import com.hddev244.timetable.repository.SchoolYearRepository;
import com.hddev244.timetable.service.SchoolYearService;

@Service
public class SchoolYearServiceImpl implements SchoolYearService {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    @Override
    public SchoolYearEntity create(SchoolYearEntity schoolYear) {
        return schoolYearRepository.save(schoolYear);
    }

    @Override
    public SchoolYearEntity getById(Integer id) {
        return schoolYearRepository.findById(id).orElse(null);
    }

    @Override
    public List<SchoolYearEntity> getAll() {
        return schoolYearRepository.findAll();
    }

    @Override
    public SchoolYearEntity update(SchoolYearEntity schoolYear) {
        return schoolYearRepository.save(schoolYear);
    }

    @Override
    public void delete(Integer id) {
        schoolYearRepository.deleteById(id);
    }
}
