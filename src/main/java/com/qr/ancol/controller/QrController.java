package com.qr.ancol.controller;

import com.qr.ancol.entity.Person;
import com.qr.ancol.repository.PersonRepository;
import com.qr.ancol.service.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@Controller
public class QrController {

    @Autowired
    QrService qrService;

    @GetMapping("/scan-qr")
    public String handleSubmit(@RequestParam("id") Long noId, @RequestParam("type") Long type,Model model) {
        qrService.postTiketByDate(noId, type, model);

        return "qr-view";
    }
}
