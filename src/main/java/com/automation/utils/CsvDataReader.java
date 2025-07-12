package com.automation.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Utility class for reading CSV test data files.
 * Provides methods to read CSV data in various formats.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class CsvDataReader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvDataReader.class);
    
    /**
     * Private constructor to prevent instantiation.
     */
    private CsvDataReader() {
        // Utility class
    }
    
    /**
     * Reads CSV file and returns all data as a list of string arrays.
     * 
     * @param filePath the path to the CSV file
     * @return List of string arrays representing rows
     */
    public static List<String[]> readCsvFile(final String filePath) {
        try (FileReader fileReader = new FileReader(filePath);
             CSVReader csvReader = new CSVReader(fileReader)) {
            
            List<String[]> data = csvReader.readAll();
            LOGGER.debug("Successfully read {} rows from CSV file: {}", data.size(), filePath);
            return data;
            
        } catch (IOException | CsvException e) {
            LOGGER.error("Error reading CSV file: {}", filePath, e);
            throw new RuntimeException("Error reading CSV file: " + filePath, e);
        }
    }
    
    /**
     * Reads CSV resource and returns all data as a list of string arrays.
     * 
     * @param resourcePath the path to the CSV resource
     * @return List of string arrays representing rows
     */
    public static List<String[]> readCsvResource(final String resourcePath) {
        try (InputStream inputStream = CsvDataReader.class.getClassLoader().getResourceAsStream(resourcePath);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(inputStreamReader)) {
            
            if (inputStream == null) {
                LOGGER.error("CSV resource not found: {}", resourcePath);
                throw new RuntimeException("CSV resource not found: " + resourcePath);
            }
            
            List<String[]> data = csvReader.readAll();
            LOGGER.debug("Successfully read {} rows from CSV resource: {}", data.size(), resourcePath);
            return data;
            
        } catch (IOException | CsvException e) {
            LOGGER.error("Error reading CSV resource: {}", resourcePath, e);
            throw new RuntimeException("Error reading CSV resource: " + resourcePath, e);
        }
    }
    
    /**
     * Reads CSV file and returns data as a list of maps with headers as keys.
     * 
     * @param filePath the path to the CSV file
     * @return List of maps representing rows with column headers as keys
     */
    public static List<Map<String, String>> readCsvFileWithHeaders(final String filePath) {
        List<String[]> rawData = readCsvFile(filePath);
        
        if (rawData.isEmpty()) {
            LOGGER.warn("CSV file is empty: {}", filePath);
            return new ArrayList<>();
        }
        
        String[] headers = rawData.get(0);
        List<Map<String, String>> data = new ArrayList<>();
        
        for (int i = 1; i < rawData.size(); i++) {
            String[] row = rawData.get(i);
            Map<String, String> rowMap = new HashMap<>();
            
            for (int j = 0; j < headers.length && j < row.length; j++) {
                rowMap.put(headers[j].trim(), row[j].trim());
            }
            
            data.add(rowMap);
        }
        
        LOGGER.debug("Successfully converted {} rows to maps from CSV file: {}", data.size(), filePath);
        return data;
    }
    
    /**
     * Reads CSV resource and returns data as a list of maps with headers as keys.
     * 
     * @param resourcePath the path to the CSV resource
     * @return List of maps representing rows with column headers as keys
     */
    public static List<Map<String, String>> readCsvResourceWithHeaders(final String resourcePath) {
        List<String[]> rawData = readCsvResource(resourcePath);
        
        if (rawData.isEmpty()) {
            LOGGER.warn("CSV resource is empty: {}", resourcePath);
            return new ArrayList<>();
        }
        
        String[] headers = rawData.get(0);
        List<Map<String, String>> data = new ArrayList<>();
        
        for (int i = 1; i < rawData.size(); i++) {
            String[] row = rawData.get(i);
            Map<String, String> rowMap = new HashMap<>();
            
            for (int j = 0; j < headers.length && j < row.length; j++) {
                rowMap.put(headers[j].trim(), row[j].trim());
            }
            
            data.add(rowMap);
        }
        
        LOGGER.debug("Successfully converted {} rows to maps from CSV resource: {}", data.size(), resourcePath);
        return data;
    }
    
    /**
     * Reads CSV file with custom separator.
     * 
     * @param filePath the path to the CSV file
     * @param separator the custom separator character
     * @return List of string arrays representing rows
     */
    public static List<String[]> readCsvFileWithSeparator(final String filePath, final char separator) {
        try (FileReader fileReader = new FileReader(filePath);
             CSVReader csvReader = new CSVReaderBuilder(fileReader)
                     .withCSVParser(new com.opencsv.CSVParserBuilder().withSeparator(separator).build())
                     .build()) {
            
            List<String[]> data = csvReader.readAll();
            LOGGER.debug("Successfully read {} rows from CSV file with separator '{}': {}", 
                data.size(), separator, filePath);
            return data;
            
        } catch (IOException | CsvException e) {
            LOGGER.error("Error reading CSV file with separator '{}': {}", separator, filePath, e);
            throw new RuntimeException("Error reading CSV file: " + filePath, e);
        }
    }
    
    /**
     * Gets a specific row from CSV file by row index.
     * 
     * @param filePath the path to the CSV file
     * @param rowIndex the index of the row (0-based)
     * @return String array representing the row
     */
    public static String[] getRowByIndex(final String filePath, final int rowIndex) {
        List<String[]> data = readCsvFile(filePath);
        
        if (rowIndex < 0 || rowIndex >= data.size()) {
            LOGGER.error("Row index {} is out of bounds for CSV file: {}", rowIndex, filePath);
            throw new IndexOutOfBoundsException("Row index " + rowIndex + " is out of bounds");
        }
        
        String[] row = data.get(rowIndex);
        LOGGER.debug("Retrieved row {} from CSV file: {}", rowIndex, filePath);
        return row;
    }
    
    /**
     * Gets a specific value from CSV file by row and column index.
     * 
     * @param filePath the path to the CSV file
     * @param rowIndex the index of the row (0-based)
     * @param columnIndex the index of the column (0-based)
     * @return the cell value as String
     */
    public static String getCellValue(final String filePath, final int rowIndex, final int columnIndex) {
        String[] row = getRowByIndex(filePath, rowIndex);
        
        if (columnIndex < 0 || columnIndex >= row.length) {
            LOGGER.error("Column index {} is out of bounds for row {} in CSV file: {}", 
                columnIndex, rowIndex, filePath);
            throw new IndexOutOfBoundsException("Column index " + columnIndex + " is out of bounds");
        }
        
        String value = row[columnIndex].trim();
        LOGGER.debug("Retrieved cell value '{}' at row {}, column {} from CSV file: {}", 
            value, rowIndex, columnIndex, filePath);
        return value;
    }
    
    /**
     * Gets a specific value from CSV file by row index and column header.
     * 
     * @param filePath the path to the CSV file
     * @param rowIndex the index of the data row (0-based, excluding header)
     * @param columnHeader the header name of the column
     * @return the cell value as String
     */
    public static String getCellValueByHeader(final String filePath, final int rowIndex, final String columnHeader) {
        List<Map<String, String>> data = readCsvFileWithHeaders(filePath);
        
        if (rowIndex < 0 || rowIndex >= data.size()) {
            LOGGER.error("Row index {} is out of bounds for CSV file: {}", rowIndex, filePath);
            throw new IndexOutOfBoundsException("Row index " + rowIndex + " is out of bounds");
        }
        
        Map<String, String> row = data.get(rowIndex);
        String value = row.get(columnHeader);
        
        if (value == null) {
            LOGGER.error("Column header '{}' not found in CSV file: {}", columnHeader, filePath);
            throw new IllegalArgumentException("Column header '" + columnHeader + "' not found");
        }
        
        LOGGER.debug("Retrieved cell value '{}' for header '{}' at row {} from CSV file: {}", 
            value, columnHeader, rowIndex, filePath);
        return value;
    }
    
    /**
     * Filters CSV data by column value.
     * 
     * @param filePath the path to the CSV file
     * @param columnHeader the header name of the column to filter by
     * @param filterValue the value to filter by
     * @return List of maps representing filtered rows
     */
    public static List<Map<String, String>> filterByColumnValue(final String filePath, 
                                                               final String columnHeader, 
                                                               final String filterValue) {
        List<Map<String, String>> allData = readCsvFileWithHeaders(filePath);
        List<Map<String, String>> filteredData = new ArrayList<>();
        
        for (Map<String, String> row : allData) {
            String cellValue = row.get(columnHeader);
            if (cellValue != null && cellValue.equals(filterValue)) {
                filteredData.add(row);
            }
        }
        
        LOGGER.debug("Filtered {} rows by column '{}' with value '{}' from CSV file: {}", 
            filteredData.size(), columnHeader, filterValue, filePath);
        return filteredData;
    }
    
    /**
     * Gets the number of rows in CSV file.
     * 
     * @param filePath the path to the CSV file
     * @return the number of rows
     */
    public static int getRowCount(final String filePath) {
        List<String[]> data = readCsvFile(filePath);
        int rowCount = data.size();
        LOGGER.debug("CSV file has {} rows: {}", rowCount, filePath);
        return rowCount;
    }
    
    /**
     * Gets the number of columns in CSV file.
     * 
     * @param filePath the path to the CSV file
     * @return the number of columns
     */
    public static int getColumnCount(final String filePath) {
        List<String[]> data = readCsvFile(filePath);
        
        if (data.isEmpty()) {
            LOGGER.warn("CSV file is empty: {}", filePath);
            return 0;
        }
        
        int columnCount = data.get(0).length;
        LOGGER.debug("CSV file has {} columns: {}", columnCount, filePath);
        return columnCount;
    }
    
    /**
     * Writes data to a CSV file.
     * 
     * @param data the data to write as list of string arrays
     * @param filePath the path to the output CSV file
     */
    public static void writeCsvFile(final List<String[]> data, final String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            
            csvWriter.writeAll(data);
            LOGGER.debug("Successfully wrote {} rows to CSV file: {}", data.size(), filePath);
            
        } catch (IOException e) {
            LOGGER.error("Error writing CSV file: {}", filePath, e);
            throw new RuntimeException("Error writing CSV file: " + filePath, e);
        }
    }
    
    /**
     * Converts list of maps to CSV format and writes to file.
     * 
     * @param data the data to write as list of maps
     * @param filePath the path to the output CSV file
     */
    public static void writeCsvFileFromMaps(final List<Map<String, String>> data, final String filePath) {
        if (data.isEmpty()) {
            LOGGER.warn("No data to write to CSV file: {}", filePath);
            return;
        }
        
        // Get headers from the first map
        Set<String> headerSet = data.get(0).keySet();
        String[] headers = headerSet.toArray(new String[0]);
        
        List<String[]> csvData = new ArrayList<>();
        csvData.add(headers);
        
        for (Map<String, String> row : data) {
            String[] rowArray = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                rowArray[i] = row.get(headers[i]);
            }
            csvData.add(rowArray);
        }
        
        writeCsvFile(csvData, filePath);
    }
}
