package com.qr.ancol.service;

import com.qr.ancol.entity.Person;
import com.qr.ancol.repository.PersonRepository;
import com.qr.ancol.util.QrCodeGenerator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmailService emailService;

    public String readAndSaveExcel(MultipartFile file) {
        int saved = 0;
        int skipped = 0;
        int sent = 0;

        int currentAncolIndex = 1;
        int currentMobilIndex = 1;
        int currentMotorIndex = 1;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            DataFormatter formatter = new DataFormatter();

            boolean headerSkipped = false;
            while (rows.hasNext()) {
                Row row = rows.next();
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String nik = formatter.formatCellValue(row.getCell(0));
                String name = formatter.formatCellValue(row.getCell(1));
                String area = formatter.formatCellValue(row.getCell(2));
                String division = formatter.formatCellValue(row.getCell(3));
                String status = formatter.formatCellValue(row.getCell(4));
                String tanggalJoin = formatter.formatCellValue(row.getCell(5));
                String souvenir = formatter.formatCellValue(row.getCell(7));
                String ancol = formatter.formatCellValue(row.getCell(8));
                String dufan = formatter.formatCellValue(row.getCell(9));
                String makan = formatter.formatCellValue(row.getCell(10));
                String snack = formatter.formatCellValue(row.getCell(11));
                String mobil = formatter.formatCellValue(row.getCell(12));
                String motor = formatter.formatCellValue(row.getCell(13));
                String email = formatter.formatCellValue(row.getCell(14));

                String lamaKerja;
                try {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate joinDate = LocalDate.parse(tanggalJoin, dateFormatter);
                    Period period = Period.between(joinDate, LocalDate.now());
                    double totalYears = period.getYears() + (period.getMonths() / 12.0);
                    lamaKerja = String.format(Locale.US, "%.1f", totalYears);
                } catch (Exception ex) {
                    System.err.println("⚠️ Gagal parsing tanggalJoin: " + tanggalJoin);
                    lamaKerja = "0";
                }

                if (nik == null || nik.trim().isEmpty() || personRepository.findByNik(nik).isPresent()) {
                    skipped++;
                    continue;
                }

                Person person = new Person();
                person.setNik(nik);
                person.setName(name);
                person.setArea(area);
                person.setDivision(division);
                person.setStatus(status);
                person.setTanggalJoin(tanggalJoin);
                person.setLamaKerja(lamaKerja);
                person.setTotalSouvenir(souvenir);
                person.setTotalTicketAncol(ancol);
                person.setTotalTicketDufan(dufan);
                person.setTotalSnack(snack);
                person.setTotalMakan(makan);
                person.setMobil(mobil);
                person.setMotor(motor);
                person.setEmail(email);
                person.setCreatedDate(new Date());

                Person savedPerson = personRepository.save(person);
                saved++;

                int jumlahAncol = parseIntSafe(ancol);
                int jumlahMobil = parseIntSafe(mobil);
                int jumlahMotor = parseIntSafe(motor);

                List<File> ancolFiles = new ArrayList<>();
                List<File> mobilFiles = new ArrayList<>();
                List<File> motorFiles = new ArrayList<>();

                for (int i = 0; i < jumlahAncol; i++) {
                    File f = new File("qr-tiket/in/ancol_" + currentAncolIndex + ".pdf");
                   // File f = new File("D:/project/qr-ancol/qr-tiket/in/ancol_" + currentAncolIndex + ".pdf");
                    if (f.exists()) ancolFiles.add(f);
                    else System.err.println("❌ File Ancol tidak ditemukan: " + f.getAbsolutePath());
                    currentAncolIndex++;
                }

                for (int i = 0; i < jumlahMobil; i++) {
                    File f = new File("qr-mobil/in/mobil_" + currentMobilIndex + ".png");
                    //  File f = new File("D:/project/qr-ancol/qr-mobil/in/mobil_" + currentMobilIndex + ".png");
                    if (f.exists()) mobilFiles.add(f);
                    else System.err.println("❌ File Mobil tidak ditemukan: " + f.getAbsolutePath());
                    currentMobilIndex++;
                }

                for (int i = 0; i < jumlahMotor; i++) {
                    File f = new File("qr-motor/in/motor_" + currentMotorIndex + ".png");
                    //File f = new File("/Users/admmtg/data/qr-ancol/qr-motor/in/motor_" + currentMotorIndex + ".png");
                   // File f = new File("D:/project/qr-ancol/qr-motor/in/motor_" + currentMotorIndex + ".png");
                    if (f.exists()) motorFiles.add(f);
                    else System.err.println("❌ File Motor tidak ditemukan: " + f.getAbsolutePath());
                    currentMotorIndex++;
                }

                try {
                    File qrFile = QrCodeGenerator.generateQrCode(String.valueOf(savedPerson.getId()), nik);
                    if (email != null && !email.trim().isEmpty()) {
                        emailService.sendEmailWithTemplateAndQrAndAttachment(savedPerson.getId(), email, name, nik, division, qrFile, ancolFiles, mobilFiles, motorFiles);
                        sent++;

                        ancolFiles.forEach(f -> moveFileToOut(f, "qr-tiket/out/"));
                        mobilFiles.forEach(f -> moveFileToOut(f, "qr-mobil/out/"));
                        motorFiles.forEach(f -> moveFileToOut(f, "qr-motor/out/"));
//                        ancolFiles.forEach(f -> moveFileToOut(f, "D:/project/qr-ancol/qr-tiket/out/"));
//                        mobilFiles.forEach(f -> moveFileToOut(f, "D:/project/qr-ancol/qr-mobil/out/"));
//                        motorFiles.forEach(f -> moveFileToOut(f, "D:/project/qr-ancol/qr-motor/out/"));
                    } else {
                        System.err.println("⚠️ Email kosong untuk NIK: " + nik);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Gagal kirim email ke: " + email + " untuk NIK: " + nik);
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Gagal proses file: " + e.getMessage();
        }

        return "✅ Berhasil menyimpan " + saved + " data, kirim email " + sent + ", dilewati " + skipped + " data.";
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void moveFileToOut(File file, String outDirPath) {
        File outDir = new File(outDirPath);
        if (!outDir.exists()) outDir.mkdirs();
        File movedFile = new File(outDir, file.getName());
        if (!file.renameTo(movedFile)) {
            System.err.println("❗ Gagal memindahkan file: " + file.getAbsolutePath());
        }
    }



}
