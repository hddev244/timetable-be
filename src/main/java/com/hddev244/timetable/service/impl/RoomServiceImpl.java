package com.hddev244.timetable.service.impl;

import com.hddev244.timetable.entity.RoomEntity;
import com.hddev244.timetable.repository.RoomRepository;
import com.hddev244.timetable.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Override
    public RoomEntity create(RoomEntity roomEntity) {
        return roomRepository.save(roomEntity);
    }

    @Override
    public RoomEntity getById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public List<RoomEntity> getAll() {
        return roomRepository.findAll();
    }

    @Override
    public RoomEntity update(RoomEntity roomEntity) {
        return roomRepository.save(roomEntity);
    }

    @Override
    public void delete(Long id) {
        roomRepository.deleteById(id);
    }
}