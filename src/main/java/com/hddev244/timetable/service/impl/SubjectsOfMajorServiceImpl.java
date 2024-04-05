// src/main/java/com/hddev244/timetable/service/impl/SubjectsOfMajorServiceImpl.java
package com.hddev244.timetable.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.entity.SubjectsOfMajorEntity;
import com.hddev244.timetable.repository.SubjectsOfMajorRepository;
import com.hddev244.timetable.service.SubjectsOfMajorService;

@Service
public class SubjectsOfMajorServiceImpl implements SubjectsOfMajorService {
    @Autowired
    private SubjectsOfMajorRepository subjectsOfMajorRepository;

    @Override
    public SubjectsOfMajorEntity create(SubjectsOfMajorEntity subjectsOfMajorEntity) {
        return subjectsOfMajorRepository.save(subjectsOfMajorEntity);
    }

    @Override
    public SubjectsOfMajorEntity getById(Long id) {
        return subjectsOfMajorRepository.findById(id).orElse(null);
    }

    @Override
    public List<SubjectsOfMajorEntity> getAll() {
        return subjectsOfMajorRepository.findAll();
    }

    @Override
    public SubjectsOfMajorEntity update(SubjectsOfMajorEntity subjectsOfMajorEntity) {
        return subjectsOfMajorRepository.save(subjectsOfMajorEntity);
    }

    @Override
    public void delete(Long id) {
        subjectsOfMajorRepository.deleteById(id);
    }


    @Override
    public SubjectsOfMajorEntity getByMajorIdAndSubjectId(String majorId, String subjectId) {
        return subjectsOfMajorRepository.findByMajorIdAndSubjectId(majorId, subjectId);
    }

}