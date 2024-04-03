package com.hddev244.timetable.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "SubjectsOfMajors")
public class SubjectsOfMajorEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "major_id")
    private MajorEntity major;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subject;
}
