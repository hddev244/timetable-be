package com.hddev244.timetable.service;

import com.hddev244.timetable.entity.RoomEntity;
import java.util.List;

public interface RoomService {
    RoomEntity create(RoomEntity roomEntity);
    RoomEntity getById(Long id);
    List<RoomEntity> getAll();
    RoomEntity update(RoomEntity roomEntity);
    void delete(Long id);
}