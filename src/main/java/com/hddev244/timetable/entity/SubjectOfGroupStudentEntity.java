package com.hddev244.timetable.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "SubjectOfGroupStudent")
public class SubjectOfGroupStudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectEntity subject;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private LecturerEntity lecturer;

    @ManyToOne
    @JoinColumn(name = "group_student_id")
    private GroupStudentEntity groupStudent;

    @JsonIgnore
    @OneToMany(mappedBy = "subjectOfGroupStudent")
    private List<SlotEntity> slots; 
    
    @ManyToOne
    @JoinColumn(name = "block_id")
    private BlockEntity block;
    
    @ManyToOne
    @JoinColumn(name = "semester_id")
    private SemesterEntity semester;
}
