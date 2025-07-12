package com.automation.tests;

import com.automation.config.ConfigReader;
import com.automation.utils.*;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Framework validation test to ensure all components work correctly.
 * This test validates the framework setup without requiring external dependencies.
 *
 * @author Automation Framework
 * @version 1.0
 */
public class FrameworkValidationTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkValidationTest.class);
    
    /**
     * Test configuration reader functionality.
     */
    @Test(description = "Validate configuration reader functionality", 
          groups = {"framework", "validation"})
    public void testConfigurationReader() {
        LOGGER.info("Testing configuration reader");
        
        ConfigReader config = ConfigReader.getInstance();
        
        // Test basic configuration reading
        AssertionUtils.assertNotNull(config, "ConfigReader instance should not be null");
        AssertionUtils.assertNotNull(config.getEnvironment(), "Environment should not be null");
        AssertionUtils.assertNotNull(config.getBrowser(), "Browser should not be null");
        
        // Test default values
        AssertionUtils.assertEquals(config.getBrowser(), "chrome", "Default browser should be chrome");
        AssertionUtils.assertEquals(config.getImplicitWait(), 10, "Default implicit wait should be 10");
        
        LOGGER.info("Configuration reader test completed successfully");
    }
    
    /**
     * Test JSON data reader functionality.
     */
    @Test(description = "Validate JSON data reader functionality", 
          groups = {"framework", "validation"})
    public void testJsonDataReader() {
        LOGGER.info("Testing JSON data reader");
        
        // Test reading JSON resource
        String username = JsonDataReader.getValueByKeyPathFromResource(
            "testdata/users.json", "testCredentials.admin.username");
        
        AssertionUtils.assertNotNull(username, "Username should not be null");
        AssertionUtils.assertEquals(username, "admin", "Username should be 'admin'");
        
        LOGGER.info("JSON data reader test completed successfully");
    }
    
    /**
     * Test CSV data reader functionality.
     */
    @Test(description = "Validate CSV data reader functionality", 
          groups = {"framework", "validation"})
    public void testCsvDataReader() {
        LOGGER.info("Testing CSV data reader");
        
        // Test reading CSV resource
        int rowCount = CsvDataReader.getRowCount("src/test/resources/testdata/products.csv");
        
        AssertionUtils.assertGreaterThan(rowCount, 0.0, "CSV should have at least one row");
        
        LOGGER.info("CSV data reader test completed successfully");
    }
    
    /**
     * Test assertion utilities functionality.
     */
    @Test(description = "Validate assertion utilities functionality", 
          groups = {"framework", "validation"})
    public void testAssertionUtils() {
        LOGGER.info("Testing assertion utilities");
        
        // Test basic assertions
        AssertionUtils.assertTrue(true, "True assertion should pass");
        AssertionUtils.assertFalse(false, "False assertion should pass");
        AssertionUtils.assertEquals("test", "test", "String equality assertion should pass");
        AssertionUtils.assertNotNull("not null", "Not null assertion should pass");
        
        // Test string assertions
        AssertionUtils.assertContains("Hello World", "World", "Contains assertion should pass");
        AssertionUtils.assertStartsWith("Hello World", "Hello", "Starts with assertion should pass");
        AssertionUtils.assertEndsWith("Hello World", "World", "Ends with assertion should pass");
        
        LOGGER.info("Assertion utilities test completed successfully");
    }
    
    /**
     * Test extent report manager initialization.
     */
    @Test(description = "Validate extent report manager functionality", 
          groups = {"framework", "validation"})
    public void testExtentReportManager() {
        LOGGER.info("Testing extent report manager");
        
        // Test report initialization (should not throw exceptions)
        ExtentReportManager.initializeReport();
        
        // Test creating a test
        ExtentReportManager.createTest("Validation Test", "Testing framework components");
        ExtentReportManager.logInfo("This is a test log message");
        ExtentReportManager.logPass("Framework validation passed");
        
        AssertionUtils.assertNotNull(ExtentReportManager.getTest(), "Extent test should not be null");
        
        LOGGER.info("Extent report manager test completed successfully");
    }
    
    /**
     * Test screenshot utilities (without WebDriver).
     */
    @Test(description = "Validate screenshot utilities setup", 
          groups = {"framework", "validation"})
    public void testScreenshotUtilities() {
        LOGGER.info("Testing screenshot utilities setup");
        
        // Test directory creation
        ScreenshotUtils.createScreenshotsDirectory();
        
        // Test directory size calculation
        long size = ScreenshotUtils.getScreenshotsDirectorySize();
        AssertionUtils.assertGreaterThan(size, -1.0, "Directory size should be non-negative");
        
        LOGGER.info("Screenshot utilities test completed successfully");
    }
    
    /**
     * Test framework integration.
     */
    @Test(description = "Validate overall framework integration", 
          groups = {"framework", "validation", "integration"})
    public void testFrameworkIntegration() {
        LOGGER.info("Testing framework integration");
        
        // Test that all major components can be initialized together
        ConfigReader config = ConfigReader.getInstance();
        ExtentReportManager.initializeReport();
        ScreenshotUtils.createScreenshotsDirectory();
        
        // Test configuration values
        String environment = config.getEnvironment();
        String browser = config.getBrowser();
        boolean headless = config.isHeadless();
        
        LOGGER.info("Framework running with: Environment={}, Browser={}, Headless={}", 
            environment, browser, headless);
        
        // Test data reading
        String testUser = JsonDataReader.getValueByKeyPathFromResource(
            "testdata/users.json", "testCredentials.user.username");
        
        AssertionUtils.assertNotNull(testUser, "Test user should be loaded from JSON");
        
        // Test reporting
        ExtentReportManager.createTest("Integration Test", "Testing framework integration");
        ExtentReportManager.logInfo("Environment: " + environment);
        ExtentReportManager.logInfo("Browser: " + browser);
        ExtentReportManager.logInfo("Test User: " + testUser);
        ExtentReportManager.logPass("Framework integration test completed successfully");
        
        LOGGER.info("Framework integration test completed successfully");
    }
}
