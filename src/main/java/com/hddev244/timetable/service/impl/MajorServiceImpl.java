package com.hddev244.timetable.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hddev244.timetable.entity.MajorEntity;
import com.hddev244.timetable.model.Major;
import com.hddev244.timetable.model.Subject;
import com.hddev244.timetable.repository.MajorRepository;
import com.hddev244.timetable.service.MajorService;

@Service
public class MajorServiceImpl implements MajorService {
    @Autowired
    private MajorRepository majorRepository;

    @Override
    public MajorEntity create(MajorEntity major) {
        return majorRepository.save(major);
    }

    @Override
    public Major getById(String id) {
        MajorEntity majorEntity = majorRepository.findById(id).orElse(null);
        if(majorEntity != null ){
            return Major.builder()
                        .id(majorEntity.getId())
                        .name(majorEntity.getName())
                        .subjects(majorEntity.getSubjectsOfMajors().stream().map(s -> 
                            Subject.builder()
                                    .id(s.getSubject().getId())
                                    .name(s.getSubject().getName())
                                    .numOfPeriods(s.getSubject().getNumOfPeriods())
                                    .build()
                        ).collect(Collectors.toList())) // Convert the stream to a list
                        .build();
        }
        return null;
    }

    @Override
    public List<MajorEntity> getAll() {
        return majorRepository.findAll();
    }

    @Override
    public MajorEntity update(MajorEntity major) {
        return majorRepository.save(major);
    }

    @Override
    public void delete(String id) {
        majorRepository.deleteById(id);
    }
}
