package com.hddev244.timetable.service.impl;

import com.hddev244.timetable.entity.DayEntity;
import com.hddev244.timetable.repository.DayRepository;
import com.hddev244.timetable.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DayServiceImpl implements DayService {
    @Autowired
    private DayRepository dayRepository;

    @Override
    public DayEntity create(DayEntity dayEntity) {
        return dayRepository.save(dayEntity);
    }

    @Override
    public DayEntity getById(Integer id) {
        return dayRepository.findById(id).orElse(null);
    }

    @Override
    public List<DayEntity> getAll() {
        return dayRepository.findAll();
    }

    @Override
    public DayEntity update(DayEntity dayEntity) {
        return dayRepository.save(dayEntity);
    }

    @Override
    public void delete(Integer id) {
        dayRepository.deleteById(id);
    }
}
