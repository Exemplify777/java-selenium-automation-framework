# Java Selenium Automation Framework

[![Build Status](https://github.com/Exemplify777/java-selenium-automation-framework/workflows/Selenium%20Test%20Automation/badge.svg)](https://github.com/Exemplify777/java-selenium-automation-framework/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-11+-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

Selenium test automation framework template using Java with Page Object Model, TestNG, and extensive reporting capabilities.

## 🚀 Features

- **Page Object Model (POM)** design pattern for maintainable test code
- **TestNG** framework with parallel execution support
- **Multiple Browser Support** (Chrome, Firefox, Edge) with headless capabilities
- **Comprehensive Reporting** with Extent Reports and Allure integration
- **Data-Driven Testing** with JSON, CSV, and Excel support
- **Docker Support** for containerized test execution
- **CI/CD Integration** with GitHub Actions
- **Code Quality Checks** with Checkstyle, PMD, and SpotBugs
- **Cross-Platform Compatibility** (Windows, macOS, Linux)
- **Selenium Grid Support** for distributed test execution
- **Screenshot Capture** on test failures
- **Comprehensive Logging** with SLF4J and Logback
- **Configuration Management** for multiple environments

## 📋 Prerequisites

- **Java 11** or higher
- **Maven 3.6** or higher
- **Git** for version control
- **Chrome/Firefox/Edge** browsers (for local execution)
- **Docker** (optional, for containerized execution)

## 🛠️ Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/Exemplify777/java-selenium-automation-framework.git
cd java-selenium-automation-framework
```

### 2. Install Dependencies

```bash
mvn clean install
```

### 3. Validate Framework Setup

Run the framework validation tests to ensure everything is working correctly:

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-validation.xml -Djacoco.skip=true
```

This will run comprehensive validation tests that check:
- Configuration management
- JSON and CSV data readers
- Assertion utilities
- Extent report generation
- Screenshot utilities
- Overall framework integration

### 4. Install Git Hooks (Optional but Recommended)

```bash
./scripts/install-hooks.sh
```

### 4. Run Tests

```bash
# Run smoke tests
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml

# Run all tests
mvn test

# Run tests with specific browser
mvn test -Dbrowser=firefox

# Run tests in headless mode
mvn test -Dheadless=true

# Run tests for specific environment
mvn test -Denvironment=staging
```

## 📁 Project Structure

```
java-selenium-automation-framework/
├── src/
│   ├── main/java/com/automation/
│   │   ├── config/           # Configuration management
│   │   ├── pages/            # Page Object Model classes
│   │   └── utils/            # Utility classes
│   └── test/
│       ├── java/com/automation/
│       │   ├── tests/        # Test classes
│       │   └── data/         # Test data classes
│       └── resources/
│           ├── config/       # Environment configurations
│           ├── testdata/     # Test data files
│           └── testng*.xml   # TestNG suite files
├── target/
│   ├── reports/             # Test reports
│   ├── screenshots/         # Screenshots
│   └── allure-results/      # Allure results
├── scripts/                 # Helper scripts
├── .github/workflows/       # GitHub Actions workflows
├── docker-compose.yml       # Docker configuration
├── Dockerfile              # Docker image definition
└── README.md               # This file
```

## 🔧 Configuration

### Environment Configuration

The framework supports multiple environments through property files:

- `src/test/resources/config/dev.properties`
- `src/test/resources/config/staging.properties`
- `src/test/resources/config/prod.properties`

### Key Configuration Properties

```properties
# Application URL
base.url=https://example.com

# Browser settings
browser=chrome
headless=false
browser.window.maximize=true

# Timeouts
browser.implicit.wait=10
browser.page.load.timeout=30
explicit.wait.timeout=15

# Parallel execution
parallel.tests=true
thread.count=3

# Reporting
reports.path=target/reports
screenshots.path=target/screenshots
screenshot.on.failure=true
```

## 🧪 Writing Tests

### Example Test Class

```java
@Listeners(TestListener.class)
public class LoginTest extends BaseTest {
    
    @Test(description = "Verify successful login")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.navigateToLoginPage();
        
        HomePage homePage = loginPage.login("username", "password");
        
        AssertionUtils.assertTrue(homePage.isUserLoggedIn(), 
            "User should be logged in successfully");
    }
}
```

### Example Page Object

```java
public class LoginPage extends BasePage {
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "loginButton")
    private WebElement loginButton;
    
    public HomePage login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
        return new HomePage();
    }
}
```

## 🐳 Docker Support

### Run Tests with Docker

```bash
# Build and run tests locally
./scripts/docker-run.sh local

# Run tests with Selenium Grid
./scripts/docker-run.sh grid

# Run smoke tests
./scripts/docker-run.sh smoke

# Run tests in debug mode (VNC available on port 5900)
./scripts/docker-run.sh debug
```

### Docker Compose

```bash
# Run with Selenium Grid
docker-compose up --build

# Run locally without Grid
docker-compose -f docker-compose.local.yml up --build
```

## 📊 Reporting

### Extent Reports

HTML reports are generated in `target/reports/` directory with:
- Test execution summary
- Screenshots on failures
- Detailed test steps
- Environment information

### Allure Reports

Generate Allure reports:

```bash
mvn allure:report
mvn allure:serve
```

## 🔄 CI/CD Integration

### GitHub Actions

The framework includes pre-configured workflows:

- **test-automation.yml**: Main test execution workflow
- **nightly-tests.yml**: Comprehensive nightly test execution
- **pr-validation.yml**: Pull request validation with code quality checks

### Running Tests in CI

```bash
# Trigger workflow manually
gh workflow run test-automation.yml -f test_suite=smoke -f browser=chrome

# View workflow status
gh run list
```

## 🧹 Code Quality

### Pre-commit Hooks

Install Git hooks for automatic code quality checks:

```bash
./scripts/install-hooks.sh
```

### Manual Code Quality Checks

```bash
# Checkstyle
mvn checkstyle:check

# PMD
mvn pmd:check

# SpotBugs
mvn spotbugs:check

# All quality checks
mvn verify
```

## 📈 Parallel Execution

### TestNG Parallel Execution

Configure parallel execution in `testng.xml`:

```xml
<suite name="Parallel Tests" parallel="methods" thread-count="3">
    <!-- Test configuration -->
</suite>
```

### Running Parallel Tests

```bash
# Run tests in parallel
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-parallel.xml

# Specify thread count
mvn test -Dthread.count=5
```

## 🔍 Debugging

### Local Debugging

1. Set `headless=false` in configuration
2. Add breakpoints in your IDE
3. Run tests in debug mode

### Remote Debugging with Docker

```bash
# Start debug environment
./scripts/docker-run.sh debug

# Connect VNC viewer to localhost:5900
```

## 🌐 Cross-Browser Testing

### Supported Browsers

- **Chrome** (default)
- **Firefox**
- **Microsoft Edge**

### Browser Configuration

```bash
# Run with specific browser
mvn test -Dbrowser=firefox

# Run with multiple browsers (CI)
mvn test -Dbrowser=chrome,firefox,edge
```

## 📚 Best Practices

### Test Organization

1. **Use Page Object Model** for UI interactions
2. **Implement data-driven testing** for multiple test scenarios
3. **Use meaningful test names** and descriptions
4. **Group tests logically** using TestNG groups
5. **Implement proper assertions** with custom assertion utilities

### Code Quality

1. **Follow coding standards** enforced by Checkstyle
2. **Write clean, readable code** with proper comments
3. **Use dependency injection** where appropriate
4. **Implement proper exception handling**
5. **Follow SOLID principles**

### Test Data Management

1. **Externalize test data** in JSON/CSV/Excel files
2. **Use environment-specific data** when needed
3. **Avoid hardcoded values** in test code
4. **Implement data factories** for complex objects

## 🚨 Troubleshooting

### Common Issues

#### WebDriver Issues

```bash
# Clear WebDriver cache
rm -rf ~/.cache/selenium

# Update WebDriverManager
mvn dependency:resolve
```

#### Browser Issues

```bash
# Update browser to latest version
# Chrome: chrome://settings/help
# Firefox: about:support

# Verify browser installation
google-chrome --version
firefox --version
```

#### Maven Issues

```bash
# Clear Maven cache
mvn dependency:purge-local-repository

# Reload dependencies
mvn clean install -U
```

#### Docker Issues

```bash
# Clean Docker environment
docker system prune -a

# Rebuild images
docker-compose build --no-cache
```

### Getting Help

1. **Check the logs** in `logs/` directory
2. **Review test reports** in `target/reports/`
3. **Check screenshots** in `target/screenshots/`
4. **Consult the troubleshooting guide** in `docs/TROUBLESHOOTING.md`
5. **Open an issue** on GitHub with detailed information

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Commit Message Format

Follow conventional commit format:
```
type(scope): description

feat(login): add remember me functionality
fix(driver): resolve browser initialization issue
docs: update README with new examples
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Selenium WebDriver](https://selenium.dev/) for browser automation
- [TestNG](https://testng.org/) for test framework
- [Extent Reports](https://extentreports.com/) for reporting
- [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) for driver management
- [Allure](https://docs.qameta.io/allure/) for advanced reporting

## 📞 Support

For support and questions:
- 🐛 Issues: [GitHub Issues](https://github.com/Exemplify777/java-selenium-automation-framework/issues)

---

**Happy Testing! 🎉**
