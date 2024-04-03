package com.hddev244.timetable.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectOfGroupStudent {
    private Long id;
    private String subject_id;
    private String lecturer_id;
    private String groupStudent_id;
    // private List<SlotEntity> slots ; 
}
