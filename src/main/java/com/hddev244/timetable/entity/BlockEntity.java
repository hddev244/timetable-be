package com.hddev244.timetable.entity;

import java.util.List;

import org.aspectj.weaver.patterns.ConcreteCflowPointcut.Slot;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "Blocks")
public class BlockEntity {
    @Id
    private Long id;
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "block")
    private List<SubjectOfGroupStudentEntity> subjectOfGroupStudents ; //
}
