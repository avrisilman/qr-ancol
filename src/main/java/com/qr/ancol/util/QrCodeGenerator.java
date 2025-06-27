package com.qr.ancol.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class QrCodeGenerator {

    public static File generateQrCode(String text, String fileName) throws WriterException, IOException {
        // Folder tujuan
        String directoryPath = "D:/project/qr-generate";

        // Pastikan folder tujuan sudah ada
        Files.createDirectories(Path.of(directoryPath));

        // Path lengkap untuk file PNG
        Path filePath = Path.of(directoryPath, fileName + ".png");

        // Generate QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);

        // Simpan sebagai file PNG
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);

        return filePath.toFile();
    }
}