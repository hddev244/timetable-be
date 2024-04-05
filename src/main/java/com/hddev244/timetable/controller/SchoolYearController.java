package com.hddev244.timetable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hddev244.timetable.entity.SchoolYearEntity;
import com.hddev244.timetable.service.SchoolYearService;

import java.util.List;

@RestController
@RequestMapping("/api/schoolYears")
public class SchoolYearController {
    @Autowired
    private SchoolYearService schoolYearService;

    @PostMapping
    public SchoolYearEntity create(@RequestBody SchoolYearEntity schoolYear) {
        return schoolYearService.create(schoolYear);
    }

    @GetMapping("/{id}")
    public SchoolYearEntity getById(@PathVariable Integer id) {
        return schoolYearService.getById(id);
    }

    @GetMapping
    public List<SchoolYearEntity> getAll() {
        return schoolYearService.getAll();
    }

    @PutMapping
    public SchoolYearEntity update(@RequestBody SchoolYearEntity schoolYear) {
        return schoolYearService.update(schoolYear);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        schoolYearService.delete(id);
    }
}