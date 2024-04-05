// SubjectServiceImpl.java
package com.hddev244.timetable.service.impl;

import com.hddev244.timetable.entity.GroupStudentEntity;
import com.hddev244.timetable.entity.MajorEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectsOfMajorEntity;
import com.hddev244.timetable.model.Subject;
import com.hddev244.timetable.repository.GroupStudentRepository;
import com.hddev244.timetable.repository.SubjectRepository;
import com.hddev244.timetable.repository.SubjectsOfMajorRepository;
import com.hddev244.timetable.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private GroupStudentRepository groupStudentRepository;
    @Autowired
    private SubjectsOfMajorRepository subjectsOfMajorRepository;

    @Override
    public List<Subject> getAllSubjects() {
        List<SubjectEntity> subjectEntities = subjectRepository.findAll();

        List<Subject> subjects = subjectEntities.stream()
                .map(entity -> Subject.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .numOfPeriods(entity.getNumOfPeriods())
                        .build())
                .toList();
        return subjects;
    }

    @Override
    public Subject getSubjectById(String id) {
        SubjectEntity subjectEntity = subjectRepository.findById(id).orElse(null);
        if (subjectEntity == null) {
            return null;
        }
        Subject subject = Subject.builder()
                .id(subjectEntity.getId())
                .name(subjectEntity.getName())
                .numOfPeriods(subjectEntity.getNumOfPeriods())
                .build();
        return subject;
    }

    @Override
    public Subject createSubject(Subject subject) {
        SubjectEntity subjectEntity = SubjectEntity.builder()
                .id(subject.getId())
                .name(subject.getName())
                .numOfPeriods(subject.getNumOfPeriods())
                .build();
        subjectRepository.save(subjectEntity);
        return subject;
    }

    @Override
    public List<Subject> getSubjectByClassId(String id) {
        Optional<GroupStudentEntity> groupStudentEntity = groupStudentRepository.findById(id);
        System.out.println(groupStudentEntity.isPresent());
        if (groupStudentEntity.isPresent()) {
            MajorEntity majorEntity = groupStudentEntity.get().getMajor();
            List<SubjectsOfMajorEntity> subjectsOfMajorEntities = subjectsOfMajorRepository.findByMajor(majorEntity);
            List<Subject> subjects = subjectsOfMajorEntities.stream()
                    .map(entity -> Subject.builder()
                            .id(entity.getSubject().getId())
                            .name(entity.getSubject().getName())
                            .numOfPeriods(entity.getSubject().getNumOfPeriods())
                            .build())
                    .toList();
                    return subjects;
        }
        return null;
    }
}