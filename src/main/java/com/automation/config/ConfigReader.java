package com.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration reader utility class for loading environment-specific properties.
 * This class implements the Singleton pattern to ensure only one instance exists.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class ConfigReader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private Properties properties;
    private String environment;
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigReader() {
        loadProperties();
    }
    
    /**
     * Gets the singleton instance of ConfigReader.
     * 
     * @return ConfigReader instance
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }
    
    /**
     * Loads properties from the appropriate environment configuration file.
     */
    private void loadProperties() {
        properties = new Properties();
        environment = System.getProperty("environment", "dev");
        
        String configFile = String.format("config/%s.properties", environment);
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                LOGGER.error("Configuration file not found: {}", configFile);
                throw new RuntimeException("Configuration file not found: " + configFile);
            }
            
            properties.load(inputStream);
            LOGGER.info("Successfully loaded configuration for environment: {}", environment);
            
        } catch (IOException e) {
            LOGGER.error("Error loading configuration file: {}", configFile, e);
            throw new RuntimeException("Error loading configuration file: " + configFile, e);
        }
    }
    
    /**
     * Gets a property value as String.
     * 
     * @param key the property key
     * @return the property value, or null if not found
     */
    public String getProperty(final String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            LOGGER.warn("Property not found: {}", key);
        }
        return value;
    }
    
    /**
     * Gets a property value as String with a default value.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if property is not found
     * @return the property value, or default value if not found
     */
    public String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Gets a property value as integer.
     * 
     * @param key the property key
     * @return the property value as integer
     * @throws NumberFormatException if the property value is not a valid integer
     */
    public int getIntProperty(final String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid integer value for property {}: {}", key, value);
            throw new RuntimeException("Invalid integer value for property " + key + ": " + value, e);
        }
    }
    
    /**
     * Gets a property value as integer with a default value.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if property is not found or invalid
     * @return the property value as integer, or default value
     */
    public int getIntProperty(final String key, final int defaultValue) {
        try {
            return getIntProperty(key);
        } catch (RuntimeException e) {
            LOGGER.warn("Using default value {} for property {}", defaultValue, key);
            return defaultValue;
        }
    }
    
    /**
     * Gets a property value as boolean.
     * 
     * @param key the property key
     * @return the property value as boolean
     */
    public boolean getBooleanProperty(final String key) {
        String value = getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Gets a property value as boolean with a default value.
     * 
     * @param key the property key
     * @param defaultValue the default value to return if property is not found
     * @return the property value as boolean, or default value
     */
    public boolean getBooleanProperty(final String key, final boolean defaultValue) {
        try {
            return getBooleanProperty(key);
        } catch (RuntimeException e) {
            LOGGER.warn("Using default value {} for property {}", defaultValue, key);
            return defaultValue;
        }
    }
    
    /**
     * Gets the current environment.
     * 
     * @return the current environment name
     */
    public String getEnvironment() {
        return environment;
    }
    
    /**
     * Gets the base URL for the application under test.
     * 
     * @return the base URL
     */
    public String getBaseUrl() {
        return getProperty("base.url");
    }
    
    /**
     * Gets the browser name.
     * 
     * @return the browser name
     */
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    /**
     * Checks if headless mode is enabled.
     * 
     * @return true if headless mode is enabled, false otherwise
     */
    public boolean isHeadless() {
        return getBooleanProperty("headless", false);
    }
    
    /**
     * Gets the implicit wait timeout.
     * 
     * @return the implicit wait timeout in seconds
     */
    public int getImplicitWait() {
        return getIntProperty("browser.implicit.wait", 10);
    }
    
    /**
     * Gets the page load timeout.
     * 
     * @return the page load timeout in seconds
     */
    public int getPageLoadTimeout() {
        return getIntProperty("browser.page.load.timeout", 30);
    }
    
    /**
     * Gets the script timeout.
     * 
     * @return the script timeout in seconds
     */
    public int getScriptTimeout() {
        return getIntProperty("browser.script.timeout", 30);
    }
    
    /**
     * Gets the explicit wait timeout.
     * 
     * @return the explicit wait timeout in seconds
     */
    public int getExplicitWaitTimeout() {
        return getIntProperty("explicit.wait.timeout", 15);
    }
    
    /**
     * Checks if screenshots should be taken on failure.
     * 
     * @return true if screenshots should be taken on failure, false otherwise
     */
    public boolean isScreenshotOnFailure() {
        return getBooleanProperty("screenshot.on.failure", true);
    }
    
    /**
     * Gets the reports path.
     * 
     * @return the reports path
     */
    public String getReportsPath() {
        return getProperty("reports.path", "target/reports");
    }
    
    /**
     * Gets the screenshots path.
     * 
     * @return the screenshots path
     */
    public String getScreenshotsPath() {
        return getProperty("screenshots.path", "target/screenshots");
    }
    
    /**
     * Checks if parallel execution is enabled.
     * 
     * @return true if parallel execution is enabled, false otherwise
     */
    public boolean isParallelTests() {
        return getBooleanProperty("parallel.tests", false);
    }
    
    /**
     * Gets the thread count for parallel execution.
     * 
     * @return the thread count
     */
    public int getThreadCount() {
        return getIntProperty("thread.count", 1);
    }
}
