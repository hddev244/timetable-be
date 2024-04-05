package com.hddev244.timetable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hddev244.timetable.entity.MajorEntity;
import com.hddev244.timetable.model.Major;
import com.hddev244.timetable.service.MajorService;

import java.util.List;

@RestController
@RequestMapping("/api/majors")
public class MajorController {
    @Autowired
    private MajorService majorService;

    @PostMapping
    public MajorEntity create(@RequestBody MajorEntity major) {
        return majorService.create(major);
    }

    @GetMapping("/{id}")
    public Major getById(@PathVariable String id) {
        return majorService.getById(id);
    }

    @GetMapping
    public List<MajorEntity> getAll() {
        return majorService.getAll();
    }

    @PutMapping
    public MajorEntity update(@RequestBody MajorEntity major) {
        return majorService.update(major);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        majorService.delete(id);
    }
}