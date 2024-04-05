package com.hddev244.timetable.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.BlockEntity;
import com.hddev244.timetable.entity.GroupStudentEntity;
import com.hddev244.timetable.entity.LecturerEntity;
import com.hddev244.timetable.entity.SemesterEntity;
import com.hddev244.timetable.entity.SubjectEntity;
import com.hddev244.timetable.entity.SubjectOfGroupStudentEntity;
import com.hddev244.timetable.model.Subject;
import com.hddev244.timetable.model.SubjectOfGroupStudent;
import com.hddev244.timetable.repository.SubjectOfGroupStudentRepository;
import com.hddev244.timetable.service.SubjectOfGroupStudentService;

@Service
public class SubjectOfGroupStudentServiceImpl implements SubjectOfGroupStudentService {
    @Autowired
    private SubjectOfGroupStudentRepository subjectOfGroupStudentRepository;

    @Override
    public SubjectOfGroupStudentEntity create(SubjectOfGroupStudentEntity subjectOfGroupStudentEntity) {
        return subjectOfGroupStudentRepository.save(subjectOfGroupStudentEntity);
    }

    @Override
    public SubjectOfGroupStudentEntity getById(Long id) {
        return subjectOfGroupStudentRepository.findById(id).orElse(null);
    }

    @Override
    public List<SubjectOfGroupStudentEntity> getAll() {
        return subjectOfGroupStudentRepository.findAll();
    }

    @Override
    public SubjectOfGroupStudentEntity update(SubjectOfGroupStudentEntity subjectOfGroupStudentEntity) {
        return subjectOfGroupStudentRepository.save(subjectOfGroupStudentEntity);
    }

    @Override
    public void delete(Long id) {
        subjectOfGroupStudentRepository.deleteById(id);
    }

    @Override
    public SubjectOfGroupStudentEntity getByGroupStudentIdAndSubjectId(String groupStudentId, String subjectId) {
        return subjectOfGroupStudentRepository.findByGroupStudentIdAndSubjectId(groupStudentId, subjectId);
    }

    @Override
    public List<SubjectEntity> getByBlockSemesterClass(Long blockId, Long semesterId, String groupStudentId) {
        List<SubjectOfGroupStudentEntity> subjectOfGroupStudentEntity = subjectOfGroupStudentRepository
                .findByBlockIdAndSemesterIdAndGroupStudentId(blockId, semesterId, groupStudentId);

        if (subjectOfGroupStudentEntity != null) {
            System.out.println(subjectOfGroupStudentEntity.size());
            return subjectOfGroupStudentEntity.stream()
                    .map(SubjectOfGroupStudentEntity::getSubject)
                    .collect(Collectors.toList());
        }
        throw new UnsupportedOperationException("Unimplemented method 'getByBlockSemesterClass'");
    }

    @Override
    public SubjectOfGroupStudentEntity change(SubjectOfGroupStudent requestBody) {
        SubjectOfGroupStudentEntity subjectOfGroupStudentEntity = subjectOfGroupStudentRepository.findForChange(
                requestBody.getBlock_id(), requestBody.getSemester_id(), requestBody.getGroupStudent_id(),
                requestBody.getSubject_id());
        System.out.println(subjectOfGroupStudentEntity == null);
        if (subjectOfGroupStudentEntity != null) {
            subjectOfGroupStudentRepository.delete(subjectOfGroupStudentEntity);
            return null;
        } else {
            SubjectOfGroupStudentEntity newEntity = new SubjectOfGroupStudentEntity();

            BlockEntity blockEntity = new BlockEntity();
            blockEntity.setId(requestBody.getBlock_id());
            newEntity.setBlock(blockEntity);

            SemesterEntity semesterEntity = new SemesterEntity();
            semesterEntity.setId(requestBody.getSemester_id());
            newEntity.setSemester(semesterEntity);

            SubjectEntity subjectEntity = new SubjectEntity();
            subjectEntity.setId(requestBody.getSubject_id());
            newEntity.setSubject(subjectEntity);

            GroupStudentEntity groupStudentEntity = new GroupStudentEntity();
            groupStudentEntity.setId(requestBody.getGroupStudent_id());
            newEntity.setGroupStudent(groupStudentEntity);

            subjectOfGroupStudentRepository.save(newEntity);
            return newEntity;
        }
    }
}