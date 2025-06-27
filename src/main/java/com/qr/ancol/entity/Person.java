package com.qr.ancol.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nik;
    private String name;
    private String area;
    private String division;
    private String status;
    private String tanggalJoin;
    private String lamaKerja;
    private String totalTicketAncol;
    private String totalTicketDufan;
    private String totalSouvenir;
    private Date dateSouvenir;
    private String totalSnack;
    private Date dateSnack;
    private String totalMakan;
    private Date dateMakan;
    private String mobil;
    private String motor;
    private String email;
    private Date createdDate;

}
