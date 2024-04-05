package com.hddev244.timetable.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
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
@Table(name = "SchoolYears")
public class SchoolYearEntity {
    public SchoolYearEntity(Integer id) {
        this.id = id;
    }
    @Id
    private Integer id;
    private String name;
    @JsonIgnore
    @OneToMany(mappedBy = "schoolYear")
    private List<GroupStudentEntity> groupStudents;
}