package com.hddev244.timetable.service;

import com.hddev244.timetable.model.Lecturer;

public interface LecturerService {

    Lecturer create(Lecturer lecturer);

    Lecturer findById(String id);
    
}
