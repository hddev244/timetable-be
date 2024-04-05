
package com.hddev244.timetable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.service.SubjectOfLecturerService;

import java.util.List;

@RestController
@RequestMapping("/api/subjectsoflecturer")
public class SubjectOfLecturerController {

    @Autowired
    private SubjectOfLecturerService subjectOfLecturerService;

    @GetMapping
    public List<SubjectOfLecturerEntity> getAllSubjectsOfLecturer() {
        return subjectOfLecturerService.getAll();
    }

    @GetMapping("/{id}")
    public SubjectOfLecturerEntity getSubjectOfLecturerById(@PathVariable Long id) {
        return subjectOfLecturerService.getById(id);
    }

    @PostMapping
    public SubjectOfLecturerEntity createSubjectOfLecturer(
            @RequestBody SubjectOfLecturerEntity subjectOfLecturerEntity) {
        return subjectOfLecturerService.create(subjectOfLecturerEntity);
    }

    @PutMapping("/{lecturerId}/{subjectId}")
    public SubjectOfLecturerEntity manageSubjectOfLecturer(@PathVariable String lecturerId,
            @PathVariable String subjectId) {
        SubjectOfLecturerEntity existingEntity = subjectOfLecturerService.getByLecturerIdAndSubjectId(lecturerId,
                subjectId);
                System.out.println(existingEntity == null);
        if (existingEntity != null) {
            subjectOfLecturerService.delete(existingEntity.getId());
            return null;
        } else {
            SubjectOfLecturerEntity newEntity = new SubjectOfLecturerEntity();
            LecturerEntity lecturer = new LecturerEntity();
            lecturer.setId(lecturerId);
            SubjectEntity subject = new SubjectEntity();
            subject.setId(subjectId);
            newEntity.setLecturer(lecturer);
            newEntity.setSubject(subject);
            return subjectOfLecturerService.create(newEntity);
        }
    }
}