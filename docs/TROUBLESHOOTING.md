# Troubleshooting Guide

This guide helps you resolve common issues when using the Java Selenium Automation Framework.

## 🔧 Environment Setup Issues

### Java Issues

#### Problem: `java: command not found`
**Solution:**
```bash
# Install Java 11 or higher
# Ubuntu/Debian
sudo apt update && sudo apt install openjdk-11-jdk

# macOS (using Homebrew)
brew install openjdk@11

# Windows - Download from Oracle or use Chocolatey
choco install openjdk11
```

#### Problem: Wrong Java version
**Solution:**
```bash
# Check current version
java -version

# Set JAVA_HOME (Linux/macOS)
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Maven Issues

#### Problem: `mvn: command not found`
**Solution:**
```bash
# Install Maven
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# Windows
choco install maven
```

#### Problem: Maven dependency resolution fails
**Solution:**
```bash
# Clear local repository
rm -rf ~/.m2/repository

# Force update dependencies
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

## 🌐 Browser Issues

### Chrome Issues

#### Problem: ChromeDriver version mismatch
**Solution:**
```bash
# Check Chrome version
google-chrome --version

# WebDriverManager handles this automatically, but if issues persist:
# Clear WebDriver cache
rm -rf ~/.cache/selenium

# Update dependencies
mvn clean install -U
```

#### Problem: Chrome crashes in headless mode
**Solution:**
Add these Chrome options in `DriverManager.java`:
```java
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
options.addArguments("--disable-gpu");
options.addArguments("--remote-debugging-port=9222");
```

### Firefox Issues

#### Problem: Firefox not found
**Solution:**
```bash
# Install Firefox
# Ubuntu/Debian
sudo apt install firefox

# macOS
brew install --cask firefox

# Windows
choco install firefox
```

#### Problem: GeckoDriver issues
**Solution:**
WebDriverManager handles this automatically. If issues persist:
```bash
# Manually download GeckoDriver
wget https://github.com/mozilla/geckodriver/releases/latest/download/geckodriver-linux64.tar.gz
tar -xzf geckodriver-linux64.tar.gz
sudo mv geckodriver /usr/local/bin/
```

## 🐳 Docker Issues

### Docker Installation

#### Problem: Docker not installed
**Solution:**
```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# macOS
brew install --cask docker

# Windows - Download Docker Desktop
```

### Docker Execution Issues

#### Problem: Permission denied
**Solution:**
```bash
# Add user to docker group (Linux)
sudo usermod -aG docker $USER
newgrp docker

# Or run with sudo
sudo docker-compose up
```

#### Problem: Port already in use
**Solution:**
```bash
# Check what's using the port
lsof -i :4444

# Kill the process
sudo kill -9 <PID>

# Or change port in docker-compose.yml
```

#### Problem: Container fails to start
**Solution:**
```bash
# Check logs
docker-compose logs

# Clean up and rebuild
docker-compose down
docker system prune -a
docker-compose up --build
```

## 🧪 Test Execution Issues

### Test Failures

#### Problem: Element not found
**Symptoms:**
- `NoSuchElementException`
- `ElementNotInteractableException`

**Solutions:**
1. **Increase wait times:**
```properties
explicit.wait.timeout=20
browser.implicit.wait=15
```

2. **Use explicit waits:**
```java
waitUtils.waitForElementToBeVisible(element);
waitUtils.waitForElementToBeClickable(element);
```

3. **Check element locators:**
```java
// Use more robust locators
@FindBy(css = "[data-testid='login-button']")
@FindBy(xpath = "//button[contains(text(), 'Login')]")
```

#### Problem: Stale element reference
**Solution:**
```java
// Re-find the element
public void clickElement(By locator) {
    WebElement element = driver.findElement(locator);
    element.click();
}
```

#### Problem: Tests pass locally but fail in CI
**Solutions:**
1. **Use headless mode in CI:**
```bash
mvn test -Dheadless=true
```

2. **Increase timeouts for CI:**
```properties
# ci.properties
explicit.wait.timeout=30
browser.page.load.timeout=60
```

3. **Add retry mechanism:**
```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testMethod() {
    // test code
}
```

### Parallel Execution Issues

