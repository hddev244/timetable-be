// src/main/java/com/hddev244/timetable/controller/SemesterController.java
package com.hddev244.timetable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hddev244.timetable.entity.SemesterEntity;
import com.hddev244.timetable.service.SemesterService;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {
    @Autowired
    private SemesterService semesterService;

    @PostMapping
    public SemesterEntity create(@RequestBody SemesterEntity semesterEntity) {
        return semesterService.create(semesterEntity);
    }

    @GetMapping("/{id}")
    public SemesterEntity getById(@PathVariable Long id) {
        return semesterService.getById(id);
    }

    @GetMapping
    public List<SemesterEntity> getAll() {
        return semesterService.getAll();
    }

    @PutMapping
    public SemesterEntity update(@RequestBody SemesterEntity semesterEntity) {
        return semesterService.update(semesterEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        semesterService.delete(id);
    }
}