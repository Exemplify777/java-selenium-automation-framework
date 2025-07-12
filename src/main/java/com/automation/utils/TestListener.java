package com.automation.utils;

import com.automation.config.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;

/**
 * TestNG listener for handling test events and integrating with reporting.
 * This listener captures test results and manages screenshots and logging.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class TestListener implements ITestListener {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TestListener.class);
    private static final ConfigReader CONFIG = ConfigReader.getInstance();
    
    /**
     * Called before the test suite starts.
     * 
     * @param context the test context
     */
    @Override
    public void onStart(final ITestContext context) {
        LOGGER.info("Test Suite Started: {}", context.getName());
        
        // Initialize Extent Reports
        ExtentReportManager.initializeReport();
        
        // Create screenshots directory
        ScreenshotUtils.createScreenshotsDirectory();
        
        // Log test suite information
        LOGGER.info("Environment: {}", CONFIG.getEnvironment());
        LOGGER.info("Browser: {}", CONFIG.getBrowser());
        LOGGER.info("Base URL: {}", CONFIG.getBaseUrl());
        LOGGER.info("Parallel Execution: {}", CONFIG.isParallelTests());
        
        if (CONFIG.isParallelTests()) {
            LOGGER.info("Thread Count: {}", CONFIG.getThreadCount());
        }
    }
    
    /**
     * Called after the test suite finishes.
     * 
     * @param context the test context
     */
    @Override
    public void onFinish(final ITestContext context) {
        LOGGER.info("Test Suite Finished: {}", context.getName());
        
        // Log test suite summary
        int totalTests = context.getAllTestMethods().length;
        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();
        
        LOGGER.info("Test Suite Summary:");
        LOGGER.info("Total Tests: {}", totalTests);
        LOGGER.info("Passed Tests: {}", passedTests);
        LOGGER.info("Failed Tests: {}", failedTests);
        LOGGER.info("Skipped Tests: {}", skippedTests);
        
        // Calculate success rate
        double successRate = totalTests > 0 ? (double) passedTests / totalTests * 100 : 0;
        LOGGER.info("Success Rate: {:.2f}%", successRate);
        
        // Flush Extent Reports
        ExtentReportManager.flushReport();
        
        // Clean up old screenshots if configured
        int daysToKeep = CONFIG.getIntProperty("screenshot.cleanup.days", 7);
        ScreenshotUtils.cleanupOldScreenshots(daysToKeep);
        
        LOGGER.info("Test execution completed. Report generated at: {}", ExtentReportManager.getReportPath());
    }
    
    /**
     * Called when a test starts.
     * 
     * @param result the test result
     */
    @Override
    public void onTestStart(final ITestResult result) {
        String testName = getTestName(result);
        String testDescription = getTestDescription(result);
        String testCategory = getTestCategory(result);
        
        LOGGER.info("Test Started: {}", testName);
        
        // Create test in Extent Reports
        if (testCategory != null && !testCategory.isEmpty()) {
            ExtentReportManager.createTest(testName, testDescription, testCategory);
        } else {
            ExtentReportManager.createTest(testName, testDescription);
        }
        
        // Log test start
        ExtentReportManager.logInfo("Test execution started");
        ExtentReportManager.logInfo("Test Method: " + result.getMethod().getMethodName());
        ExtentReportManager.logInfo("Test Class: " + result.getTestClass().getName());
        
        // Add test metadata
        String author = getTestAuthor(result);
        if (author != null && !author.isEmpty()) {
            ExtentReportManager.addAuthor(author);
        }
        
        if (testCategory != null && !testCategory.isEmpty()) {
            ExtentReportManager.addCategory(testCategory);
        }
        
        // Add browser information
        ExtentReportManager.addDevice(CONFIG.getBrowser().toUpperCase());
    }
    
    /**
     * Called when a test passes.
     * 
     * @param result the test result
     */
    @Override
    public void onTestSuccess(final ITestResult result) {
        String testName = getTestName(result);
        LOGGER.info("Test Passed: {}", testName);
        
        // Log success in Extent Reports
        ExtentReportManager.logPass("Test executed successfully");
        
        // Take success screenshot if configured
        if (DriverManager.hasDriver()) {
            String screenshotPath = ScreenshotUtils.takeSuccessScreenshot(DriverManager.getDriver(), testName);
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Test Success Screenshot");
            }
        }
        
        // Log execution time
        long executionTime = result.getEndMillis() - result.getStartMillis();
        ExtentReportManager.logInfo("Execution Time: " + executionTime + " ms");
        
        LOGGER.info("Test '{}' completed successfully in {} ms", testName, executionTime);
    }
    
    /**
     * Called when a test fails.
     * 
     * @param result the test result
     */
    @Override
    public void onTestFailure(final ITestResult result) {
        String testName = getTestName(result);
        Throwable throwable = result.getThrowable();
        
        LOGGER.error("Test Failed: {}", testName, throwable);
        
        // Log failure in Extent Reports
        ExtentReportManager.logFail("Test failed: " + throwable.getMessage());
        
        // Take failure screenshot
        if (DriverManager.hasDriver()) {
            String screenshotPath = ScreenshotUtils.takeFailureScreenshot(DriverManager.getDriver(), testName);
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, "Test Failure Screenshot");
            }
        }
        
        // Log stack trace
        if (throwable != null) {
            ExtentReportManager.logFail("Stack Trace: " + getStackTrace(throwable));
        }
        
        // Log execution time
        long executionTime = result.getEndMillis() - result.getStartMillis();
        ExtentReportManager.logInfo("Execution Time: " + executionTime + " ms");
        
        LOGGER.error("Test '{}' failed after {} ms", testName, executionTime);
    }
    
    /**
     * Called when a test is skipped.
     * 
     * @param result the test result
     */
    @Override
    public void onTestSkipped(final ITestResult result) {
        String testName = getTestName(result);
        Throwable throwable = result.getThrowable();
        
        LOGGER.warn("Test Skipped: {}", testName);
        
        // Create test in Extent Reports if not already created
        if (ExtentReportManager.getTest() == null) {
            String testDescription = getTestDescription(result);
            ExtentReportManager.createTest(testName, testDescription);
        }
        
        // Log skip in Extent Reports
        String skipReason = throwable != null ? throwable.getMessage() : "Test was skipped";
        ExtentReportManager.logSkip("Test skipped: " + skipReason);
        
        LOGGER.warn("Test '{}' was skipped. Reason: {}", testName, skipReason);
    }
    
    /**
     * Called when a test fails but is within success percentage.
     * 
     * @param result the test result
     */
    @Override
    public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
        String testName = getTestName(result);
        LOGGER.warn("Test Failed but within success percentage: {}", testName);
        
        ExtentReportManager.logWarning("Test failed but within success percentage");
    }
    
    /**
     * Gets the test name from the test result.
     * 
     * @param result the test result
     * @return the test name
     */
    private String getTestName(final ITestResult result) {
        return result.getMethod().getMethodName();
    }
    
    /**
     * Gets the test description from the test result.
     * 
     * @param result the test result
     * @return the test description
     */
    private String getTestDescription(final ITestResult result) {
        String description = result.getMethod().getDescription();
        return description != null ? description : "Test method: " + getTestName(result);
    }
    
    /**
     * Gets the test category from annotations or method name.
     * 
     * @param result the test result
     * @return the test category
     */
    private String getTestCategory(final ITestResult result) {
        // This can be enhanced to read from custom annotations
        String className = result.getTestClass().getName();
        String[] parts = className.split("\\.");
        return parts.length > 0 ? parts[parts.length - 1] : "General";
    }
    
    /**
     * Gets the test author from annotations.
     * 
     * @param result the test result
     * @return the test author
     */
    private String getTestAuthor(final ITestResult result) {
        // This can be enhanced to read from custom annotations
        return "Automation Team";
    }
    
    /**
     * Gets the stack trace as a string.
     * 
     * @param throwable the throwable
     * @return the stack trace string
     */
    private String getStackTrace(final Throwable throwable) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
