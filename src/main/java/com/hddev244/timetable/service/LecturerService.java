package com.hddev244.timetable.service;

import java.util.List;

import com.hddev244.timetable.model.Lecturer;

public interface LecturerService {

    Lecturer create(Lecturer lecturer);

    Lecturer findById(String id);

    List<Lecturer> findAll();
    
}
