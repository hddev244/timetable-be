package com.hddev244.timetable.service.impl;

import com.hddev244.timetable.entity.PeriodEntity;
import com.hddev244.timetable.repository.PeriodRepository;
import com.hddev244.timetable.service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodServiceImpl implements PeriodService {
    @Autowired
    private PeriodRepository periodRepository;

    @Override
    public PeriodEntity create(PeriodEntity periodEntity) {
        return periodRepository.save(periodEntity);
    }

    @Override
    public PeriodEntity getById(Long id) {
        return periodRepository.findById(id).orElse(null);
    }

    @Override
    public List<PeriodEntity> getAll() {
        return periodRepository.findAll();
    }

    @Override
    public PeriodEntity update(PeriodEntity periodEntity) {
        return periodRepository.save(periodEntity);
    }

    @Override
    public void delete(Long id) {
        periodRepository.deleteById(id);
    }
}