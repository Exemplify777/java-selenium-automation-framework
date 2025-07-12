package com.automation.utils;

import com.automation.config.ConfigReader;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for taking and managing screenshots.
 * Provides methods for full page and element-specific screenshots.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class ScreenshotUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    private static final ConfigReader CONFIG = ConfigReader.getInstance();
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ScreenshotUtils() {
        // Utility class
    }
    
    /**
     * Takes a full page screenshot.
     * 
     * @param driver the WebDriver instance
     * @param testName the name of the test
     * @return the path to the screenshot file
     */
    public static String takeScreenshot(final WebDriver driver, final String testName) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String screenshotPath = generateScreenshotPath(testName, "full");
            File destFile = new File(screenshotPath);
            
            // Create directories if they don't exist
            destFile.getParentFile().mkdirs();
            
            FileUtils.copyFile(sourceFile, destFile);
            
            LOGGER.info("Screenshot taken: {}", screenshotPath);
            return screenshotPath;
            
        } catch (IOException e) {
            LOGGER.error("Failed to take screenshot for test: {}", testName, e);
            return null;
        }
    }
    
    /**
     * Takes a screenshot of a specific element.
     * 
     * @param element the WebElement to capture
     * @param testName the name of the test
     * @param elementName the name of the element
     * @return the path to the screenshot file
     */
    public static String takeElementScreenshot(final WebElement element, final String testName, final String elementName) {
        try {
            File sourceFile = element.getScreenshotAs(OutputType.FILE);
            
            String screenshotPath = generateScreenshotPath(testName, elementName);
            File destFile = new File(screenshotPath);
            
            // Create directories if they don't exist
            destFile.getParentFile().mkdirs();
            
            FileUtils.copyFile(sourceFile, destFile);
            
            LOGGER.info("Element screenshot taken: {}", screenshotPath);
            return screenshotPath;
            
        } catch (IOException e) {
            LOGGER.error("Failed to take element screenshot for test: {} element: {}", testName, elementName, e);
            return null;
        }
    }
    
    /**
     * Takes a screenshot and returns it as a byte array.
     * Useful for embedding in reports.
     * 
     * @param driver the WebDriver instance
     * @return screenshot as byte array
     */
    public static byte[] takeScreenshotAsBytes(final WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            LOGGER.error("Failed to take screenshot as bytes", e);
            return new byte[0];
        }
    }
    
    /**
     * Takes a screenshot and returns it as a base64 string.
     * Useful for embedding in HTML reports.
     * 
     * @param driver the WebDriver instance
     * @return screenshot as base64 string
     */
    public static String takeScreenshotAsBase64(final WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            LOGGER.error("Failed to take screenshot as base64", e);
            return "";
        }
    }
    
    /**
     * Takes a screenshot on test failure.
     * 
     * @param driver the WebDriver instance
     * @param testName the name of the failed test
     * @return the path to the screenshot file
     */
    public static String takeFailureScreenshot(final WebDriver driver, final String testName) {
        if (CONFIG.isScreenshotOnFailure()) {
            return takeScreenshot(driver, testName + "_FAILED");
        }
        return null;
    }
    
    /**
     * Takes a screenshot on test success.
     * 
     * @param driver the WebDriver instance
     * @param testName the name of the successful test
     * @return the path to the screenshot file
     */
    public static String takeSuccessScreenshot(final WebDriver driver, final String testName) {
        if (CONFIG.getBooleanProperty("screenshot.on.success", false)) {
            return takeScreenshot(driver, testName + "_PASSED");
        }
        return null;
    }
    
    /**
     * Generates a unique screenshot file path.
     * 
     * @param testName the name of the test
     * @param suffix additional suffix for the filename
     * @return the screenshot file path
     */
    private static String generateScreenshotPath(final String testName, final String suffix) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String screenshotsDir = CONFIG.getScreenshotsPath();
        String format = CONFIG.getProperty("screenshot.format", "png");
        
        String fileName = String.format("%s_%s_%s.%s", 
            sanitizeFileName(testName), 
            sanitizeFileName(suffix), 
            timestamp, 
            format);
        
        return screenshotsDir + File.separator + fileName;
    }
    
    /**
     * Sanitizes a filename by removing invalid characters.
     * 
     * @param fileName the original filename
     * @return the sanitized filename
     */
    private static String sanitizeFileName(final String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * Creates the screenshots directory if it doesn't exist.
     */
    public static void createScreenshotsDirectory() {
        String screenshotsDir = CONFIG.getScreenshotsPath();
        File directory = new File(screenshotsDir);
        
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                LOGGER.info("Screenshots directory created: {}", screenshotsDir);
            } else {
                LOGGER.error("Failed to create screenshots directory: {}", screenshotsDir);
            }
        }
    }
    
    /**
     * Cleans up old screenshot files.
     * 
     * @param daysToKeep number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(final int daysToKeep) {
        String screenshotsDir = CONFIG.getScreenshotsPath();
        File directory = new File(screenshotsDir);
        
        if (!directory.exists()) {
            return;
        }
        
        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60L * 60L * 1000L);
        
        File[] files = directory.listFiles();
        if (files != null) {
            int deletedCount = 0;
            for (File file : files) {
                if (file.isFile() && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
            LOGGER.info("Cleaned up {} old screenshot files", deletedCount);
        }
    }
    
    /**
     * Gets the total size of screenshots directory.
     * 
     * @return the size in bytes
     */
    public static long getScreenshotsDirectorySize() {
        String screenshotsDir = CONFIG.getScreenshotsPath();
        File directory = new File(screenshotsDir);
        
        if (!directory.exists()) {
            return 0;
        }
        
        try {
            return FileUtils.sizeOfDirectory(directory);
        } catch (Exception e) {
            LOGGER.error("Failed to calculate screenshots directory size", e);
            return 0;
        }
    }
    
    /**
     * Compresses screenshots directory to a ZIP file.
     * 
     * @param zipFileName the name of the ZIP file
     * @return the path to the ZIP file
     */
    public static String compressScreenshots(final String zipFileName) {
        // Implementation would depend on requirements
        // This is a placeholder for future enhancement
        LOGGER.info("Screenshot compression requested for: {}", zipFileName);
        return null;
    }
}
