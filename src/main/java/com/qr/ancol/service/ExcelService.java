package com.qr.ancol.service;

import com.qr.ancol.entity.Person;
import com.qr.ancol.repository.PersonRepository;
import com.qr.ancol.util.FileIndexTracker;
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

    private static final List<String> FILE_EXTENSIONS = Arrays.asList(".pdf", ".png", ".jpg", ".jpeg");

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmailService emailService;

    public String readAndSaveExcel(MultipartFile file) {
        int saved = 0;
        int skipped = 0;
        int sent = 0;

        int currentAncolIndex = FileIndexTracker.currentAncolIndex;
        int currentMobilIndex = FileIndexTracker.currentMobilIndex;
        int currentMotorIndex = FileIndexTracker.currentMotorIndex;

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
                    System.err.println("\u26A0\uFE0F Gagal parsing tanggalJoin: " + tanggalJoin);
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
                    File f = findFileWithExtensions("qr-tiket/in/ancol_" + currentAncolIndex);
                    if (f != null) ancolFiles.add(f);
                    else System.err.println("\u274C File Ancol tidak ditemukan untuk index: " + currentAncolIndex);
                    currentAncolIndex++;
                }

                for (int i = 0; i < jumlahMobil; i++) {
                    File f = findFileWithExtensions("qr-mobil/in/mobil_" + currentMobilIndex);
                    if (f != null) mobilFiles.add(f);
                    else System.err.println("\u274C File Mobil tidak ditemukan untuk index: " + currentMobilIndex);
                    currentMobilIndex++;
                }

                for (int i = 0; i < jumlahMotor; i++) {
                    File f = findFileWithExtensions("qr-motor/in/motor_" + currentMotorIndex);
                    if (f != null) motorFiles.add(f);
                    else System.err.println("\u274C File Motor tidak ditemukan untuk index: " + currentMotorIndex);
                    currentMotorIndex++;
                }

                try {
                    File qrFile = QrCodeGenerator.generateQrCode(String.valueOf(savedPerson.getId()), nik);
                    if (email != null && !email.trim().isEmpty()) {
                        emailService.sendEmailWithTemplateAndQrAndAttachment(
                                savedPerson.getId(), email, name, nik, division,
                                qrFile, ancolFiles, mobilFiles, motorFiles
                        );
                        sent++;

                        moveFiles(ancolFiles, "qr-tiket/out/");
                        moveFiles(mobilFiles, "qr-mobil/out/");
                        moveFiles(motorFiles, "qr-motor/out/");
                    } else {
                        System.err.println("\u26A0\uFE0F Email kosong untuk NIK: " + nik);
                    }
                } catch (Exception e) {
                    System.err.println("\u274C Gagal kirim email ke: " + email + " untuk NIK: " + nik);
                    e.printStackTrace();
                }
            }

            FileIndexTracker.currentAncolIndex = currentAncolIndex;
            FileIndexTracker.currentMobilIndex = currentMobilIndex;
            FileIndexTracker.currentMotorIndex = currentMotorIndex;

        } catch (Exception e) {
            e.printStackTrace();
            return "\u274C Gagal proses file: " + e.getMessage();
        }

        return "\u2705 Berhasil menyimpan " + saved + " data, kirim email " + sent + ", dilewati " + skipped + " data.";
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private File findFileWithExtensions(String basePath) {
        for (String ext : FILE_EXTENSIONS) {
            File file = new File(basePath + ext);
            if (file.exists()) return file;
        }
        return null;
    }

    private void moveFiles(List<File> files, String outDirPath) {
        File outDir = new File(outDirPath);
        if (!outDir.exists()) outDir.mkdirs();
        for (File file : files) {
            File movedFile = new File(outDir, file.getName());
            if (!file.renameTo(movedFile)) {
                System.err.println("\u2757 Gagal memindahkan file: " + file.getAbsolutePath());
            }
        }
    }
}