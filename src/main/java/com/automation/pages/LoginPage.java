package com.automation.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object Model for Login Page.
 * Demonstrates best practices for page object implementation.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class LoginPage extends BasePage {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPage.class);
    
    // Page URL
    private static final String LOGIN_URL = "/login";
    
    // Web Elements using Page Factory
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "loginButton")
    private WebElement loginButton;
    
    @FindBy(id = "forgotPasswordLink")
    private WebElement forgotPasswordLink;
    
    @FindBy(id = "rememberMeCheckbox")
    private WebElement rememberMeCheckbox;
    
    @FindBy(className = "error-message")
    private WebElement errorMessage;
    
    @FindBy(className = "success-message")
    private WebElement successMessage;
    
    @FindBy(id = "logoutButton")
    private WebElement logoutButton;
    
    @FindBy(className = "welcome-message")
    private WebElement welcomeMessage;
    
    @FindBy(id = "userProfile")
    private WebElement userProfileLink;
    
    /**
     * Navigates to the login page.
     */
    public void navigateToLoginPage() {
        String loginUrl = config.getBaseUrl() + LOGIN_URL;
        driver.get(loginUrl);
        waitForPageToLoad();
        LOGGER.info("Navigated to login page: {}", loginUrl);
    }
    
    /**
     * Enters username in the username field.
     * 
     * @param username the username to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterUsername(final String username) {
        type(usernameField, username);
        LOGGER.debug("Entered username: {}", username);
        return this;
    }
    
    /**
     * Enters password in the password field.
     * 
     * @param password the password to enter
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterPassword(final String password) {
        type(passwordField, password);
        LOGGER.debug("Entered password: [HIDDEN]");
        return this;
    }
    
    /**
     * Clicks the login button.
     * 
     * @return HomePage instance if login is successful
     */
    public HomePage clickLoginButton() {
        click(loginButton);
        LOGGER.info("Clicked login button");
        waitForPageToLoad();
        return new HomePage();
    }
    
    /**
     * Performs login with username and password.
     * 
     * @param username the username
     * @param password the password
     * @return HomePage instance if login is successful
     */
    public HomePage login(final String username, final String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }
    
    /**
     * Performs login with remember me option.
     * 
     * @param username the username
     * @param password the password
     * @param rememberMe whether to check remember me
     * @return HomePage instance if login is successful
     */
    public HomePage loginWithRememberMe(final String username, final String password, final boolean rememberMe) {
        enterUsername(username);
        enterPassword(password);
        
        if (rememberMe) {
            setRememberMe(true);
        }
        
        return clickLoginButton();
    }
    
    /**
     * Sets the remember me checkbox.
     * 
     * @param check whether to check the checkbox
     * @return LoginPage instance for method chaining
     */
    public LoginPage setRememberMe(final boolean check) {
        if (check && !isSelected(rememberMeCheckbox)) {
            click(rememberMeCheckbox);
            LOGGER.debug("Checked remember me checkbox");
        } else if (!check && isSelected(rememberMeCheckbox)) {
            click(rememberMeCheckbox);
            LOGGER.debug("Unchecked remember me checkbox");
        }
        return this;
    }
    
    /**
     * Clicks the forgot password link.
     * 
     * @return ForgotPasswordPage instance
     */
    public ForgotPasswordPage clickForgotPasswordLink() {
        click(forgotPasswordLink);
        LOGGER.info("Clicked forgot password link");
        waitForPageToLoad();
        return new ForgotPasswordPage();
    }
    
    /**
     * Gets the error message text.
     * 
     * @return the error message text
     */
    public String getErrorMessage() {
        if (isDisplayed(errorMessage)) {
            String message = getText(errorMessage);
            LOGGER.debug("Retrieved error message: {}", message);
            return message;
        }
        return "";
    }
    
    /**
     * Gets the success message text.
     * 
     * @return the success message text
     */
    public String getSuccessMessage() {
        if (isDisplayed(successMessage)) {
            String message = getText(successMessage);
            LOGGER.debug("Retrieved success message: {}", message);
            return message;
        }
        return "";
    }
    
    /**
     * Checks if error message is displayed.
     * 
     * @return true if error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        boolean displayed = isDisplayed(errorMessage);
        LOGGER.debug("Error message displayed: {}", displayed);
        return displayed;
    }
    
    /**
     * Checks if success message is displayed.
     * 
     * @return true if success message is displayed, false otherwise
     */
    public boolean isSuccessMessageDisplayed() {
        boolean displayed = isDisplayed(successMessage);
        LOGGER.debug("Success message displayed: {}", displayed);
        return displayed;
    }
    
    /**
     * Checks if login button is enabled.
     * 
     * @return true if login button is enabled, false otherwise
     */
    public boolean isLoginButtonEnabled() {
        boolean enabled = isEnabled(loginButton);
        LOGGER.debug("Login button enabled: {}", enabled);
        return enabled;
    }
    
    /**
     * Checks if user is logged in by looking for logout button.
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        boolean loggedIn = isDisplayed(logoutButton);
        LOGGER.debug("User logged in: {}", loggedIn);
        return loggedIn;
    }
    
    /**
     * Gets the welcome message for logged in user.
     * 
     * @return the welcome message text
     */
    public String getWelcomeMessage() {
        if (isDisplayed(welcomeMessage)) {
            String message = getText(welcomeMessage);
            LOGGER.debug("Retrieved welcome message: {}", message);
            return message;
        }
        return "";
    }
    
    /**
     * Logs out the current user.
     * 
     * @return LoginPage instance after logout
     */
    public LoginPage logout() {
        if (isDisplayed(logoutButton)) {
            click(logoutButton);
            LOGGER.info("Clicked logout button");
            waitForPageToLoad();
        }
        return this;
    }
    
    /**
     * Clicks on user profile link.
     * 
     * @return UserProfilePage instance
     */
    public UserProfilePage clickUserProfile() {
        click(userProfileLink);
        LOGGER.info("Clicked user profile link");
        waitForPageToLoad();
        return new UserProfilePage();
    }
    
    /**
     * Clears the username field.
     * 
     * @return LoginPage instance for method chaining
     */
    public LoginPage clearUsername() {
        usernameField.clear();
        LOGGER.debug("Cleared username field");
        return this;
    }
    
    /**
     * Clears the password field.
     * 
     * @return LoginPage instance for method chaining
     */
    public LoginPage clearPassword() {
        passwordField.clear();
        LOGGER.debug("Cleared password field");
        return this;
    }
    
    /**
     * Gets the current username value.
     * 
     * @return the username field value
     */
    public String getUsernameValue() {
        String value = getValue(usernameField);
        LOGGER.debug("Retrieved username value: {}", value);
        return value;
    }
    
    /**
     * Checks if remember me checkbox is selected.
     * 
     * @return true if remember me is checked, false otherwise
     */
    public boolean isRememberMeChecked() {
        boolean checked = isSelected(rememberMeCheckbox);
        LOGGER.debug("Remember me checked: {}", checked);
        return checked;
    }
    
    /**
     * Validates that the login page is loaded correctly.
     * 
     * @return true if login page is loaded, false otherwise
     */
    public boolean isLoginPageLoaded() {
        boolean loaded = isDisplayed(usernameField) && 
                        isDisplayed(passwordField) && 
                        isDisplayed(loginButton);
        LOGGER.debug("Login page loaded: {}", loaded);
        return loaded;
    }
}
