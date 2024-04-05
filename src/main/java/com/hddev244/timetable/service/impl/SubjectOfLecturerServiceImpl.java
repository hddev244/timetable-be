package com.hddev244.timetable.service.impl;

import com.hddev244.timetable.entity.SubjectOfLecturerEntity;
import com.hddev244.timetable.repository.SubjectOfLecturerRepository;
import com.hddev244.timetable.service.SubjectOfLecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class SubjectOfLecturerServiceImpl implements SubjectOfLecturerService {

    @Autowired
    private SubjectOfLecturerRepository subjectOfLecturerRepository;

    @Override
    public List<SubjectOfLecturerEntity> getAll() {
        return subjectOfLecturerRepository.findAll();
    }

    @Override
    public SubjectOfLecturerEntity getById(Long id) {
        return subjectOfLecturerRepository.findById(id).orElse(null);
    }

    @Override
    public SubjectOfLecturerEntity create(SubjectOfLecturerEntity subjectOfLecturerEntity) {
        return subjectOfLecturerRepository.save(subjectOfLecturerEntity);
    }

    @Override
    public SubjectOfLecturerEntity getByLecturerIdAndSubjectId(String lecturerId, String subjectId) {
        return subjectOfLecturerRepository.findByLecturerIdAndSubjectId(lecturerId, subjectId);
    }

    @Override
    public void delete(Long id) {
        subjectOfLecturerRepository.deleteById(id);
    }
}