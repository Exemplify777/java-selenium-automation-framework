# Contributing to Java Selenium Automation Framework

Thank you for your interest in contributing to the Java Selenium Automation Framework! This document provides guidelines and information for contributors.

## 🤝 How to Contribute

### Reporting Issues

Before creating an issue, please:

1. **Search existing issues** to avoid duplicates
2. **Use the issue templates** provided
3. **Provide detailed information** including:
   - Environment details (OS, Java version, browser version)
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots or logs if applicable

### Suggesting Features

We welcome feature suggestions! Please:

1. **Check existing feature requests** first
2. **Describe the use case** clearly
3. **Explain the benefits** to the framework
4. **Consider implementation complexity**

### Code Contributions

#### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Git
- IDE with Java support (IntelliJ IDEA, Eclipse, VS Code)

#### Development Setup

1. **Fork the repository**
```bash
git clone https://github.com/Exemplify777/java-selenium-automation-framework.git
cd java-selenium-automation-framework
```

2. **Install dependencies**
```bash
mvn clean install
```

3. **Install Git hooks**
```bash
./scripts/install-hooks.sh
```

4. **Create a feature branch**
```bash
git checkout -b feature/your-feature-name
```

#### Code Standards

##### Coding Style

- Follow **Google Java Style Guide**
- Use **meaningful variable and method names**
- Write **comprehensive JavaDoc** for public methods
- Keep **methods small and focused** (max 20-30 lines)
- Use **proper exception handling**

##### Code Quality

All code must pass:
- **Checkstyle** validation
- **PMD** analysis
- **SpotBugs** checks
- **Unit tests** (where applicable)

Run quality checks:
```bash
mvn verify
```

##### Testing Guidelines

1. **Write tests for new features**
2. **Follow existing test patterns**
3. **Use meaningful test names**
4. **Include both positive and negative test cases**
5. **Use data-driven testing** where appropriate

Example test structure:
```java
@Test(description = "Verify user can login with valid credentials", 
      groups = {"smoke", "login"})
public void testValidLogin() {
    // Given
    LoginPage loginPage = new LoginPage();
    loginPage.navigateToLoginPage();
    
    // When
    HomePage homePage = loginPage.login("validUser", "validPassword");
    
    // Then
    AssertionUtils.assertTrue(homePage.isUserLoggedIn(), 
        "User should be logged in successfully");
}
```

#### Commit Guidelines

##### Commit Message Format

Use **Conventional Commits** format:

```
type(scope): description

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Build process or auxiliary tool changes

**Examples:**
```
feat(login): add remember me functionality
fix(driver): resolve browser initialization issue
docs: update README with Docker instructions
test(login): add negative test cases
```

##### Commit Best Practices

- **Make atomic commits** (one logical change per commit)
- **Write clear commit messages**
- **Keep commits small and focused**
- **Test before committing**

#### Pull Request Process

1. **Ensure your branch is up to date**
```bash
git checkout main
git pull upstream main
git checkout feature/your-feature-name
git rebase main
```

2. **Run all tests and quality checks**
```bash
mvn clean verify
```

3. **Create a pull request** with:
   - Clear title and description
   - Reference to related issues
   - Screenshots (if UI changes)
   - Testing instructions

4. **Respond to review feedback** promptly

5. **Squash commits** if requested

#### Code Review Guidelines

##### For Authors

- **Self-review** your code before submitting
- **Write clear PR descriptions**
- **Respond to feedback** constructively
- **Keep PRs small** and focused

##### For Reviewers

- **Be constructive** and respectful
- **Focus on code quality** and maintainability
- **Test the changes** locally if needed
- **Approve when satisfied** with the quality

## 📁 Project Structure Guidelines

### Package Organization

```
com.automation/
├── config/          # Configuration management
├── pages/           # Page Object Model classes
│   ├── common/      # Common page components
│   └── modules/     # Feature-specific pages
├── utils/           # Utility classes
│   ├── data/        # Data management utilities
│   ├── reporting/   # Reporting utilities
│   └── selenium/    # Selenium-specific utilities
└── tests/           # Test classes
    ├── api/         # API tests
    ├── ui/          # UI tests
    └── integration/ # Integration tests
```

### Naming Conventions

#### Classes
- **Page Objects:** `LoginPage`, `HomePage`
- **Test Classes:** `LoginTest`, `UserManagementTest`
- **Utility Classes:** `DriverManager`, `ConfigReader`
- **Data Classes:** `UserData`, `ProductData`

#### Methods
- **Test Methods:** `testSuccessfulLogin()`, `testInvalidCredentials()`
- **Page Methods:** `enterUsername()`, `clickLoginButton()`
- **Utility Methods:** `takeScreenshot()`, `waitForElement()`

#### Variables
- **WebElements:** `usernameField`, `loginButton`
- **Test Data:** `validUser`, `invalidPassword`
- **Configuration:** `baseUrl`, `browserType`

## 🧪 Testing Guidelines

### Test Categories

Use TestNG groups to categorize tests:

- **smoke**: Critical functionality tests
- **regression**: Comprehensive test suite
- **positive**: Happy path scenarios
- **negative**: Error handling scenarios
- **api**: API-specific tests
- **ui**: UI-specific tests

### Test Data Management

1. **Externalize test data** in JSON/CSV files
2. **Use data providers** for multiple test scenarios
3. **Avoid hardcoded values** in test methods
4. **Create data factories** for complex objects

Example:
```java
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return JsonDataReader.readTestData("login-data.json");
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password, boolean expectedResult) {
    // Test implementation
}
```

### Page Object Guidelines

1. **Encapsulate page interactions**
2. **Return page objects** from navigation methods
3. **Use meaningful element names**
4. **Implement fluent interfaces** where appropriate

Example:
```java
public class LoginPage extends BasePage {
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }
    
    public HomePage clickLogin() {
        click(loginButton);
        return new HomePage();
    }
    
    // Fluent interface
    public HomePage login(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLogin();
    }
}
```

## 📚 Documentation Guidelines

### Code Documentation

1. **Write JavaDoc** for all public methods
2. **Include parameter descriptions**
3. **Document return values**
4. **Add usage examples** for complex methods

Example:
```java
/**
 * Performs login with the provided credentials.
 * 
 * @param username the username to enter
 * @param password the password to enter
 * @return HomePage instance if login is successful
 * @throws IllegalArgumentException if username or password is null
 * 
 * @example
 * <pre>
 * LoginPage loginPage = new LoginPage();
 * HomePage homePage = loginPage.login("user123", "password");
 * </pre>
 */
public HomePage login(String username, String password) {
    // Implementation
}
```

### README Updates

When adding new features:

1. **Update feature list**
2. **Add configuration examples**
3. **Include usage instructions**
4. **Update troubleshooting section** if needed

## 🚀 Release Process

### Version Numbering

We follow **Semantic Versioning** (SemVer):
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Release Checklist

1. **Update version** in `pom.xml`
2. **Update CHANGELOG.md**
3. **Run full test suite**
4. **Update documentation**
5. **Create release notes**
6. **Tag the release**

## 🏆 Recognition

Contributors will be recognized in:
- **CONTRIBUTORS.md** file
- **Release notes**
- **GitHub contributors** section

## 📞 Getting Help

If you need help with contributing:

1. **Check existing documentation**
2. **Search closed issues** for similar questions
3. **Ask in discussions** section
4. **Contact maintainers** directly

## 📋 Contributor License Agreement

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to the Java Selenium Automation Framework! 🎉
