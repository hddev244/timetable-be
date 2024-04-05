package com.hddev244.timetable.controller;

import java.util.List;

import javax.swing.GroupLayout.Group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hddev244.timetable.entity.GroupStudentEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectOfGroupStudentEntity;
import com.hddev244.timetable.model.Subject;
import com.hddev244.timetable.model.SubjectOfGroupStudent;
import com.hddev244.timetable.service.SubjectOfGroupStudentService;

@RestController
@RequestMapping("/api/subjectOfGroupStudent")
public class SubjectOfGroupStudentController {
    @Autowired
    private SubjectOfGroupStudentService subjectOfGroupStudentService;

    @PostMapping
    public SubjectOfGroupStudentEntity create(@RequestBody SubjectOfGroupStudentEntity subjectOfGroupStudentEntity) {
        return subjectOfGroupStudentService.create(subjectOfGroupStudentEntity);
    }

    @GetMapping("/{id}")
    public SubjectOfGroupStudentEntity getById(@PathVariable Long id) {
        return subjectOfGroupStudentService.getById(id);
    }
    @GetMapping("/list")
    public List<SubjectEntity> getBySubjectOfGroupStudent(@RequestParam("block_id") Long blockId, @RequestParam("semester_id") Long semesterId, @RequestParam("groupStudent_id") String groupStudentId) { 
        return subjectOfGroupStudentService.getByBlockSemesterClass(blockId, semesterId, groupStudentId);
    }

    @GetMapping
    public List<SubjectOfGroupStudentEntity> getAll() {
        return subjectOfGroupStudentService.getAll();
    }

    @PutMapping
    public SubjectOfGroupStudentEntity update(@RequestBody SubjectOfGroupStudentEntity subjectOfGroupStudentEntity) {
        return subjectOfGroupStudentService.update(subjectOfGroupStudentEntity);
    }
    @PutMapping("/change")
    public SubjectOfGroupStudentEntity change(@RequestBody SubjectOfGroupStudent requestBody) {
        System.out.println("Change");
        System.out.println(requestBody.getBlock_id());
        System.out.println(requestBody.getGroupStudent_id());
        System.out.println(requestBody.getSemester_id());
        System.out.println(requestBody.getSubject_id());

        return subjectOfGroupStudentService.change(requestBody);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subjectOfGroupStudentService.delete(id);
    }
}