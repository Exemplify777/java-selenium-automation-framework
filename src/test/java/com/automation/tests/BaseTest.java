package com.automation.tests;

import com.automation.config.ConfigReader;
import com.automation.utils.DriverManager;
import com.automation.utils.ExtentReportManager;
import com.automation.utils.ScreenshotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

/**
 * Base test class containing common setup and teardown methods.
 * All test classes should extend this class to inherit common functionality.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public abstract class BaseTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
    protected static final ConfigReader CONFIG = ConfigReader.getInstance();
    
    /**
     * Suite-level setup executed once before all tests.
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        LOGGER.info("Starting test suite execution");
        LOGGER.info("Environment: {}", CONFIG.getEnvironment());
        LOGGER.info("Browser: {}", CONFIG.getBrowser());
        LOGGER.info("Base URL: {}", CONFIG.getBaseUrl());
        LOGGER.info("Headless Mode: {}", CONFIG.isHeadless());
        LOGGER.info("Parallel Execution: {}", CONFIG.isParallelTests());
        
        if (CONFIG.isParallelTests()) {
            LOGGER.info("Thread Count: {}", CONFIG.getThreadCount());
        }
        
        // Initialize Extent Reports
        ExtentReportManager.initializeReport();
        
        // Create necessary directories
        ScreenshotUtils.createScreenshotsDirectory();
        
        LOGGER.info("Test suite setup completed successfully");
    }
    
    /**
     * Class-level setup executed once before all test methods in the class.
     */
    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        LOGGER.info("Setting up test class: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Method-level setup executed before each test method.
     */
    @BeforeMethod(alwaysRun = true)
    public void methodSetup() {
        LOGGER.info("Setting up test method");
        
        // Create WebDriver instance
        DriverManager.createDriver();
        
        LOGGER.debug("WebDriver created successfully");
    }
    
    /**
     * Method-level teardown executed after each test method.
     */
    @AfterMethod(alwaysRun = true)
    public void methodTeardown() {
        LOGGER.info("Tearing down test method");
        
        // Quit WebDriver instance
        if (DriverManager.hasDriver()) {
            DriverManager.quitDriver();
            LOGGER.debug("WebDriver quit successfully");
        }
    }
    
    /**
     * Class-level teardown executed once after all test methods in the class.
     */
    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        LOGGER.info("Tearing down test class: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Suite-level teardown executed once after all tests.
     */
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        LOGGER.info("Tearing down test suite");
        
        // Flush Extent Reports
        ExtentReportManager.flushReport();
        
        // Clean up old screenshots if configured
        int daysToKeep = CONFIG.getIntProperty("screenshot.cleanup.days", 7);
        ScreenshotUtils.cleanupOldScreenshots(daysToKeep);
        
        LOGGER.info("Test suite teardown completed successfully");
        LOGGER.info("Reports generated at: {}", CONFIG.getReportsPath());
    }
    
    /**
     * Gets the current test method name.
     * 
     * @return the test method name
     */
    protected String getCurrentTestName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
    
    /**
     * Logs test step information.
     * 
     * @param stepDescription the description of the test step
     */
    protected void logTestStep(final String stepDescription) {
        LOGGER.info("Test Step: {}", stepDescription);
        ExtentReportManager.logInfo("Step: " + stepDescription);
    }
    
    /**
     * Logs test information.
     * 
     * @param message the information message
     */
    protected void logTestInfo(final String message) {
        LOGGER.info(message);
        ExtentReportManager.logInfo(message);
    }
    
    /**
     * Logs test warning.
     * 
     * @param message the warning message
     */
    protected void logTestWarning(final String message) {
        LOGGER.warn(message);
        ExtentReportManager.logWarning(message);
    }
    
    /**
     * Takes a screenshot and attaches it to the report.
     * 
     * @param description the screenshot description
     */
    protected void takeScreenshot(final String description) {
        if (DriverManager.hasDriver()) {
            String screenshotPath = ScreenshotUtils.takeScreenshot(
                DriverManager.getDriver(), getCurrentTestName());
            if (screenshotPath != null) {
                ExtentReportManager.attachScreenshot(screenshotPath, description);
            }
        }
    }
    
    /**
     * Waits for a specified number of seconds.
     * Use sparingly and prefer explicit waits.
     * 
     * @param seconds the number of seconds to wait
     */
    protected void waitForSeconds(final int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            LOGGER.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Wait interrupted", e);
        }
    }
    
    /**
     * Navigates to the base URL.
     */
    protected void navigateToBaseUrl() {
        String baseUrl = CONFIG.getBaseUrl();
        DriverManager.getDriver().get(baseUrl);
        LOGGER.info("Navigated to base URL: {}", baseUrl);
        ExtentReportManager.logInfo("Navigated to: " + baseUrl);
    }
    
    /**
     * Gets the current page URL.
     * 
     * @return the current page URL
     */
    protected String getCurrentUrl() {
        String url = DriverManager.getDriver().getCurrentUrl();
        LOGGER.debug("Current URL: {}", url);
        return url;
    }
    
    /**
     * Gets the current page title.
     * 
     * @return the current page title
     */
    protected String getCurrentTitle() {
        String title = DriverManager.getDriver().getTitle();
        LOGGER.debug("Current title: {}", title);
        return title;
    }
    
    /**
     * Refreshes the current page.
     */
    protected void refreshPage() {
        DriverManager.getDriver().navigate().refresh();
        LOGGER.info("Page refreshed");
        ExtentReportManager.logInfo("Page refreshed");
    }
    
    /**
     * Navigates back in browser history.
     */
    protected void navigateBack() {
        DriverManager.getDriver().navigate().back();
        LOGGER.info("Navigated back");
        ExtentReportManager.logInfo("Navigated back");
    }
    
    /**
     * Navigates forward in browser history.
     */
    protected void navigateForward() {
        DriverManager.getDriver().navigate().forward();
        LOGGER.info("Navigated forward");
        ExtentReportManager.logInfo("Navigated forward");
    }
    
    /**
     * Maximizes the browser window.
     */
    protected void maximizeWindow() {
        DriverManager.getDriver().manage().window().maximize();
        LOGGER.debug("Browser window maximized");
    }
    
    /**
     * Deletes all cookies.
     */
    protected void deleteAllCookies() {
        DriverManager.getDriver().manage().deleteAllCookies();
        LOGGER.debug("All cookies deleted");
        ExtentReportManager.logInfo("All cookies deleted");
    }
    
    /**
     * Clears browser cache (by deleting cookies and refreshing).
     */
    protected void clearBrowserCache() {
        deleteAllCookies();
        refreshPage();
        LOGGER.info("Browser cache cleared");
        ExtentReportManager.logInfo("Browser cache cleared");
    }
}
