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

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "Days")
public class DayEntity {
    @Id
    private Integer id;
    private  String name;

    @JsonIgnore
    @OneToMany(mappedBy = "day")
    private List<SlotEntity> slots;

}
