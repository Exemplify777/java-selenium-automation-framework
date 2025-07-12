package com.automation.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Page Object Model for Home Page.
 * Represents the main landing page after successful login.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class HomePage extends BasePage {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);
    
    // Web Elements using Page Factory
    @FindBy(id = "welcomeMessage")
    private WebElement welcomeMessage;
    
    @FindBy(id = "userMenu")
    private WebElement userMenu;
    
    @FindBy(id = "logoutButton")
    private WebElement logoutButton;
    
    @FindBy(id = "searchBox")
    private WebElement searchBox;
    
    @FindBy(id = "searchButton")
    private WebElement searchButton;
    
    @FindBy(className = "navigation-menu")
    private WebElement navigationMenu;
    
    @FindBy(className = "main-content")
    private WebElement mainContent;
    
    @FindBy(className = "sidebar")
    private WebElement sidebar;
    
    @FindBy(className = "footer")
    private WebElement footer;
    
    @FindBy(css = ".product-card")
    private List<WebElement> productCards;
    
    @FindBy(id = "profileLink")
    private WebElement profileLink;
    
    @FindBy(id = "settingsLink")
    private WebElement settingsLink;
    
    @FindBy(id = "notificationBell")
    private WebElement notificationBell;
    
    @FindBy(className = "notification-count")
    private WebElement notificationCount;
    
    /**
     * Gets the welcome message text.
     * 
     * @return the welcome message text
     */
    public String getWelcomeMessage() {
        String message = getText(welcomeMessage);
        LOGGER.debug("Retrieved welcome message: {}", message);
        return message;
    }
    
    /**
     * Checks if the home page is loaded correctly.
     * 
     * @return true if home page is loaded, false otherwise
     */
    public boolean isHomePageLoaded() {
        boolean loaded = isDisplayed(welcomeMessage) && 
                        isDisplayed(navigationMenu) && 
                        isDisplayed(mainContent);
        LOGGER.debug("Home page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Checks if user is logged in by verifying welcome message.
     * 
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        boolean loggedIn = isDisplayed(welcomeMessage) && isDisplayed(userMenu);
        LOGGER.debug("User logged in: {}", loggedIn);
        return loggedIn;
    }
    
    /**
     * Performs logout by clicking logout button.
     * 
     * @return LoginPage instance after logout
     */
    public LoginPage logout() {
        click(logoutButton);
        LOGGER.info("Clicked logout button");
        waitForPageToLoad();
        return new LoginPage();
    }
    
    /**
     * Performs search with given search term.
     * 
     * @param searchTerm the term to search for
     * @return SearchResultsPage instance
     */
    public SearchResultsPage search(final String searchTerm) {
        type(searchBox, searchTerm);
        click(searchButton);
        LOGGER.info("Performed search for: {}", searchTerm);
        waitForPageToLoad();
        return new SearchResultsPage();
    }
    
    /**
     * Clicks on user profile link.
     * 
     * @return UserProfilePage instance
     */
    public UserProfilePage goToProfile() {
        click(profileLink);
        LOGGER.info("Navigated to user profile");
        waitForPageToLoad();
        return new UserProfilePage();
    }
    
    /**
     * Clicks on settings link.
     * 
     * @return SettingsPage instance
     */
    public SettingsPage goToSettings() {
        click(settingsLink);
        LOGGER.info("Navigated to settings");
        waitForPageToLoad();
        return new SettingsPage();
    }
    
    /**
     * Gets the number of products displayed on the home page.
     * 
     * @return the number of product cards
     */
    public int getProductCount() {
        int count = productCards.size();
        LOGGER.debug("Found {} products on home page", count);
        return count;
    }
    
    /**
     * Clicks on a specific product by index.
     * 
     * @param index the index of the product (0-based)
     * @return ProductDetailsPage instance
     */
    public ProductDetailsPage clickProduct(final int index) {
        if (index < 0 || index >= productCards.size()) {
            throw new IndexOutOfBoundsException("Product index " + index + " is out of bounds");
        }
        
        WebElement product = productCards.get(index);
        click(product);
        LOGGER.info("Clicked on product at index: {}", index);
        waitForPageToLoad();
        return new ProductDetailsPage();
    }
    
    /**
     * Gets the notification count.
     * 
     * @return the notification count as integer
     */
    public int getNotificationCount() {
        if (isDisplayed(notificationCount)) {
            String countText = getText(notificationCount);
            try {
                int count = Integer.parseInt(countText);
                LOGGER.debug("Notification count: {}", count);
                return count;
            } catch (NumberFormatException e) {
                LOGGER.warn("Could not parse notification count: {}", countText);
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Clicks on notification bell.
     * 
     * @return NotificationsPage instance
     */
    public NotificationsPage clickNotifications() {
        click(notificationBell);
        LOGGER.info("Clicked on notifications");
        waitForPageToLoad();
        return new NotificationsPage();
    }
    
    /**
     * Checks if notifications are available.
     * 
     * @return true if notifications are available, false otherwise
     */
    public boolean hasNotifications() {
        return getNotificationCount() > 0;
    }
    
    /**
     * Gets the page title.
     * 
     * @return the page title
     */
    public String getHomePageTitle() {
        String title = getPageTitle();
        LOGGER.debug("Home page title: {}", title);
        return title;
    }
    
    /**
     * Validates the home page layout.
     * 
     * @return true if all main elements are present, false otherwise
     */
    public boolean validatePageLayout() {
        boolean valid = isDisplayed(navigationMenu) && 
                       isDisplayed(mainContent) && 
                       isDisplayed(sidebar) && 
                       isDisplayed(footer);
        LOGGER.debug("Home page layout valid: {}", valid);
        return valid;
    }
}

// Placeholder classes for other pages referenced in HomePage
class SearchResultsPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultsPage.class);
}

class UserProfilePage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfilePage.class);
}

class SettingsPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsPage.class);
}

class ProductDetailsPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailsPage.class);
}

class NotificationsPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationsPage.class);
}

class ForgotPasswordPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordPage.class);
}
