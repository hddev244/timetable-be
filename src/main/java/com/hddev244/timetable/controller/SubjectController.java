// SubjectController.java
package com.hddev244.timetable.controller;

import com.hddev244.timetable.model.Subject;
import com.hddev244.timetable.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;
    
    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/class/{id}")
    public List<Subject> getSubjectByMajorIdId(@PathVariable String id) {
        return subjectService.getSubjectByClassId(id);
    }

    @GetMapping("/{id}")
    public Subject getSubjectById(@PathVariable String id) {
        return subjectService.getSubjectById(id);
    }

    @PostMapping
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }
}