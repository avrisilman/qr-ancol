package com.qr.ancol.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Doorprize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nik;
    private String name;
    private String area;
    private String division;
    private String panitia;
    private String doorprizeSesiOne;
    private String doorprizeSesiTwo;

}
