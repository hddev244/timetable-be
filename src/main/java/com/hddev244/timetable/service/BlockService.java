package com.hddev244.timetable.service;

import com.hddev244.timetable.entity.BlockEntity;
import java.util.List;

public interface BlockService {
    BlockEntity create(BlockEntity blockEntity);
    BlockEntity getById(Long id);
    List<BlockEntity> getAll();
    BlockEntity update(BlockEntity blockEntity);
    void delete(Long id);
}