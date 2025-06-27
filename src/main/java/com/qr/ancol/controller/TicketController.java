package com.qr.ancol.controller;

import com.qr.ancol.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TicketController {

    @Autowired
    PersonService personService;

    @GetMapping("/employee")
    public String confirmPage(@RequestParam("nik") String nik, Model model) {
        personService.processConfirmation(nik, model);
        return "ticket-success";
    }


}