#### Problem: Tests interfere with each other
**Solutions:**
1. **Use ThreadLocal for WebDriver:**
```java
private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
```

2. **Isolate test data:**
```java
// Use unique test data per thread
String username = "user_" + Thread.currentThread().getId();
```

3. **Avoid shared resources:**
```java
// Don't share static variables between tests
```

## 📊 Reporting Issues

### Extent Reports

#### Problem: Reports not generated
**Solution:**
```bash
# Check if reports directory exists
mkdir -p target/reports

# Verify ExtentReportManager is initialized
# Check logs for initialization messages
```

#### Problem: Screenshots not attached
**Solution:**
```java
// Ensure screenshot path is correct
String screenshotPath = ScreenshotUtils.takeScreenshot(driver, testName);
ExtentReportManager.attachScreenshot(screenshotPath, "Test Screenshot");
```

### Allure Reports

#### Problem: Allure command not found
**Solution:**
```bash
# Install Allure
# macOS
brew install allure

# Windows
choco install allure

# Linux
wget https://github.com/allure-framework/allure2/releases/download/2.24.0/allure-2.24.0.tgz
tar -xzf allure-2.24.0.tgz
sudo mv allure-2.24.0 /opt/allure
export PATH=/opt/allure/bin:$PATH
```

## 🔍 Debugging Tips

### Enable Debug Logging

Add to `logback-test.xml`:
```xml
<logger name="com.automation" level="DEBUG"/>
<logger name="org.openqa.selenium" level="DEBUG"/>
```

### Take Screenshots for Debugging

```java
// Add to test methods
ScreenshotUtils.takeScreenshot(driver, "debug_screenshot");
```

### Use Browser Developer Tools

```java
// Enable Chrome DevTools
ChromeOptions options = new ChromeOptions();
options.addArguments("--auto-open-devtools-for-tabs");
```

### Remote Debugging

```java
// Add remote debugging port
options.addArguments("--remote-debugging-port=9222");
// Access via http://localhost:9222
```

## 🚨 Common Error Messages

### `SessionNotCreatedException`
**Cause:** Browser/driver version mismatch
**Solution:** Update browser or use WebDriverManager

### `TimeoutException`
**Cause:** Element not found within timeout
**Solution:** Increase wait times or fix locators

### `WebDriverException: unknown error: Chrome failed to start`
**Cause:** Chrome installation issues
**Solution:** Reinstall Chrome or use different browser

### `java.lang.OutOfMemoryError`
**Cause:** Insufficient memory for parallel execution
**Solution:** 
```bash
export MAVEN_OPTS="-Xmx2g -Xms1g"
mvn test
```

## 📞 Getting Help

### Log Analysis

1. **Check application logs:**
```bash
tail -f logs/automation.log
```

2. **Check Maven output:**
```bash
mvn test -X  # Debug mode
```

3. **Check browser logs:**
```java
LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
```

### Reporting Issues

When reporting issues, include:

1. **Environment information:**
   - OS version
   - Java version
   - Browser version
   - Framework version

2. **Error details:**
   - Full stack trace
   - Test configuration
   - Steps to reproduce

3. **Logs:**
   - Application logs
   - Browser console logs
   - Screenshots

### Community Support

- **GitHub Issues:** Report bugs and feature requests
- **Stack Overflow:** Tag questions with `selenium-java`
- **Selenium Documentation:** https://selenium.dev/documentation/

## 🔄 Performance Optimization

### Reduce Test Execution Time

1. **Use headless browsers:**
```properties
headless=true
```

2. **Optimize waits:**
```java
// Use explicit waits instead of Thread.sleep()
waitUtils.waitForElementToBeVisible(element);
```

3. **Parallel execution:**
```xml
<suite parallel="methods" thread-count="4">
```

4. **Disable images and CSS:**
```java
// Chrome
Map<String, Object> prefs = new HashMap<>();
prefs.put("profile.managed_default_content_settings.images", 2);
options.setExperimentalOption("prefs", prefs);
```

### Memory Optimization

```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx4g -Xms2g"

# Garbage collection tuning
export MAVEN_OPTS="$MAVEN_OPTS -XX:+UseG1GC"
```

---

**Still having issues?** Open a [GitHub issue](https://github.com/Exemplify777/java-selenium-automation-framework/issues) with detailed information.
