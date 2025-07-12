package com.automation.utils;

import com.automation.config.ConfigReader;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Extent Reports manager for generating comprehensive test reports.
 * This class manages the lifecycle of Extent Reports and provides
 * thread-safe test reporting capabilities.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class ExtentReportManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final ConfigReader CONFIG = ConfigReader.getInstance();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Private constructor to prevent instantiation.
     */
    private ExtentReportManager() {
        // Utility class
    }
    
    /**
     * Initializes the Extent Reports.
     */
    public static synchronized void initializeReport() {
        if (extentReports == null) {
            String reportPath = generateReportPath();
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            setSystemInfo();
            
            LOGGER.info("Extent Reports initialized: {}", reportPath);
        }
    }
    
    /**
     * Configures the Spark reporter with custom settings.
     * 
     * @param sparkReporter the ExtentSparkReporter instance
     */
    private static void configureSparkReporter(final ExtentSparkReporter sparkReporter) {
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle(CONFIG.getProperty("extent.report.title", "Automation Test Report"));
        sparkReporter.config().setReportName(CONFIG.getProperty("extent.report.name", "Test Execution Report"));
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        sparkReporter.config().setEncoding("UTF-8");
        
        // Custom CSS for better appearance
        sparkReporter.config().setCss(
            ".badge-primary { background-color: #007bff; } " +
            ".badge-success { background-color: #28a745; } " +
            ".badge-danger { background-color: #dc3545; } " +
            ".badge-warning { background-color: #ffc107; color: #212529; }"
        );
        
        // Custom JavaScript for enhanced functionality
        sparkReporter.config().setJs(
            "document.addEventListener('DOMContentLoaded', function() { " +
            "console.log('Extent Report loaded successfully'); " +
            "});"
        );
    }
    
    /**
     * Sets system information in the report.
     */
    private static void setSystemInfo() {
        extentReports.setSystemInfo("Environment", CONFIG.getEnvironment());
        extentReports.setSystemInfo("Browser", CONFIG.getBrowser());
        extentReports.setSystemInfo("Base URL", CONFIG.getBaseUrl());
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("User", System.getProperty("user.name"));
        extentReports.setSystemInfo("Headless Mode", String.valueOf(CONFIG.isHeadless()));
        extentReports.setSystemInfo("Parallel Execution", String.valueOf(CONFIG.isParallelTests()));
        
        if (CONFIG.isParallelTests()) {
            extentReports.setSystemInfo("Thread Count", String.valueOf(CONFIG.getThreadCount()));
        }
    }
    
    /**
     * Creates a new test in the report.
     * 
     * @param testName the name of the test
     * @param description the test description
     * @return the ExtentTest instance
     */
    public static ExtentTest createTest(final String testName, final String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        extentTest.set(test);
        LOGGER.debug("Created test in report: {}", testName);
        return test;
    }
    
    /**
     * Creates a new test with category in the report.
     * 
     * @param testName the name of the test
     * @param description the test description
     * @param category the test category
     * @return the ExtentTest instance
     */
    public static ExtentTest createTest(final String testName, final String description, final String category) {
        ExtentTest test = extentReports.createTest(testName, description);
        test.assignCategory(category);
        extentTest.set(test);
        LOGGER.debug("Created test in report: {} with category: {}", testName, category);
        return test;
    }
    
    /**
     * Gets the current test instance.
     * 
     * @return the current ExtentTest instance
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    /**
     * Logs an info message to the current test.
     * 
     * @param message the message to log
     */
    public static void logInfo(final String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.INFO, message);
        }
    }
    
    /**
     * Logs a pass message to the current test.
     * 
     * @param message the message to log
     */
    public static void logPass(final String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.PASS, message);
        }
    }
    
    /**
     * Logs a fail message to the current test.
     * 
     * @param message the message to log
     */
    public static void logFail(final String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.FAIL, message);
        }
    }
    
    /**
     * Logs a warning message to the current test.
     * 
     * @param message the message to log
     */
    public static void logWarning(final String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.WARNING, message);
        }
    }
    
    /**
     * Logs a skip message to the current test.
     * 
     * @param message the message to log
     */
    public static void logSkip(final String message) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.log(Status.SKIP, message);
        }
    }
    
    /**
     * Attaches a screenshot to the current test.
     * 
     * @param screenshotPath the path to the screenshot
     * @param description the screenshot description
     */
    public static void attachScreenshot(final String screenshotPath, final String description) {
        ExtentTest test = extentTest.get();
        if (test != null && screenshotPath != null) {
            try {
                test.addScreenCaptureFromPath(screenshotPath, description);
                LOGGER.debug("Screenshot attached to report: {}", screenshotPath);
            } catch (Exception e) {
                LOGGER.error("Failed to attach screenshot to report: {}", screenshotPath, e);
            }
        }
    }
    
    /**
     * Attaches a base64 screenshot to the current test.
     * 
     * @param base64Screenshot the base64 encoded screenshot
     * @param description the screenshot description
     */
    public static void attachBase64Screenshot(final String base64Screenshot, final String description) {
        ExtentTest test = extentTest.get();
        if (test != null && base64Screenshot != null && !base64Screenshot.isEmpty()) {
            try {
                test.addScreenCaptureFromBase64String(base64Screenshot, description);
                LOGGER.debug("Base64 screenshot attached to report");
            } catch (Exception e) {
                LOGGER.error("Failed to attach base64 screenshot to report", e);
            }
        }
    }
    
    /**
     * Adds an author to the current test.
     * 
     * @param author the author name
     */
    public static void addAuthor(final String author) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.assignAuthor(author);
        }
    }
    
    /**
     * Adds a category to the current test.
     * 
     * @param category the category name
     */
    public static void addCategory(final String category) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.assignCategory(category);
        }
    }
    
    /**
     * Adds a device to the current test.
     * 
     * @param device the device name
     */
    public static void addDevice(final String device) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            test.assignDevice(device);
        }
    }
    
    /**
     * Creates a child test (step) under the current test.
     * 
     * @param stepName the name of the step
     * @param description the step description
     * @return the child ExtentTest instance
     */
    public static ExtentTest createStep(final String stepName, final String description) {
        ExtentTest test = extentTest.get();
        if (test != null) {
            ExtentTest step = test.createNode(stepName, description);
            LOGGER.debug("Created step in report: {}", stepName);
            return step;
        }
        return null;
    }
    
    /**
     * Flushes the report and writes it to disk.
     */
    public static synchronized void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
            LOGGER.info("Extent Reports flushed successfully");
        }
    }
    
    /**
     * Removes the current test from ThreadLocal.
     */
    public static void removeTest() {
        extentTest.remove();
    }
    
    /**
     * Generates the report file path.
     * 
     * @return the report file path
     */
    private static String generateReportPath() {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String reportsDir = CONFIG.getReportsPath();
        String reportName = CONFIG.getProperty("extent.report.name", "TestReport");
        
        // Create reports directory if it doesn't exist
        File directory = new File(reportsDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        return reportsDir + File.separator + reportName + "_" + timestamp + ".html";
    }
    
    /**
     * Gets the report file path.
     * 
     * @return the current report file path
     */
    public static String getReportPath() {
        return generateReportPath();
    }
}
