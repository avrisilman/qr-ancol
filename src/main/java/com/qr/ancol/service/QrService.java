package com.qr.ancol.service;

import com.qr.ancol.entity.Person;
import com.qr.ancol.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.Optional;

@Service
public class QrService {
    @Autowired
    private PersonRepository personRepository;

    public void postTiketByDate(Long id, Long type, Model model) {
        model.addAttribute("hasil", postTiketByDateAjax(id, type));
    }

    public String postTiketByDateAjax(Long id, Long type) {
        Optional<Person> p = personRepository.findById(id);

        if (p.isPresent()) {
            Person person = p.get();
            Date now = new Date();

            if (type.equals(1L) && person.getDateSouvenir() == null) {
                person.setDateSouvenir(now);
                personRepository.save(person);
                return "✅ Tiket Souvenir berhasil untuk: " + person.getName();
            } else if (type.equals(2L) && person.getDateMakan() == null) {
                person.setDateMakan(now);
                personRepository.save(person);
                return "✅ Tiket Makan berhasil untuk: " + person.getName();
            } else if (type.equals(3L) && person.getDateSnack() == null) {
                person.setDateSnack(now);
                personRepository.save(person);
                return "✅ Tiket Snack berhasil untuk: " + person.getName();
            } else {
                return "❌ Tiket sudah pernah digunakan atau tipe tidak valid.";
            }
        } else {
            return "❌ Gagal! ID " + id + " tidak ditemukan.";
        }
    }
}
