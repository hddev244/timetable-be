// src/main/java/com/hddev244/timetable/controller/SubjectsOfMajorController.java
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

import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.entity.MajorEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.entity.SubjectsOfMajorEntity;
import com.hddev244.timetable.model.Major;
import com.hddev244.timetable.service.SubjectsOfMajorService;

@RestController
@RequestMapping("/api/subjectsOfMajor")
public class SubjectsOfMajorController {
    @Autowired
    private SubjectsOfMajorService subjectsOfMajorService;

    @PostMapping
    public SubjectsOfMajorEntity create(@RequestBody SubjectsOfMajorEntity subjectsOfMajorEntity) {
        return subjectsOfMajorService.create(subjectsOfMajorEntity);
    }

    @GetMapping("/{id}")
    public SubjectsOfMajorEntity getById(@PathVariable Long id) {
        return subjectsOfMajorService.getById(id);
    }

    @GetMapping
    public List<SubjectsOfMajorEntity> getAll() {
        return subjectsOfMajorService.getAll();
    }

    @PutMapping
    public SubjectsOfMajorEntity update(@RequestBody SubjectsOfMajorEntity subjectsOfMajorEntity) {
        return subjectsOfMajorService.update(subjectsOfMajorEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subjectsOfMajorService.delete(id);
    }

    @PutMapping("/{majorId}/{subjectId}")
    public SubjectsOfMajorEntity manageSubjectOfLecturer(@PathVariable String majorId,
            @PathVariable String subjectId) {
                System.out.println(majorId + " " +subjectId);
        SubjectsOfMajorEntity existingEntity = subjectsOfMajorService.getByMajorIdAndSubjectId(majorId,
                subjectId);
        System.out.println(existingEntity == null);
        if (existingEntity != null) {
            subjectsOfMajorService.delete(existingEntity.getId());
            return null;
        } else {
            SubjectsOfMajorEntity newEntity = new SubjectsOfMajorEntity();
            MajorEntity majorEntity = new MajorEntity();
            majorEntity.setId(majorId);
            SubjectEntity subject = new SubjectEntity();
            subject.setId(subjectId);
            newEntity.setMajor(majorEntity);
            newEntity.setSubject(subject);
            return subjectsOfMajorService.create(newEntity);
        }
        // return null;

    }
}