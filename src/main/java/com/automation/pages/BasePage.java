package com.automation.pages;

import com.automation.config.ConfigReader;
import com.automation.utils.DriverManager;
import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Base page class containing common web element interactions and utilities.
 * All page objects should extend this class to inherit common functionality.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public abstract class BasePage {
    
    protected static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;
    protected final ConfigReader config;
    
    /**
     * Constructor for BasePage.
     */
    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.waitUtils = new WaitUtils(driver);
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
        this.config = ConfigReader.getInstance();
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Clicks on an element.
     * 
     * @param element the WebElement to click
     */
    protected void click(final WebElement element) {
        waitUtils.waitForElementToBeClickable(element);
        element.click();
        LOGGER.debug("Clicked on element: {}", element);
    }
    
    /**
     * Clicks on an element using JavaScript.
     * 
     * @param element the WebElement to click
     */
    protected void clickUsingJS(final WebElement element) {
        waitUtils.waitForElementToBeVisible(element);
        jsExecutor.executeScript("arguments[0].click();", element);
        LOGGER.debug("Clicked on element using JavaScript: {}", element);
    }
    
    /**
     * Types text into an input field.
     * 
     * @param element the WebElement to type into
     * @param text the text to type
     */
    protected void type(final WebElement element, final String text) {
        waitUtils.waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
        LOGGER.debug("Typed '{}' into element: {}", text, element);
    }
    
    /**
     * Types text into an input field using JavaScript.
     * 
     * @param element the WebElement to type into
     * @param text the text to type
     */
    protected void typeUsingJS(final WebElement element, final String text) {
        waitUtils.waitForElementToBeVisible(element);
        jsExecutor.executeScript("arguments[0].value = arguments[1];", element, text);
        LOGGER.debug("Typed '{}' into element using JavaScript: {}", text, element);
    }
    
    /**
     * Gets the text of an element.
     * 
     * @param element the WebElement to get text from
     * @return the text of the element
     */
    protected String getText(final WebElement element) {
        waitUtils.waitForElementToBeVisible(element);
        String text = element.getText();
        LOGGER.debug("Got text '{}' from element: {}", text, element);
        return text;
    }
    
    /**
     * Gets the value attribute of an element.
     * 
     * @param element the WebElement to get value from
     * @return the value attribute of the element
     */
    protected String getValue(final WebElement element) {
        waitUtils.waitForElementToBeVisible(element);
        String value = element.getAttribute("value");
        LOGGER.debug("Got value '{}' from element: {}", value, element);
        return value;
    }
    
    /**
     * Gets an attribute value of an element.
     * 
     * @param element the WebElement to get attribute from
     * @param attributeName the name of the attribute
     * @return the attribute value
     */
    protected String getAttribute(final WebElement element, final String attributeName) {
        waitUtils.waitForElementToBeVisible(element);
        String attributeValue = element.getAttribute(attributeName);
        LOGGER.debug("Got attribute '{}' value '{}' from element: {}", attributeName, attributeValue, element);
        return attributeValue;
    }
    
    /**
     * Checks if an element is displayed.
     * 
     * @param element the WebElement to check
     * @return true if element is displayed, false otherwise
     */
    protected boolean isDisplayed(final WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            LOGGER.debug("Element is not displayed: {}", element);
            return false;
        }
    }
    
    /**
     * Checks if an element is enabled.
     * 
     * @param element the WebElement to check
     * @return true if element is enabled, false otherwise
     */
    protected boolean isEnabled(final WebElement element) {
        try {
            return element.isEnabled();
        } catch (Exception e) {
            LOGGER.debug("Element is not enabled: {}", element);
            return false;
        }
    }
    
    /**
     * Checks if an element is selected.
     * 
     * @param element the WebElement to check
     * @return true if element is selected, false otherwise
     */
    protected boolean isSelected(final WebElement element) {
        try {
            return element.isSelected();
        } catch (Exception e) {
            LOGGER.debug("Element is not selected: {}", element);
            return false;
        }
    }
    
    /**
     * Selects an option from a dropdown by visible text.
     * 
     * @param element the dropdown WebElement
     * @param text the visible text to select
     */
    protected void selectByText(final WebElement element, final String text) {
        waitUtils.waitForElementToBeVisible(element);
        Select select = new Select(element);
        select.selectByVisibleText(text);
        LOGGER.debug("Selected option '{}' from dropdown: {}", text, element);
    }
    
    /**
     * Selects an option from a dropdown by value.
     * 
     * @param element the dropdown WebElement
     * @param value the value to select
     */
    protected void selectByValue(final WebElement element, final String value) {
        waitUtils.waitForElementToBeVisible(element);
        Select select = new Select(element);
        select.selectByValue(value);
        LOGGER.debug("Selected option with value '{}' from dropdown: {}", value, element);
    }
    
    /**
     * Selects an option from a dropdown by index.
     * 
     * @param element the dropdown WebElement
     * @param index the index to select
     */
    protected void selectByIndex(final WebElement element, final int index) {
        waitUtils.waitForElementToBeVisible(element);
        Select select = new Select(element);
        select.selectByIndex(index);
        LOGGER.debug("Selected option at index '{}' from dropdown: {}", index, element);
    }
    
    /**
     * Hovers over an element.
     * 
     * @param element the WebElement to hover over
     */
    protected void hover(final WebElement element) {
        waitUtils.waitForElementToBeVisible(element);
        actions.moveToElement(element).perform();
        LOGGER.debug("Hovered over element: {}", element);
    }
    
    /**
     * Double clicks on an element.
     * 
     * @param element the WebElement to double click
     */
    protected void doubleClick(final WebElement element) {
        waitUtils.waitForElementToBeClickable(element);
        actions.doubleClick(element).perform();
        LOGGER.debug("Double clicked on element: {}", element);
    }
    
    /**
     * Right clicks on an element.
     * 
     * @param element the WebElement to right click
     */
    protected void rightClick(final WebElement element) {
        waitUtils.waitForElementToBeClickable(element);
        actions.contextClick(element).perform();
        LOGGER.debug("Right clicked on element: {}", element);
    }
    
    /**
     * Scrolls to an element.
     * 
     * @param element the WebElement to scroll to
     */
    protected void scrollToElement(final WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
        LOGGER.debug("Scrolled to element: {}", element);
    }
    
    /**
     * Scrolls to the top of the page.
     */
    protected void scrollToTop() {
        jsExecutor.executeScript("window.scrollTo(0, 0);");
        LOGGER.debug("Scrolled to top of page");
    }
    
    /**
     * Scrolls to the bottom of the page.
     */
    protected void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        LOGGER.debug("Scrolled to bottom of page");
    }
    
    /**
     * Gets the current page title.
     *
     * @return the page title
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        LOGGER.debug("Page title: {}", title);
        return title;
    }
    
    /**
     * Gets the current page URL.
     * 
     * @return the page URL
     */
    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        LOGGER.debug("Current URL: {}", url);
        return url;
    }
    
    /**
     * Refreshes the current page.
     */
    protected void refreshPage() {
        driver.navigate().refresh();
        LOGGER.debug("Page refreshed");
    }
    
    /**
     * Navigates back in browser history.
     */
    protected void navigateBack() {
        driver.navigate().back();
        LOGGER.debug("Navigated back");
    }
    
    /**
     * Navigates forward in browser history.
     */
    protected void navigateForward() {
        driver.navigate().forward();
        LOGGER.debug("Navigated forward");
    }
    
    /**
     * Finds an element by locator.
     * 
     * @param locator the By locator
     * @return the WebElement
     */
    protected WebElement findElement(final By locator) {
        return waitUtils.waitForElementToBeVisible(locator);
    }
    
    /**
     * Finds multiple elements by locator.
     * 
     * @param locator the By locator
     * @return list of WebElements
     */
    protected List<WebElement> findElements(final By locator) {
        return waitUtils.waitForElementsToBeVisible(locator);
    }
    
    /**
     * Waits for page to load completely.
     */
    protected void waitForPageToLoad() {
        waitUtils.waitForPageToLoad();
    }
    
    /**
     * Switches to a frame by index.
     * 
     * @param index the frame index
     */
    protected void switchToFrame(final int index) {
        driver.switchTo().frame(index);
        LOGGER.debug("Switched to frame at index: {}", index);
    }
    
    /**
     * Switches to a frame by WebElement.
     * 
     * @param frameElement the frame WebElement
     */
    protected void switchToFrame(final WebElement frameElement) {
        driver.switchTo().frame(frameElement);
        LOGGER.debug("Switched to frame: {}", frameElement);
    }
    
    /**
     * Switches to the default content.
     */
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        LOGGER.debug("Switched to default content");
    }
}
