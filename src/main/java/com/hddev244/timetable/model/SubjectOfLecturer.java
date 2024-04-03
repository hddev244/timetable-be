package com.hddev244.timetable.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectOfLecturer {
    private Long id;
    private  Subject subject;
    private  Lecturer  lecturer;
}
