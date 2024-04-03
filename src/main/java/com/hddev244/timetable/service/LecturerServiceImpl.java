package com.hddev244.timetable.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.model.Lecturer;
import com.hddev244.timetable.model.Subject;
import com.hddev244.timetable.model.SubjectOfGroupStudent;
import com.hddev244.timetable.model.SubjectOfLecturer;
import com.hddev244.timetable.repository.LecturerRepository;

@Service
public class LecturerServiceImpl implements LecturerService {
    @Autowired
    LecturerRepository repository;

    @Override
    public Lecturer create(Lecturer lecturer) {
        LecturerEntity lecturerEntity = new LecturerEntity();
        BeanUtils.copyProperties(lecturer, lecturerEntity);
        repository.save(lecturerEntity);
        return lecturer;
    }

    @Override
    public Lecturer findById(String id) {
        Optional<LecturerEntity> lecturerEntity = repository.findById(id);
        if (lecturerEntity.isPresent()) {
            LecturerEntity e = lecturerEntity.get();

            List<Subject> su = e.getSubjectOfLecturerEntities().stream()
            .map(sl ->          
                    Subject.builder()
                    .id(sl.getSubject().getId())
                    .name(sl.getSubject().getName())
                    .build()
            ).toList();

            List<SubjectOfGroupStudent> st = e.getSubjectOfGroupStudentEntities().stream()
            .map(sl ->          
                    SubjectOfGroupStudent.builder()
                    .id(sl.getId())
                    .lecturer_id(sl.getLecturer().getId())
                    .subject_id(sl.getSubject().getId())
                    .groupStudent_id(sl.getGroupStudent().getId())
                    .build()
            )
            .toList();     
            return Lecturer.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .subjectOfLecturer(su)
                    .subjectOfGroupStudent(st)
                    .build();
        }
       return null ;
    }

}
