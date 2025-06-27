package com.qr.ancol.service;

import com.qr.ancol.entity.Person;
import com.qr.ancol.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    PersonRepository personRepository;

    public void processConfirmation(String nik, Model model) {
        Optional<Person> optional = personRepository.findByNik(nik);

        if (optional.isEmpty()) {
            model.addAttribute("error", "Data tidak ditemukan");
            return;
        }

        Person person = optional.get();
        Date now = new Date();

        model.addAttribute("nik", nik);
        model.addAttribute("name", person.getName());

        // === SOUVENIR ===
        if (person.getDateSouvenir() == null) {
            person.setDateSouvenir(now);
            personRepository.save(person);
            model.addAttribute("souvenir", "Berhasil ambil Souvenir: " + person.getTotalSouvenir());
            return;
        } else if (now.getTime() - person.getDateSouvenir().getTime() <= 3600000) {
            model.addAttribute("souvenir", "Souvenir sudah diambil dalam 1 jam terakhir.");
            return;
        }

        // === MAKAN ===
        if (person.getDateMakan() == null) {
            person.setDateMakan(now);
            personRepository.save(person);
            model.addAttribute("makan", "Berhasil ambil Tiket Makan: " + person.getTotalMakan());
            return;
        } else if (now.getTime() - person.getDateMakan().getTime() <= 3600000) {
            model.addAttribute("makan", "Tiket Makan sudah diambil dalam 1 jam terakhir.");
            return;
        }

        // === SNACK ===
        if (person.getDateSnack() == null) {
            person.setDateSnack(now);
            personRepository.save(person);
            model.addAttribute("snack", "Berhasil ambil Tiket Snack: " + person.getTotalSnack());
            return;
        } else if (now.getTime() - person.getDateSnack().getTime() <= 3600000) {
            model.addAttribute("snack", "Tiket Snack sudah diambil dalam 1 jam terakhir.");
            return;
        }

        // === SEMUA SUDAH DIAMBIL >1 JAM ===
        model.addAttribute("info", "Semua tiket sudah diambil lebih dari 1 jam yang lalu.");
    }



}
