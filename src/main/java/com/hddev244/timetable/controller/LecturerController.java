package com.hddev244.timetable.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hddev244.timetable.model.Lecturer;
import com.hddev244.timetable.service.LecturerService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {
    private final LecturerService lecturerService;
    @GetMapping
    public List<Lecturer> getAll() {
        return lecturerService.findAll();
    }
    @GetMapping("/{id}")
    public Lecturer getOne(@PathVariable("id") String id) {
        return lecturerService.findById(id);
    }
    @PostMapping
    public ResponseEntity<Lecturer> postMethodName(@RequestBody Lecturer lecturer) {
        Lecturer createdLecturer = lecturerService.create(lecturer) ;
        return new ResponseEntity<>(createdLecturer, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody Lecturer lecturer) {
        
        return null;
    }
    @DeleteMapping
    public ResponseEntity<?> delete(){
        return ResponseEntity.ok().build();
    }
}
