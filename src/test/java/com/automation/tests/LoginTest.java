package com.automation.tests;

import com.automation.pages.HomePage;
import com.automation.pages.LoginPage;
import com.automation.utils.*;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Comprehensive test class for login functionality.
 * Demonstrates best practices for test implementation with data-driven testing.
 * 
 * @author Automation Framework
 * @version 1.0
 */
@Listeners(TestListener.class)
public class LoginTest extends BaseTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginTest.class);
    private LoginPage loginPage;
    
    /**
     * Setup method executed before each test method.
     */
    @BeforeMethod
    public void setUp() {
        LOGGER.info("Setting up test environment");
        loginPage = new LoginPage();
        loginPage.navigateToLoginPage();
        
        // Verify login page is loaded
        AssertionUtils.assertTrue(loginPage.isLoginPageLoaded(), 
            "Login page should be loaded successfully");
        
        ExtentReportManager.logInfo("Navigated to login page and verified page load");
    }
    
    /**
     * Test successful login with valid credentials.
     */
    @Test(description = "Verify successful login with valid credentials", 
          groups = {"smoke", "login", "positive"})
    public void testSuccessfulLogin() {
        LOGGER.info("Starting test: testSuccessfulLogin");
        ExtentReportManager.logInfo("Testing successful login with valid credentials");
        
        // Get test data from JSON
        String username = JsonDataReader.getValueByKeyPathFromResource(
            "testdata/users.json", "testCredentials.admin.username");
        String password = JsonDataReader.getValueByKeyPathFromResource(
            "testdata/users.json", "testCredentials.admin.password");
        
        ExtentReportManager.logInfo("Using credentials - Username: " + username);
        
        // Perform login
        HomePage homePage = loginPage.login(username, password);
        
        // Verify successful login
        AssertionUtils.assertTrue(homePage.isUserLoggedIn(), 
            "User should be logged in successfully");
        AssertionUtils.assertTrue(homePage.isHomePageLoaded(), 
            "Home page should be loaded after successful login");
        
        String welcomeMessage = homePage.getWelcomeMessage();
        AssertionUtils.assertContains(welcomeMessage.toLowerCase(), "welcome", 
            "Welcome message should contain 'welcome' text");
        
        ExtentReportManager.logPass("Login successful - User logged in and redirected to home page");
        LOGGER.info("Test completed successfully: testSuccessfulLogin");
    }
    
    /**
     * Test login failure with invalid credentials.
     */
    @Test(description = "Verify login failure with invalid credentials", 
          groups = {"login", "negative"})
    public void testLoginWithInvalidCredentials() {
        LOGGER.info("Starting test: testLoginWithInvalidCredentials");
        ExtentReportManager.logInfo("Testing login failure with invalid credentials");
        
        // Use invalid credentials
        String invalidUsername = "invaliduser";
        String invalidPassword = "wrongpassword";
        
        ExtentReportManager.logInfo("Using invalid credentials - Username: " + invalidUsername);
        
        // Attempt login
        loginPage.enterUsername(invalidUsername)
                 .enterPassword(invalidPassword)
                 .clickLoginButton();
        
        // Verify login failure
        AssertionUtils.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for invalid credentials");
        
        String errorMessage = loginPage.getErrorMessage();
        AssertionUtils.assertNotNull(errorMessage, "Error message should not be null");
        AssertionUtils.assertFalse(errorMessage.isEmpty(), "Error message should not be empty");
        
        // Verify user is still on login page
        AssertionUtils.assertTrue(loginPage.isLoginPageLoaded(), 
            "User should remain on login page after failed login");
        
        ExtentReportManager.logPass("Login correctly failed with invalid credentials");
        LOGGER.info("Test completed successfully: testLoginWithInvalidCredentials");
    }
    
    /**
     * Data provider for login test data from JSON file.
     *
     * @return Object array containing test data
     */
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        // Simple test data for demonstration
        Object[][] data = new Object[2][3]; // username, password, expectedResult

        data[0] = new Object[]{"admin", "admin123", true};
        data[1] = new Object[]{"testuser1", "password123", true};

        return data;
    }
    
    /**
     * Data-driven test for login functionality.
     * 
     * @param username the username to test
     * @param password the password to test
     * @param expectedResult the expected login result
     */
    @Test(dataProvider = "loginData", 
          description = "Data-driven test for login functionality",
          groups = {"login", "datadriven"})
    public void testLoginWithMultipleCredentials(String username, String password, boolean expectedResult) {
        LOGGER.info("Starting data-driven test with username: {}", username);
        ExtentReportManager.logInfo("Testing login with username: " + username);
        
        // Perform login
        HomePage homePage = loginPage.login(username, password);
        
        if (expectedResult) {
            // Verify successful login
            AssertionUtils.assertTrue(homePage.isUserLoggedIn(), 
                "User should be logged in successfully with valid credentials");
            ExtentReportManager.logPass("Login successful for user: " + username);
            
            // Logout for next iteration
            homePage.logout();
        } else {
            // Verify login failure
            AssertionUtils.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for invalid credentials");
            ExtentReportManager.logPass("Login correctly failed for user: " + username);
        }
        
        LOGGER.info("Data-driven test completed for username: {}", username);
    }
    
    /**
     * Test login with remember me functionality.
     */
    @Test(description = "Verify login with remember me functionality", 
          groups = {"login", "functionality"})
    public void testLoginWithRememberMe() {
        LOGGER.info("Starting test: testLoginWithRememberMe");
        ExtentReportManager.logInfo("Testing login with remember me functionality");
        
        String username = "testuser1";
        String password = "password123";
        
        // Verify remember me checkbox is not checked initially
        AssertionUtils.assertFalse(loginPage.isRememberMeChecked(), 
            "Remember me checkbox should not be checked initially");
        
        // Perform login with remember me
        HomePage homePage = loginPage.loginWithRememberMe(username, password, true);
        
        // Verify successful login
        AssertionUtils.assertTrue(homePage.isUserLoggedIn(), 
            "User should be logged in successfully");
        
        ExtentReportManager.logPass("Login with remember me functionality works correctly");
        LOGGER.info("Test completed successfully: testLoginWithRememberMe");
    }
    
    /**
     * Test login with empty credentials.
     */
    @Test(description = "Verify login behavior with empty credentials", 
          groups = {"login", "negative", "validation"})
    public void testLoginWithEmptyCredentials() {
        LOGGER.info("Starting test: testLoginWithEmptyCredentials");
        ExtentReportManager.logInfo("Testing login with empty credentials");
        
        // Attempt login with empty credentials
        loginPage.enterUsername("")
                 .enterPassword("")
                 .clickLoginButton();
        
        // Verify appropriate validation
        boolean hasError = loginPage.isErrorMessageDisplayed();
        boolean loginButtonEnabled = loginPage.isLoginButtonEnabled();
        
        // Either error message should be shown or login button should be disabled
        AssertionUtils.assertTrue(hasError || !loginButtonEnabled, 
            "System should prevent login with empty credentials");
        
        ExtentReportManager.logPass("System correctly handles empty credentials");
        LOGGER.info("Test completed successfully: testLoginWithEmptyCredentials");
    }
    
    /**
     * Test login page UI elements.
     */
    @Test(description = "Verify login page UI elements are present", 
          groups = {"login", "ui", "smoke"})
    public void testLoginPageUIElements() {
        LOGGER.info("Starting test: testLoginPageUIElements");
        ExtentReportManager.logInfo("Verifying login page UI elements");
        
        // Verify all essential UI elements are present
        AssertionUtils.assertTrue(loginPage.isLoginPageLoaded(), 
            "Login page should be loaded with all essential elements");
        
        // Verify login button is enabled
        AssertionUtils.assertTrue(loginPage.isLoginButtonEnabled(), 
            "Login button should be enabled");
        
        // Verify page title
        String pageTitle = loginPage.getPageTitle();
        AssertionUtils.assertContains(pageTitle.toLowerCase(), "login", 
            "Page title should contain 'login'");
        
        ExtentReportManager.logPass("All login page UI elements are present and functional");
        LOGGER.info("Test completed successfully: testLoginPageUIElements");
    }
    
    /**
     * Test login performance.
     */
    @Test(description = "Verify login performance is within acceptable limits", 
          groups = {"login", "performance"})
    public void testLoginPerformance() {
        LOGGER.info("Starting test: testLoginPerformance");
        ExtentReportManager.logInfo("Testing login performance");
        
        String username = "admin";
        String password = "admin123";
        
        long startTime = System.currentTimeMillis();
        
        // Perform login
        HomePage homePage = loginPage.login(username, password);
        
        long endTime = System.currentTimeMillis();
        long loginTime = endTime - startTime;
        
        // Verify login was successful
        AssertionUtils.assertTrue(homePage.isUserLoggedIn(), 
            "User should be logged in successfully");
        
        // Verify login time is within acceptable limits (5 seconds)
        AssertionUtils.assertLessThan(loginTime, 5000.0, 
            "Login should complete within 5 seconds");
        
        ExtentReportManager.logInfo("Login completed in " + loginTime + " milliseconds");
        ExtentReportManager.logPass("Login performance is within acceptable limits");
        LOGGER.info("Test completed successfully: testLoginPerformance - Time: {}ms", loginTime);
    }
    
    /**
     * Cleanup method executed after each test method.
     */
    @AfterMethod
    public void tearDown() {
        LOGGER.info("Cleaning up test environment");
        
        // Logout if user is logged in
        try {
            if (loginPage.isUserLoggedIn()) {
                loginPage.logout();
                ExtentReportManager.logInfo("User logged out successfully");
            }
        } catch (Exception e) {
            LOGGER.warn("Could not logout user: {}", e.getMessage());
        }
        
        ExtentReportManager.removeTest();
    }
}
