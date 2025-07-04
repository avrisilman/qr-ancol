package com.qr.ancol.controller;

import com.qr.ancol.service.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class QrController {

    @Autowired
    QrService qrService;

    @GetMapping("/scan-qr")
    public String showForm() {
        return "qr-view";
    }

    @PostMapping("/scan-qr")
    public String handleSubmit(@RequestParam("id") Long noId,
                               @RequestParam("type") Long type,
                               Model model) {
        model.addAttribute("id", noId);
        model.addAttribute("type", type);
        qrService.postTiketByDate(noId, type, model);
        return "qr-view";
    }

    @PostMapping("/scan-qr-ajax")
    @ResponseBody
    public Map<String, String> handleAjax(@RequestParam("id") Long id,
                                          @RequestParam("type") Long type) {
        Map<String, String> response = new HashMap<>();
        String hasil = qrService.postTiketByDateAjax(id, type);
        response.put("hasil", hasil);
        return response;
    }
}
