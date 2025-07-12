package com.automation.utils;

import com.automation.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Utility class for WebDriver waits and synchronization.
 * Provides various wait methods for different scenarios.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class WaitUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WaitUtils.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final FluentWait<WebDriver> fluentWait;
    private final ConfigReader config;
    
    /**
     * Constructor for WaitUtils.
     * 
     * @param driver the WebDriver instance
     */
    public WaitUtils(final WebDriver driver) {
        this.driver = driver;
        this.config = ConfigReader.getInstance();
        
        int explicitWaitTimeout = config.getExplicitWaitTimeout();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitTimeout));
        
        int fluentWaitTimeout = config.getIntProperty("fluent.wait.timeout", 20);
        int pollingInterval = config.getIntProperty("fluent.wait.polling", 2);
        
        this.fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(fluentWaitTimeout))
                .pollingEvery(Duration.ofSeconds(pollingInterval))
                .ignoring(NoSuchElementException.class);
    }
    
    /**
     * Waits for an element to be visible.
     * 
     * @param element the WebElement to wait for
     * @return the WebElement once it's visible
     */
    public WebElement waitForElementToBeVisible(final WebElement element) {
        try {
            return wait.until(ExpectedConditions.visibilityOf(element));
        } catch (Exception e) {
            LOGGER.error("Element not visible within timeout: {}", element, e);
            throw e;
        }
    }
    
    /**
     * Waits for an element to be visible by locator.
     * 
     * @param locator the By locator
     * @return the WebElement once it's visible
     */
    public WebElement waitForElementToBeVisible(final By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            LOGGER.error("Element not visible within timeout: {}", locator, e);
            throw e;
        }
    }
    
    /**
     * Waits for multiple elements to be visible.
     * 
     * @param locator the By locator
     * @return list of WebElements once they're visible
     */
    public List<WebElement> waitForElementsToBeVisible(final By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            LOGGER.error("Elements not visible within timeout: {}", locator, e);
            throw e;
        }
    }
    
    /**
     * Waits for an element to be clickable.
     * 
     * @param element the WebElement to wait for
     * @return the WebElement once it's clickable
     */
    public WebElement waitForElementToBeClickable(final WebElement element) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            LOGGER.error("Element not clickable within timeout: {}", element, e);
            throw e;
        }
    }
    
    /**
     * Waits for an element to be clickable by locator.
     * 
     * @param locator the By locator
     * @return the WebElement once it's clickable
     */
    public WebElement waitForElementToBeClickable(final By locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            LOGGER.error("Element not clickable within timeout: {}", locator, e);
            throw e;
        }
    }
    
    /**
     * Waits for an element to be present in DOM.
     * 
     * @param locator the By locator
     * @return the WebElement once it's present
     */
    public WebElement waitForElementToBePresent(final By locator) {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            LOGGER.error("Element not present within timeout: {}", locator, e);
            throw e;
        }
    }
    
    /**
     * Waits for an element to disappear.
     * 
     * @param locator the By locator
     * @return true if element disappears, false otherwise
     */
    public boolean waitForElementToDisappear(final By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            LOGGER.error("Element did not disappear within timeout: {}", locator, e);
            return false;
        }
    }
    
    /**
     * Waits for text to be present in an element.
     * 
     * @param element the WebElement
     * @param text the text to wait for
     * @return true if text is present, false otherwise
     */
    public boolean waitForTextToBePresentInElement(final WebElement element, final String text) {
        try {
            return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception e) {
            LOGGER.error("Text '{}' not present in element within timeout: {}", text, element, e);
            return false;
        }
    }
    
    /**
     * Waits for an attribute to contain a specific value.
     * 
     * @param element the WebElement
     * @param attribute the attribute name
     * @param value the value to wait for
     * @return true if attribute contains value, false otherwise
     */
    public boolean waitForAttributeToContain(final WebElement element, final String attribute, final String value) {
        try {
            return wait.until(ExpectedConditions.attributeContains(element, attribute, value));
        } catch (Exception e) {
            LOGGER.error("Attribute '{}' does not contain value '{}' within timeout: {}", attribute, value, element, e);
            return false;
        }
    }
    
    /**
     * Waits for page to load completely.
     */
    public void waitForPageToLoad() {
        try {
            wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").equals("complete"));
            LOGGER.debug("Page loaded completely");
        } catch (Exception e) {
            LOGGER.error("Page did not load completely within timeout", e);
        }
    }
    
    /**
     * Waits for jQuery to complete (if jQuery is present).
     */
    public void waitForJQueryToComplete() {
        try {
            wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return jQuery.active == 0"));
            LOGGER.debug("jQuery completed");
        } catch (Exception e) {
            LOGGER.debug("jQuery not present or did not complete within timeout");
        }
    }
    
    /**
     * Waits for Angular to complete (if Angular is present).
     */
    public void waitForAngularToComplete() {
        try {
            wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1"));
            LOGGER.debug("Angular completed");
        } catch (Exception e) {
            LOGGER.debug("Angular not present or did not complete within timeout");
        }
    }
    
    /**
     * Custom wait with fluent wait.
     * 
     * @param condition the condition to wait for
     * @param <T> the return type
     * @return the result of the condition
     */
    public <T> T waitWithFluentWait(final java.util.function.Function<WebDriver, T> condition) {
        try {
            return fluentWait.until(condition);
        } catch (Exception e) {
            LOGGER.error("Fluent wait condition not met within timeout", e);
            throw e;
        }
    }
    
    /**
     * Waits for a specific amount of time (thread sleep).
     * Use sparingly and prefer explicit waits.
     * 
     * @param seconds the number of seconds to wait
     */
    public void waitForSeconds(final int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            LOGGER.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Wait interrupted", e);
        }
    }
}
