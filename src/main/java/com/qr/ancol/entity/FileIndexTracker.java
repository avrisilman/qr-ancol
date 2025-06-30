package com.qr.ancol.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "file_index_tracker")
@Getter
@Setter
public class FileIndexTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, unique = true)
    private String type; // e.g. "ancol", "mobil", "motor"

    @Column(name = "current_index")
    private int currentIndex;
}
