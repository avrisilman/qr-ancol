package com.qr.ancol.controller;

import com.qr.ancol.entity.Person;
import com.qr.ancol.repository.PersonRepository;
import com.qr.ancol.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/")
    public String home(Model model) {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadExcel(@RequestParam("file") MultipartFile file, Model model) {
        String message = excelService.readAndSaveExcel(file);
        List<Person> data = personRepository.findAll();
        model.addAttribute("message", message);
        model.addAttribute("data", data);
        return "upload";
    }
}
