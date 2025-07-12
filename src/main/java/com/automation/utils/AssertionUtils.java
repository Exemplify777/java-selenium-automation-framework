package com.automation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.List;

/**
 * Utility class for enhanced assertions with detailed logging.
 * Provides custom assertion methods with better error messages.
 * 
 * @author Automation Framework
 * @version 1.0
 */
public final class AssertionUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AssertionUtils.class);
    
    /**
     * Private constructor to prevent instantiation.
     */
    private AssertionUtils() {
        // Utility class
    }
    
    /**
     * Asserts that two strings are equal with detailed logging.
     * 
     * @param actual the actual value
     * @param expected the expected value
     * @param message the assertion message
     */
    public static void assertEquals(final String actual, final String expected, final String message) {
        try {
            Assert.assertEquals(actual, expected, message);
            LOGGER.info("Assertion passed: {} - Expected: '{}', Actual: '{}'", message, expected, actual);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Expected: '{}', Actual: '{}'", message, expected, actual);
            throw e;
        }
    }
    
    /**
     * Asserts that two integers are equal with detailed logging.
     * 
     * @param actual the actual value
     * @param expected the expected value
     * @param message the assertion message
     */
    public static void assertEquals(final int actual, final int expected, final String message) {
        try {
            Assert.assertEquals(actual, expected, message);
            LOGGER.info("Assertion passed: {} - Expected: {}, Actual: {}", message, expected, actual);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Expected: {}, Actual: {}", message, expected, actual);
            throw e;
        }
    }
    
    /**
     * Asserts that two objects are equal with detailed logging.
     * 
     * @param actual the actual value
     * @param expected the expected value
     * @param message the assertion message
     */
    public static void assertEquals(final Object actual, final Object expected, final String message) {
        try {
            Assert.assertEquals(actual, expected, message);
            LOGGER.info("Assertion passed: {} - Expected: '{}', Actual: '{}'", message, expected, actual);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Expected: '{}', Actual: '{}'", message, expected, actual);
            throw e;
        }
    }
    
    /**
     * Asserts that a condition is true with detailed logging.
     * 
     * @param condition the condition to check
     * @param message the assertion message
     */
    public static void assertTrue(final boolean condition, final String message) {
        try {
            Assert.assertTrue(condition, message);
            LOGGER.info("Assertion passed: {} - Condition is true", message);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Condition is false", message);
            throw e;
        }
    }
    
    /**
     * Asserts that a condition is false with detailed logging.
     * 
     * @param condition the condition to check
     * @param message the assertion message
     */
    public static void assertFalse(final boolean condition, final String message) {
        try {
            Assert.assertFalse(condition, message);
            LOGGER.info("Assertion passed: {} - Condition is false", message);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Condition is true", message);
            throw e;
        }
    }
    
    /**
     * Asserts that an object is null with detailed logging.
     * 
     * @param object the object to check
     * @param message the assertion message
     */
    public static void assertNull(final Object object, final String message) {
        try {
            Assert.assertNull(object, message);
            LOGGER.info("Assertion passed: {} - Object is null", message);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Object is not null: '{}'", message, object);
            throw e;
        }
    }
    
    /**
     * Asserts that an object is not null with detailed logging.
     * 
     * @param object the object to check
     * @param message the assertion message
     */
    public static void assertNotNull(final Object object, final String message) {
        try {
            Assert.assertNotNull(object, message);
            LOGGER.info("Assertion passed: {} - Object is not null", message);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Object is null", message);
            throw e;
        }
    }
    
    /**
     * Asserts that a string contains a substring with detailed logging.
     * 
     * @param actual the actual string
     * @param expected the expected substring
     * @param message the assertion message
     */
    public static void assertContains(final String actual, final String expected, final String message) {
        try {
            Assert.assertTrue(actual.contains(expected), 
                String.format("%s - Expected '%s' to contain '%s'", message, actual, expected));
            LOGGER.info("Assertion passed: {} - '{}' contains '{}'", message, actual, expected);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - '{}' does not contain '{}'", message, actual, expected);
            throw e;
        }
    }
    
    /**
     * Asserts that a string does not contain a substring with detailed logging.
     * 
     * @param actual the actual string
     * @param notExpected the substring that should not be present
     * @param message the assertion message
     */
    public static void assertNotContains(final String actual, final String notExpected, final String message) {
        try {
            Assert.assertFalse(actual.contains(notExpected), 
                String.format("%s - Expected '%s' to not contain '%s'", message, actual, notExpected));
            LOGGER.info("Assertion passed: {} - '{}' does not contain '{}'", message, actual, notExpected);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - '{}' contains '{}'", message, actual, notExpected);
            throw e;
        }
    }
    
    /**
     * Asserts that a string starts with a prefix with detailed logging.
     * 
     * @param actual the actual string
     * @param prefix the expected prefix
     * @param message the assertion message
     */
    public static void assertStartsWith(final String actual, final String prefix, final String message) {
        try {
            Assert.assertTrue(actual.startsWith(prefix), 
                String.format("%s - Expected '%s' to start with '%s'", message, actual, prefix));
            LOGGER.info("Assertion passed: {} - '{}' starts with '{}'", message, actual, prefix);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - '{}' does not start with '{}'", message, actual, prefix);
            throw e;
        }
    }
    
    /**
     * Asserts that a string ends with a suffix with detailed logging.
     * 
     * @param actual the actual string
     * @param suffix the expected suffix
     * @param message the assertion message
     */
    public static void assertEndsWith(final String actual, final String suffix, final String message) {
        try {
            Assert.assertTrue(actual.endsWith(suffix), 
                String.format("%s - Expected '%s' to end with '%s'", message, actual, suffix));
            LOGGER.info("Assertion passed: {} - '{}' ends with '{}'", message, actual, suffix);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - '{}' does not end with '{}'", message, actual, suffix);
            throw e;
        }
    }
    
    /**
     * Asserts that a string matches a regular expression with detailed logging.
     * 
     * @param actual the actual string
     * @param regex the regular expression pattern
     * @param message the assertion message
     */
    public static void assertMatches(final String actual, final String regex, final String message) {
        try {
            Assert.assertTrue(actual.matches(regex), 
                String.format("%s - Expected '%s' to match pattern '%s'", message, actual, regex));
            LOGGER.info("Assertion passed: {} - '{}' matches pattern '{}'", message, actual, regex);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - '{}' does not match pattern '{}'", message, actual, regex);
            throw e;
        }
    }
    
    /**
     * Asserts that a list is empty with detailed logging.
     * 
     * @param list the list to check
     * @param message the assertion message
     */
    public static void assertEmpty(final List<?> list, final String message) {
        try {
            Assert.assertTrue(list.isEmpty(), 
                String.format("%s - Expected list to be empty but has %d elements", message, list.size()));
            LOGGER.info("Assertion passed: {} - List is empty", message);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - List is not empty, size: {}", message, list.size());
            throw e;
        }
    }
    
    /**
     * Asserts that a list is not empty with detailed logging.
     * 
     * @param list the list to check
     * @param message the assertion message
     */
    public static void assertNotEmpty(final List<?> list, final String message) {
        try {
            Assert.assertFalse(list.isEmpty(), 
                String.format("%s - Expected list to not be empty", message));
            LOGGER.info("Assertion passed: {} - List is not empty, size: {}", message, list.size());
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - List is empty", message);
            throw e;
        }
    }
    
    /**
     * Asserts that a list has a specific size with detailed logging.
     * 
     * @param list the list to check
     * @param expectedSize the expected size
     * @param message the assertion message
     */
    public static void assertSize(final List<?> list, final int expectedSize, final String message) {
        try {
            Assert.assertEquals(list.size(), expectedSize, 
                String.format("%s - Expected list size %d but was %d", message, expectedSize, list.size()));
            LOGGER.info("Assertion passed: {} - List size is {}", message, expectedSize);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - Expected list size {} but was {}", message, expectedSize, list.size());
            throw e;
        }
    }
    
    /**
     * Asserts that a number is greater than another with detailed logging.
     * 
     * @param actual the actual number
     * @param expected the number to compare against
     * @param message the assertion message
     */
    public static void assertGreaterThan(final double actual, final double expected, final String message) {
        try {
            Assert.assertTrue(actual > expected, 
                String.format("%s - Expected %f to be greater than %f", message, actual, expected));
            LOGGER.info("Assertion passed: {} - {} is greater than {}", message, actual, expected);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - {} is not greater than {}", message, actual, expected);
            throw e;
        }
    }
    
    /**
     * Asserts that a number is less than another with detailed logging.
     * 
     * @param actual the actual number
     * @param expected the number to compare against
     * @param message the assertion message
     */
    public static void assertLessThan(final double actual, final double expected, final String message) {
        try {
            Assert.assertTrue(actual < expected, 
                String.format("%s - Expected %f to be less than %f", message, actual, expected));
            LOGGER.info("Assertion passed: {} - {} is less than {}", message, actual, expected);
        } catch (AssertionError e) {
            LOGGER.error("Assertion failed: {} - {} is not less than {}", message, actual, expected);
            throw e;
        }
    }
    
    /**
     * Soft assertion that doesn't immediately fail the test.
     * Logs the assertion result and continues execution.
     * 
     * @param condition the condition to check
     * @param message the assertion message
     * @return true if assertion passed, false otherwise
     */
    public static boolean softAssert(final boolean condition, final String message) {
        if (condition) {
            LOGGER.info("Soft assertion passed: {}", message);
            return true;
        } else {
            LOGGER.error("Soft assertion failed: {}", message);
            return false;
        }
    }
}
