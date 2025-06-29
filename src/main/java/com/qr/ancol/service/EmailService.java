package com.qr.ancol.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailWithTemplateAndQrAndAttachment(
            Long id,
            String toEmail,
            String nama,
            String nik,
            String division,
            File qrFile,
            List<File> ancolFiles,
            List<File> mobilFiles,
            List<File> motorFiles
    ) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//        helper.setFrom("noreply@registrasievent.com");
//        helper.setTo(toEmail);
//        helper.setSubject("Anniversary 40th PT. KAO Indonesia");

        helper.setFrom("EVENT REGISTRASI <kao-event@registrasievent.com>");
        helper.setTo(toEmail);
        helper.setSubject("Anniversary 40th PT. KAO Indonesia");

        // ✅ Load template dari classpath
        Resource resource = new ClassPathResource("templates/emailTemplate.html");
        String htmlContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        htmlContent = htmlContent.replace("{{nama}}", nama);
        htmlContent = htmlContent.replace("{{id}}", id.toString());
        htmlContent = htmlContent.replace("{{nik}}", nik);
        htmlContent = htmlContent.replace("{{division}}", division);
        helper.setText(htmlContent, true);

        // ✅ Tambah QR inline
        if (qrFile != null && qrFile.exists()) {
            helper.addInline("qrImage", qrFile);
        }

        // ✅ Tambah attachment
        if (ancolFiles != null) {
            for (File file : ancolFiles) {
                if (file.exists()) {
                    helper.addAttachment(file.getName(), new FileSystemResource(file));
                }
            }
        }

        if (mobilFiles != null) {
            for (File file : mobilFiles) {
                if (file.exists()) {
                    helper.addAttachment(file.getName(), new FileSystemResource(file));
                }
            }
        }

        if (motorFiles != null) {
            for (File file : motorFiles) {
                if (file.exists()) {
                    helper.addAttachment(file.getName(), new FileSystemResource(file));
                }
            }
        }

        mailSender.send(message);
    }
}
