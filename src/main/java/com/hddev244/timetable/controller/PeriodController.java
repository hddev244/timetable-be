package com.hddev244.timetable.controller;

import com.hddev244.timetable.entity.PeriodEntity;
import com.hddev244.timetable.service.PeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/periods")
public class PeriodController {
    @Autowired
    private PeriodService periodService;

    @PostMapping
    public PeriodEntity create(@RequestBody PeriodEntity periodEntity) {
        return periodService.create(periodEntity);
    }

    @GetMapping("/{id}")
    public PeriodEntity getById(@PathVariable Long id) {
        return periodService.getById(id);
    }

    @GetMapping
    public List<PeriodEntity> getAll() {
        return periodService.getAll();
    }

    @PutMapping
    public PeriodEntity update(@RequestBody PeriodEntity periodEntity) {
        return periodService.update(periodEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        periodService.delete(id);
    }
}