package com.hddev244.timetable.service;

import java.util.List;
import java.util.Optional;

import com.hddev244.timetable.entity.SlotEntity;

public interface TimetableService {
    SlotEntity[][][] generateTimetable();
}
