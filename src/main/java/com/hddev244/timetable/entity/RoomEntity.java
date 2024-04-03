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
@Table(name = "Rooms")
public class RoomEntity {
    @Id
    private  Long id;

    private int roomOrdinalNumber;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "room")
    private List<SlotEntity> slots ; 
}
