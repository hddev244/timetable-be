package com.hddev244.timetable.controller;

import com.hddev244.timetable.entity.DayEntity;
import com.hddev244.timetable.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/days")
public class DayController {
    @Autowired
    private DayService dayService;

    @PostMapping
    public DayEntity create(@RequestBody DayEntity dayEntity) {
        return dayService.create(dayEntity);
    }

    @GetMapping("/{id}")
    public DayEntity getById(@PathVariable Integer id) {
        return dayService.getById(id);
    }

    @GetMapping
    public List<DayEntity> getAll() {
        return dayService.getAll();
    }

    @PutMapping
    public DayEntity update(@RequestBody DayEntity dayEntity) {
        return dayService.update(dayEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        dayService.delete(id);
    }
}
