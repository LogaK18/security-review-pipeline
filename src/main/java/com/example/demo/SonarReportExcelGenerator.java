package com.example.demo;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SonarReportExcelGenerator {

    private static final String SONAR_URL =
        "http://localhost:9000/api/issues/search?componentKeys=demo&types=BUG,VULNERABILITY,CODE_SMELL";
    private static final String TOKEN = "squ_cc65d8552f187448d4408411b7665a0052b3e81f"; // replace with your SonarQube token

    public static void main(String[] args) throws Exception {
        // Fetch JSON from SonarQube
        HttpURLConnection conn = (HttpURLConnection) new URL(SONAR_URL).openConnection();
        conn.setRequestProperty("Authorization", "Basic " +
            Base64.getEncoder().encodeToString((TOKEN + ":").getBytes()));
        InputStream in = conn.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(in);

        // Create Excel workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("SonarQube Issues");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Create header row
        Row header = sheet.createRow(0);
        String[] columns = {"Type", "Severity", "Message", "File", "Line", "Key"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Fill data rows
        int rowNum = 1;
        for (JsonNode issue : root.get("issues")) {
            Row row = sheet.createRow(rowNum++);

            String type = issue.get("type").asText();
            String severity = issue.get("severity").asText();
            String message = issue.get("message").asText();
            String file = issue.get("component").asText();
            String key = issue.get("key").asText();

            // Line number (may be null)
            String line = "N/A";
            JsonNode lineNode = issue.get("line");
            if (lineNode != null && !lineNode.isNull()) {
                line = String.valueOf(lineNode.asInt());
            }

            row.createCell(0).setCellValue(type);
            row.createCell(1).setCellValue(severity);
            row.createCell(2).setCellValue(message);
            row.createCell(3).setCellValue(file);
            row.createCell(4).setCellValue(line);
            row.createCell(5).setCellValue(key);

            // Conditional formatting by severity
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            if ("MAJOR".equalsIgnoreCase(severity)) {
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                font.setColor(IndexedColors.WHITE.getIndex());
            } else if ("MINOR".equalsIgnoreCase(severity)) {
                style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                font.setColor(IndexedColors.BLACK.getIndex());
            } else if ("INFO".equalsIgnoreCase(severity)) {
                style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                font.setColor(IndexedColors.BLACK.getIndex());
            }
            style.setFont(font);

            // Apply style to entire row
            for (int i = 0; i < columns.length; i++) {
                row.getCell(i).setCellStyle(style);
            }
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save Excel file
        FileOutputStream fileOut = new FileOutputStream("sonar_issues.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

        System.out.println("✅ Excel generated: sonar_issues.xlsx");
        System.out.println("🔗 Dashboard: http://localhost:9000/dashboard?id=demo");
    }
}
