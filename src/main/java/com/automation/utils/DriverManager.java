package com.automation.utils;

import com.automation.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * WebDriver manager class for creating and managing WebDriver instances.
 * This class uses ThreadLocal to ensure thread safety for parallel execution.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class DriverManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
    private static final ConfigReader CONFIG = ConfigReader.getInstance();
    
    /**
     * Private constructor to prevent instantiation.
     */
    private DriverManager() {
        // Utility class
    }
    
    /**
     * Gets the WebDriver instance for the current thread.
     * 
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }
    
    /**
     * Creates and initializes a WebDriver instance based on configuration.
     * 
     * @return WebDriver instance
     */
    public static WebDriver createDriver() {
        String browser = CONFIG.getBrowser().toLowerCase();
        boolean isHeadless = CONFIG.isHeadless();
        
        LOGGER.info("Creating {} driver with headless mode: {}", browser, isHeadless);
        
        WebDriver driver;
        
        switch (browser) {
            case "chrome":
                driver = createChromeDriver(isHeadless);
                break;
            case "firefox":
                driver = createFirefoxDriver(isHeadless);
                break;
            case "edge":
                driver = createEdgeDriver(isHeadless);
                break;
            default:
                LOGGER.warn("Unsupported browser: {}. Defaulting to Chrome.", browser);
                driver = createChromeDriver(isHeadless);
        }
        
        configureDriver(driver);
        DRIVER_THREAD_LOCAL.set(driver);
        
        LOGGER.info("WebDriver created successfully for browser: {}", browser);
        return driver;
    }
    
    /**
     * Creates a Chrome WebDriver instance.
     * 
     * @param isHeadless whether to run in headless mode
     * @return ChromeDriver instance
     */
    private static WebDriver createChromeDriver(final boolean isHeadless) {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        if (isHeadless) {
            options.addArguments("--headless");
        }
        
        // Common Chrome options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-translate");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments("--ignore-certificate-errors-spki-list");
        
        // Performance optimizations
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        
        // Set download preferences
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.managed_default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);
        
        return new ChromeDriver(options);
    }
    
    /**
     * Creates a Firefox WebDriver instance.
     * 
     * @param isHeadless whether to run in headless mode
     * @return FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver(final boolean isHeadless) {
        WebDriverManager.firefoxdriver().setup();
        
        FirefoxOptions options = new FirefoxOptions();
        
        if (isHeadless) {
            options.addArguments("--headless");
        }
        
        // Common Firefox options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        // Set preferences
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("media.volume_scale", "0.0");
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", 
            "application/pdf,application/octet-stream,application/x-winzip,application/x-pdf,application/pdf");
        
        return new FirefoxDriver(options);
    }
    
    /**
     * Creates an Edge WebDriver instance.
     * 
     * @param isHeadless whether to run in headless mode
     * @return EdgeDriver instance
     */
    private static WebDriver createEdgeDriver(final boolean isHeadless) {
        WebDriverManager.edgedriver().setup();
        
        EdgeOptions options = new EdgeOptions();
        
        if (isHeadless) {
            options.addArguments("--headless");
        }
        
        // Common Edge options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        
        return new EdgeDriver(options);
    }
    
    /**
     * Creates a Remote WebDriver instance for Selenium Grid.
     * 
     * @param browser the browser name
     * @param isHeadless whether to run in headless mode
     * @return RemoteWebDriver instance
     */
    public static WebDriver createRemoteDriver(final String browser, final boolean isHeadless) {
        String gridUrl = CONFIG.getProperty("grid.hub.url");
        
        if (gridUrl == null || gridUrl.isEmpty()) {
            throw new RuntimeException("Grid hub URL is not configured");
        }
        
        try {
            URL hubUrl = new URL(gridUrl);
            
            switch (browser.toLowerCase()) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (isHeadless) {
                        chromeOptions.addArguments("--headless");
                    }
                    return new RemoteWebDriver(hubUrl, chromeOptions);
                    
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (isHeadless) {
                        firefoxOptions.addArguments("--headless");
                    }
                    return new RemoteWebDriver(hubUrl, firefoxOptions);
                    
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (isHeadless) {
                        edgeOptions.addArguments("--headless");
                    }
                    return new RemoteWebDriver(hubUrl, edgeOptions);
                    
                default:
                    throw new RuntimeException("Unsupported browser for remote execution: " + browser);
            }
            
        } catch (MalformedURLException e) {
            LOGGER.error("Invalid grid hub URL: {}", gridUrl, e);
            throw new RuntimeException("Invalid grid hub URL: " + gridUrl, e);
        }
    }
    
    /**
     * Configures the WebDriver instance with timeouts and window settings.
     * 
     * @param driver the WebDriver instance to configure
     */
    private static void configureDriver(final WebDriver driver) {
        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(CONFIG.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(CONFIG.getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(CONFIG.getScriptTimeout()));
        
        // Maximize window if configured
        if (CONFIG.getBooleanProperty("browser.window.maximize", true)) {
            driver.manage().window().maximize();
        }
        
        LOGGER.debug("WebDriver configured with timeouts and window settings");
    }
    
    /**
     * Quits the WebDriver instance for the current thread.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            try {
                driver.quit();
                LOGGER.info("WebDriver quit successfully");
            } catch (Exception e) {
                LOGGER.error("Error while quitting WebDriver", e);
            } finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
    }
    
    /**
     * Closes the current browser window.
     */
    public static void closeDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            try {
                driver.close();
                LOGGER.info("WebDriver closed successfully");
            } catch (Exception e) {
                LOGGER.error("Error while closing WebDriver", e);
            }
        }
    }
    
    /**
     * Checks if a WebDriver instance exists for the current thread.
     * 
     * @return true if driver exists, false otherwise
     */
    public static boolean hasDriver() {
        return DRIVER_THREAD_LOCAL.get() != null;
    }
}
