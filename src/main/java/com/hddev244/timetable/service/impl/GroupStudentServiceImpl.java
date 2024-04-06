package com.hddev244.timetable.service.impl;

import java.util.List;

// GroupStudentServiceImpl.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.GroupStudentEntity;
import com.hddev244.timetable.repository.GroupStudentRepository;
import com.hddev244.timetable.service.GroupStudentService;

@Service
public class GroupStudentServiceImpl implements GroupStudentService {
    @Autowired
    private GroupStudentRepository groupStudentRepository;

    @Override
    public GroupStudentEntity create(GroupStudentEntity groupStudent) {
        return groupStudentRepository.save(groupStudent);
    }

    @Override
    public GroupStudentEntity getById(String id) {
        return groupStudentRepository.findById(id).orElse(null);
    }

    @Override
    public List<GroupStudentEntity> getAll() {
        return groupStudentRepository.findAll();
    }

    @Override
    public GroupStudentEntity update(GroupStudentEntity groupStudent) {
        return groupStudentRepository.save(groupStudent);
    }

    @Override
    public void delete(String id) {
        groupStudentRepository.deleteById(id);
    }

    @Override
    public List<GroupStudentEntity> getByMajorAndStudentYear(String majorId, Integer schoolYearId) {
        return groupStudentRepository.findByMajorIdAndStudentYearId(majorId, schoolYearId);
    }
}
