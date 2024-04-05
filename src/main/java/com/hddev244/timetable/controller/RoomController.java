// src/main/java/com/hddev244/timetable/controller/RoomController.java
package com.hddev244.timetable.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hddev244.timetable.entity.RoomEntity;
import com.hddev244.timetable.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping
    public RoomEntity create(@RequestBody RoomEntity roomEntity) {
        return roomService.create(roomEntity);
    }

    @GetMapping("/{id}")
    public RoomEntity getById(@PathVariable Long id) {
        return roomService.getById(id);
    }

    @GetMapping
    public List<RoomEntity> getAll() {
        return roomService.getAll();
    }

    @PutMapping
    public RoomEntity update(@RequestBody RoomEntity roomEntity) {
        return roomService.update(roomEntity);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
}