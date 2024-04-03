package com.hddev244.timetable.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Slots")
public class SlotEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private DayEntity day;

    @ManyToOne
    @JoinColumn(name = "period_id")
    private PeriodEntity period;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @ManyToOne
    @JoinColumn(name = "subjectOfGroupStudent_id")
    private SubjectOfGroupStudentEntity subjectOfGroupStudent;

}
