package com.hddev244.timetable.controller;

import com.hddev244.timetable.entity.BlockEntity;
import com.hddev244.timetable.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {
    @Autowired
    private BlockService blockService;

    @PostMapping
    public BlockEntity create(@RequestBody BlockEntity blockEntity) {
        return blockService.create(blockEntity);
    }

    @GetMapping("/{id}")
    public BlockEntity getById(@PathVariable Long id) {
        return blockService.getById(id);
    }

    @GetMapping
    public List<BlockEntity> getAll() {
        return blockService.getAll();
    }

    @PutMapping
    public BlockEntity update(@RequestBody BlockEntity blockEntity) {
        return blockService.update(blockEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        blockService.delete(id);
    }
}