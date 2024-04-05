package com.hddev244.timetable.service.impl;

import com.hddev244.timetable.entity.BlockEntity;
import com.hddev244.timetable.repository.BlockRepository;
import com.hddev244.timetable.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockServiceImpl implements BlockService {
    @Autowired
    private BlockRepository blockRepository;

    @Override
    public BlockEntity create(BlockEntity blockEntity) {
        return blockRepository.save(blockEntity);
    }

    @Override
    public BlockEntity getById(Long id) {
        return blockRepository.findById(id).orElse(null);
    }

    @Override
    public List<BlockEntity> getAll() {
        return blockRepository.findAll();
    }

    @Override
    public BlockEntity update(BlockEntity blockEntity) {
        return blockRepository.save(blockEntity);
    }

    @Override
    public void delete(Long id) {
        blockRepository.deleteById(id);
    }
}