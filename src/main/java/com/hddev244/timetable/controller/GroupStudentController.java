package com.hddev244.timetable.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hddev244.timetable.entity.GroupStudentEntity;
import com.hddev244.timetable.service.GroupStudentService;

import java.util.List;

@RestController
@RequestMapping("/api/groupStudents")
public class GroupStudentController {
    @Autowired
    private GroupStudentService groupStudentService;

    @PostMapping
    public GroupStudentEntity create(@RequestBody GroupStudentEntity groupStudent) {
        return groupStudentService.create(groupStudent);
    }

    @GetMapping("/{id}")
    public GroupStudentEntity getById(@PathVariable String id) {
        return groupStudentService.getById(id);
    }

    @GetMapping
    public List<GroupStudentEntity> getAll() {
        return groupStudentService.getAll();
    }

    @PutMapping
    public GroupStudentEntity update(@RequestBody GroupStudentEntity groupStudent) {
        return groupStudentService.update(groupStudent);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        groupStudentService.delete(id);
    }
}