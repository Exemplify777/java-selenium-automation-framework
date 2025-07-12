package com.automation.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading JSON test data files.
 * Provides methods to read JSON data in various formats.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class JsonDataReader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonDataReader.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /**
     * Private constructor to prevent instantiation.
     */
    private JsonDataReader() {
        // Utility class
    }
    
    /**
     * Reads JSON data from a file and returns as JsonNode.
     * 
     * @param filePath the path to the JSON file
     * @return JsonNode containing the data
     */
    public static JsonNode readJsonFile(final String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.error("JSON file not found: {}", filePath);
                throw new RuntimeException("JSON file not found: " + filePath);
            }
            
            JsonNode jsonNode = OBJECT_MAPPER.readTree(file);
            LOGGER.debug("Successfully read JSON file: {}", filePath);
            return jsonNode;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON file: {}", filePath, e);
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }
    
    /**
     * Reads JSON data from classpath resource and returns as JsonNode.
     * 
     * @param resourcePath the path to the JSON resource
     * @return JsonNode containing the data
     */
    public static JsonNode readJsonResource(final String resourcePath) {
        try (InputStream inputStream = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                LOGGER.error("JSON resource not found: {}", resourcePath);
                throw new RuntimeException("JSON resource not found: " + resourcePath);
            }
            
            JsonNode jsonNode = OBJECT_MAPPER.readTree(inputStream);
            LOGGER.debug("Successfully read JSON resource: {}", resourcePath);
            return jsonNode;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON resource: {}", resourcePath, e);
            throw new RuntimeException("Error reading JSON resource: " + resourcePath, e);
        }
    }
    
    /**
     * Reads JSON data and converts to a specific class type.
     * 
     * @param filePath the path to the JSON file
     * @param clazz the class type to convert to
     * @param <T> the type parameter
     * @return object of the specified type
     */
    public static <T> T readJsonFile(final String filePath, final Class<T> clazz) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.error("JSON file not found: {}", filePath);
                throw new RuntimeException("JSON file not found: " + filePath);
            }
            
            T object = OBJECT_MAPPER.readValue(file, clazz);
            LOGGER.debug("Successfully read JSON file to {}: {}", clazz.getSimpleName(), filePath);
            return object;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON file to {}: {}", clazz.getSimpleName(), filePath, e);
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }
    
    /**
     * Reads JSON resource and converts to a specific class type.
     * 
     * @param resourcePath the path to the JSON resource
     * @param clazz the class type to convert to
     * @param <T> the type parameter
     * @return object of the specified type
     */
    public static <T> T readJsonResource(final String resourcePath, final Class<T> clazz) {
        try (InputStream inputStream = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                LOGGER.error("JSON resource not found: {}", resourcePath);
                throw new RuntimeException("JSON resource not found: " + resourcePath);
            }
            
            T object = OBJECT_MAPPER.readValue(inputStream, clazz);
            LOGGER.debug("Successfully read JSON resource to {}: {}", clazz.getSimpleName(), resourcePath);
            return object;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON resource to {}: {}", clazz.getSimpleName(), resourcePath, e);
            throw new RuntimeException("Error reading JSON resource: " + resourcePath, e);
        }
    }
    
    /**
     * Reads JSON data and converts to a List of objects.
     * 
     * @param filePath the path to the JSON file
     * @param clazz the class type of list elements
     * @param <T> the type parameter
     * @return List of objects of the specified type
     */
    public static <T> List<T> readJsonFileToList(final String filePath, final Class<T> clazz) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.error("JSON file not found: {}", filePath);
                throw new RuntimeException("JSON file not found: " + filePath);
            }
            
            TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {};
            List<T> list = OBJECT_MAPPER.readValue(file, typeReference);
            LOGGER.debug("Successfully read JSON file to List<{}>: {}", clazz.getSimpleName(), filePath);
            return list;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON file to List<{}>: {}", clazz.getSimpleName(), filePath, e);
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }
    
    /**
     * Reads JSON resource and converts to a List of objects.
     * 
     * @param resourcePath the path to the JSON resource
     * @param clazz the class type of list elements
     * @param <T> the type parameter
     * @return List of objects of the specified type
     */
    public static <T> List<T> readJsonResourceToList(final String resourcePath, final Class<T> clazz) {
        try (InputStream inputStream = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                LOGGER.error("JSON resource not found: {}", resourcePath);
                throw new RuntimeException("JSON resource not found: " + resourcePath);
            }
            
            TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {};
            List<T> list = OBJECT_MAPPER.readValue(inputStream, typeReference);
            LOGGER.debug("Successfully read JSON resource to List<{}>: {}", clazz.getSimpleName(), resourcePath);
            return list;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON resource to List<{}>: {}", clazz.getSimpleName(), resourcePath, e);
            throw new RuntimeException("Error reading JSON resource: " + resourcePath, e);
        }
    }
    
    /**
     * Reads JSON data and converts to a Map.
     * 
     * @param filePath the path to the JSON file
     * @return Map containing the JSON data
     */
    public static Map<String, Object> readJsonFileToMap(final String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                LOGGER.error("JSON file not found: {}", filePath);
                throw new RuntimeException("JSON file not found: " + filePath);
            }
            
            TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> map = OBJECT_MAPPER.readValue(file, typeReference);
            LOGGER.debug("Successfully read JSON file to Map: {}", filePath);
            return map;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON file to Map: {}", filePath, e);
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }
    
    /**
     * Reads JSON resource and converts to a Map.
     * 
     * @param resourcePath the path to the JSON resource
     * @return Map containing the JSON data
     */
    public static Map<String, Object> readJsonResourceToMap(final String resourcePath) {
        try (InputStream inputStream = JsonDataReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                LOGGER.error("JSON resource not found: {}", resourcePath);
                throw new RuntimeException("JSON resource not found: " + resourcePath);
            }
            
            TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> map = OBJECT_MAPPER.readValue(inputStream, typeReference);
            LOGGER.debug("Successfully read JSON resource to Map: {}", resourcePath);
            return map;
            
        } catch (IOException e) {
            LOGGER.error("Error reading JSON resource to Map: {}", resourcePath, e);
            throw new RuntimeException("Error reading JSON resource: " + resourcePath, e);
        }
    }
    
    /**
     * Gets a specific value from JSON by key path.
     * 
     * @param filePath the path to the JSON file
     * @param keyPath the dot-separated key path (e.g., "user.credentials.username")
     * @return the value as String
     */
    public static String getValueByKeyPath(final String filePath, final String keyPath) {
        JsonNode rootNode = readJsonFile(filePath);
        JsonNode valueNode = rootNode;
        
        String[] keys = keyPath.split("\\.");
        for (String key : keys) {
            valueNode = valueNode.get(key);
            if (valueNode == null) {
                LOGGER.warn("Key path not found: {} in file: {}", keyPath, filePath);
                return null;
            }
        }
        
        String value = valueNode.asText();
        LOGGER.debug("Retrieved value '{}' for key path '{}' from file: {}", value, keyPath, filePath);
        return value;
    }
    
    /**
     * Gets a specific value from JSON resource by key path.
     * 
     * @param resourcePath the path to the JSON resource
     * @param keyPath the dot-separated key path (e.g., "user.credentials.username")
     * @return the value as String
     */
    public static String getValueByKeyPathFromResource(final String resourcePath, final String keyPath) {
        JsonNode rootNode = readJsonResource(resourcePath);
        JsonNode valueNode = rootNode;
        
        String[] keys = keyPath.split("\\.");
        for (String key : keys) {
            valueNode = valueNode.get(key);
            if (valueNode == null) {
                LOGGER.warn("Key path not found: {} in resource: {}", keyPath, resourcePath);
                return null;
            }
        }
        
        String value = valueNode.asText();
        LOGGER.debug("Retrieved value '{}' for key path '{}' from resource: {}", value, keyPath, resourcePath);
        return value;
    }
    
    /**
     * Writes an object to a JSON file.
     * 
     * @param object the object to write
     * @param filePath the path to the output JSON file
     */
    public static void writeJsonFile(final Object object, final String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, object);
            LOGGER.debug("Successfully wrote object to JSON file: {}", filePath);
            
        } catch (IOException e) {
            LOGGER.error("Error writing object to JSON file: {}", filePath, e);
            throw new RuntimeException("Error writing JSON file: " + filePath, e);
        }
    }
}
