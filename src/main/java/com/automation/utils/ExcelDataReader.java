package com.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Utility class for reading Excel test data files.
 * Supports both .xls and .xlsx formats.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class ExcelDataReader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataReader.class);
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ExcelDataReader() {
        // Utility class
    }
    
    /**
     * Creates a workbook from file path.
     * 
     * @param filePath the path to the Excel file
     * @return Workbook instance
     */
    private static Workbook createWorkbook(final String filePath) {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            
            if (filePath.endsWith(".xlsx")) {
                return new XSSFWorkbook(fileInputStream);
            } else if (filePath.endsWith(".xls")) {
                return new HSSFWorkbook(fileInputStream);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Only .xls and .xlsx are supported.");
            }
            
        } catch (IOException e) {
            LOGGER.error("Error creating workbook from file: {}", filePath, e);
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }
    }
    
    /**
     * Creates a workbook from resource path.
     * 
     * @param resourcePath the path to the Excel resource
     * @return Workbook instance
     */
    private static Workbook createWorkbookFromResource(final String resourcePath) {
        try (InputStream inputStream = ExcelDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            
            if (inputStream == null) {
                LOGGER.error("Excel resource not found: {}", resourcePath);
                throw new RuntimeException("Excel resource not found: " + resourcePath);
            }
            
            if (resourcePath.endsWith(".xlsx")) {
                return new XSSFWorkbook(inputStream);
            } else if (resourcePath.endsWith(".xls")) {
                return new HSSFWorkbook(inputStream);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Only .xls and .xlsx are supported.");
            }
            
        } catch (IOException e) {
            LOGGER.error("Error creating workbook from resource: {}", resourcePath, e);
            throw new RuntimeException("Error reading Excel resource: " + resourcePath, e);
        }
    }
    
    /**
     * Reads all data from a specific sheet.
     * 
     * @param filePath the path to the Excel file
     * @param sheetName the name of the sheet
     * @return List of string arrays representing rows
     */
    public static List<String[]> readSheet(final String filePath, final String sheetName) {
        try (Workbook workbook = createWorkbook(filePath)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                LOGGER.error("Sheet '{}' not found in Excel file: {}", sheetName, filePath);
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file: " + filePath);
            }
            
            List<String[]> data = new ArrayList<>();
            
            for (Row row : sheet) {
                String[] rowData = new String[row.getLastCellNum()];
                
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    rowData[i] = getCellValueAsString(cell);
                }
                
                data.add(rowData);
            }
            
            LOGGER.debug("Successfully read {} rows from sheet '{}' in Excel file: {}", 
                data.size(), sheetName, filePath);
            return data;
            
        } catch (Exception e) {
            LOGGER.error("Error reading sheet '{}' from Excel file: {}", sheetName, filePath, e);
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }
    }
    
    /**
     * Reads all data from a specific sheet by index.
     * 
     * @param filePath the path to the Excel file
     * @param sheetIndex the index of the sheet (0-based)
     * @return List of string arrays representing rows
     */
    public static List<String[]> readSheet(final String filePath, final int sheetIndex) {
        try (Workbook workbook = createWorkbook(filePath)) {
            
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                LOGGER.error("Sheet index {} is out of bounds in Excel file: {}", sheetIndex, filePath);
                throw new IndexOutOfBoundsException("Sheet index " + sheetIndex + " is out of bounds");
            }
            
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            List<String[]> data = new ArrayList<>();
            
            for (Row row : sheet) {
                String[] rowData = new String[row.getLastCellNum()];
                
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i);
                    rowData[i] = getCellValueAsString(cell);
                }
                
                data.add(rowData);
            }
            
            LOGGER.debug("Successfully read {} rows from sheet index {} in Excel file: {}", 
                data.size(), sheetIndex, filePath);
            return data;
            
        } catch (Exception e) {
            LOGGER.error("Error reading sheet index {} from Excel file: {}", sheetIndex, filePath, e);
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }
    }
    
    /**
     * Reads sheet data and returns as list of maps with headers as keys.
     * 
     * @param filePath the path to the Excel file
     * @param sheetName the name of the sheet
     * @return List of maps representing rows with column headers as keys
     */
    public static List<Map<String, String>> readSheetWithHeaders(final String filePath, final String sheetName) {
        List<String[]> rawData = readSheet(filePath, sheetName);
        
        if (rawData.isEmpty()) {
            LOGGER.warn("Excel sheet is empty: {} in file: {}", sheetName, filePath);
            return new ArrayList<>();
        }
        
        String[] headers = rawData.get(0);
        List<Map<String, String>> data = new ArrayList<>();
        
        for (int i = 1; i < rawData.size(); i++) {
            String[] row = rawData.get(i);
            Map<String, String> rowMap = new HashMap<>();
            
            for (int j = 0; j < headers.length && j < row.length; j++) {
                String header = headers[j] != null ? headers[j].trim() : "";
                String value = row[j] != null ? row[j].trim() : "";
                rowMap.put(header, value);
            }
            
            data.add(rowMap);
        }
        
        LOGGER.debug("Successfully converted {} rows to maps from Excel sheet '{}': {}", 
            data.size(), sheetName, filePath);
        return data;
    }
    
    /**
     * Gets a specific cell value from Excel file.
     * 
     * @param filePath the path to the Excel file
     * @param sheetName the name of the sheet
     * @param rowIndex the index of the row (0-based)
     * @param columnIndex the index of the column (0-based)
     * @return the cell value as String
     */
    public static String getCellValue(final String filePath, final String sheetName, 
                                     final int rowIndex, final int columnIndex) {
        try (Workbook workbook = createWorkbook(filePath)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                LOGGER.error("Sheet '{}' not found in Excel file: {}", sheetName, filePath);
                throw new RuntimeException("Sheet '" + sheetName + "' not found");
            }
            
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                LOGGER.warn("Row {} is empty in sheet '{}': {}", rowIndex, sheetName, filePath);
                return "";
            }
            
            Cell cell = row.getCell(columnIndex);
            String value = getCellValueAsString(cell);
            
            LOGGER.debug("Retrieved cell value '{}' at row {}, column {} from sheet '{}': {}", 
                value, rowIndex, columnIndex, sheetName, filePath);
            return value;
            
        } catch (Exception e) {
            LOGGER.error("Error getting cell value at row {}, column {} from sheet '{}': {}", 
                rowIndex, columnIndex, sheetName, filePath, e);
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }
    }
    
    /**
     * Gets a specific cell value by column header.
     * 
     * @param filePath the path to the Excel file
     * @param sheetName the name of the sheet
     * @param rowIndex the index of the data row (0-based, excluding header)
     * @param columnHeader the header name of the column
     * @return the cell value as String
     */
    public static String getCellValueByHeader(final String filePath, final String sheetName, 
                                             final int rowIndex, final String columnHeader) {
        List<Map<String, String>> data = readSheetWithHeaders(filePath, sheetName);
        
        if (rowIndex < 0 || rowIndex >= data.size()) {
            LOGGER.error("Row index {} is out of bounds for sheet '{}': {}", rowIndex, sheetName, filePath);
            throw new IndexOutOfBoundsException("Row index " + rowIndex + " is out of bounds");
        }
        
        Map<String, String> row = data.get(rowIndex);
        String value = row.get(columnHeader);
        
        if (value == null) {
            LOGGER.error("Column header '{}' not found in sheet '{}': {}", columnHeader, sheetName, filePath);
            throw new IllegalArgumentException("Column header '" + columnHeader + "' not found");
        }
        
        LOGGER.debug("Retrieved cell value '{}' for header '{}' at row {} from sheet '{}': {}", 
            value, columnHeader, rowIndex, sheetName, filePath);
        return value;
    }
    
    /**
     * Gets all sheet names from Excel file.
     * 
     * @param filePath the path to the Excel file
     * @return List of sheet names
     */
    public static List<String> getSheetNames(final String filePath) {
        try (Workbook workbook = createWorkbook(filePath)) {
            
            List<String> sheetNames = new ArrayList<>();
            int numberOfSheets = workbook.getNumberOfSheets();
            
            for (int i = 0; i < numberOfSheets; i++) {
                sheetNames.add(workbook.getSheetName(i));
            }
            
            LOGGER.debug("Found {} sheets in Excel file: {}", sheetNames.size(), filePath);
            return sheetNames;
            
        } catch (Exception e) {
            LOGGER.error("Error getting sheet names from Excel file: {}", filePath, e);
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }
    }
    
    /**
     * Gets the number of rows in a specific sheet.
     * 
     * @param filePath the path to the Excel file
     * @param sheetName the name of the sheet
     * @return the number of rows
     */
    public static int getRowCount(final String filePath, final String sheetName) {
        try (Workbook workbook = createWorkbook(filePath)) {
            
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                LOGGER.error("Sheet '{}' not found in Excel file: {}", sheetName, filePath);
                throw new RuntimeException("Sheet '" + sheetName + "' not found");
            }
            
            int rowCount = sheet.getLastRowNum() + 1;
            LOGGER.debug("Sheet '{}' has {} rows in Excel file: {}", sheetName, rowCount, filePath);
            return rowCount;
            
        } catch (Exception e) {
            LOGGER.error("Error getting row count from sheet '{}': {}", sheetName, filePath, e);
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }
    }
    
    /**
     * Converts cell value to string based on cell type.
     * 
     * @param cell the cell to convert
     * @return the cell value as String
     */
    private static String getCellValueAsString(final Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
