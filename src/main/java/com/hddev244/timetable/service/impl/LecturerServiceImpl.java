package com.hddev244.timetable.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.entity.SubjectOfGroupStudentEntity;
import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.model.Lecturer;
import com.hddev244.timetable.model.Subject;
import com.hddev244.timetable.model.SubjectOfGroupStudent;
import com.hddev244.timetable.repository.LecturerRepository;
import com.hddev244.timetable.service.LecturerService;

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
        Optional<LecturerEntity> lecturerEntityOptional = repository.findById(id);
        return lecturerEntityOptional.map(this::mapLecturerEntityToLecturer).orElse(null);
    }

    @Override
    public List<Lecturer> findAll() {
        List<LecturerEntity> lecturerEntities = repository.findAll();
        return lecturerEntities.stream().map(this::mapLecturerEntityToLecturer).collect(Collectors.toList());
    }

    private Lecturer mapLecturerEntityToLecturer(LecturerEntity entity) {
        List<Subject> subjects = entity.getSubjectOfLecturerEntities().stream()
                .map(this::mapSubjectOfLecturerEntityToSubject)
                .collect(Collectors.toList());

        List<SubjectOfGroupStudent> subjectsOfGroupStudent = entity.getSubjectOfGroupStudentEntities().stream()
                .map(this::mapSubjectOfGroupStudentEntityToSubjectOfGroupStudent)
                .collect(Collectors.toList());

        return Lecturer.builder()
                .id(entity.getId())
                .name(entity.getName())
                .subjects(subjects)
                .subjectOfGroupStudent(subjectsOfGroupStudent)
                .build();
    }

    private Subject mapSubjectOfLecturerEntityToSubject(SubjectOfLecturerEntity sl) {
        return Subject.builder()
                .id(sl.getSubject().getId())
                .name(sl.getSubject().getName())
                .build();
    }

    private SubjectOfGroupStudent mapSubjectOfGroupStudentEntityToSubjectOfGroupStudent(
            SubjectOfGroupStudentEntity sl) {
        return SubjectOfGroupStudent.builder()
                .id(sl.getId())
                .lecturer_id(sl.getLecturer().getId())
                .subject_id(sl.getSubject().getId())
                .groupStudent_id(sl.getGroupStudent().getId())
                .build();
    }

}
