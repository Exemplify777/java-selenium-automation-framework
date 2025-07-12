#!/bin/bash

# Pre-commit hook for Java Selenium Automation Framework
# This script runs code quality checks before allowing commits

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

print_message $BLUE "🔍 Running pre-commit checks..."

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    print_message $RED "❌ Maven is not installed or not in PATH"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    print_message $RED "❌ Java is not installed or not in PATH"
    exit 1
fi

# Get list of staged Java files
STAGED_JAVA_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.java$' || true)

if [ -z "$STAGED_JAVA_FILES" ]; then
    print_message $YELLOW "⚠️  No Java files staged for commit"
    exit 0
fi

print_message $BLUE "📁 Found staged Java files:"
echo "$STAGED_JAVA_FILES"

# Function to run a check and handle errors
run_check() {
    local check_name=$1
    local command=$2
    local error_message=$3
    
    print_message $BLUE "🔄 Running $check_name..."
    
    if eval "$command"; then
        print_message $GREEN "✅ $check_name passed"
        return 0
    else
        print_message $RED "❌ $check_name failed"
        print_message $RED "$error_message"
        return 1
    fi
}

# Initialize error flag
ERRORS=0

# 1. Compile check
if ! run_check "Compilation" "mvn clean compile -q" "Code compilation failed. Please fix compilation errors before committing."; then
    ERRORS=1
fi

# 2. Checkstyle check
if ! run_check "Checkstyle" "mvn checkstyle:check -q" "Code style violations found. Please fix them before committing."; then
    ERRORS=1
fi

# 3. PMD check
if ! run_check "PMD" "mvn pmd:check -q" "PMD violations found. Please review and fix them before committing."; then
    print_message $YELLOW "⚠️  PMD violations found but allowing commit. Please review the issues."
fi

# 4. SpotBugs check
if ! run_check "SpotBugs" "mvn spotbugs:check -q" "SpotBugs violations found. Please review and fix them before committing."; then
    print_message $YELLOW "⚠️  SpotBugs violations found but allowing commit. Please review the issues."
fi

# 5. Test compilation check
if ! run_check "Test Compilation" "mvn test-compile -q" "Test compilation failed. Please fix test compilation errors before committing."; then
    ERRORS=1
fi

# 6. Unit tests (if any exist and are fast)
if [ -d "src/test/java" ] && [ "$(find src/test/java -name '*Test.java' | wc -l)" -gt 0 ]; then
    print_message $BLUE "🧪 Running unit tests..."
    if mvn test -Dtest="*UnitTest" -q 2>/dev/null; then
        print_message $GREEN "✅ Unit tests passed"
    else
        print_message $YELLOW "⚠️  No unit tests found or unit tests failed. Consider adding unit tests."
    fi
fi

# 7. Check for common issues in staged files
print_message $BLUE "🔍 Checking for common issues..."

# Check for System.out.println (should use logger)
if echo "$STAGED_JAVA_FILES" | xargs grep -l "System\.out\.print" 2>/dev/null; then
    print_message $YELLOW "⚠️  Found System.out.print statements. Consider using logger instead."
fi

# Check for printStackTrace (should use logger)
if echo "$STAGED_JAVA_FILES" | xargs grep -l "printStackTrace" 2>/dev/null; then
    print_message $YELLOW "⚠️  Found printStackTrace calls. Consider using logger instead."
fi

# Check for hardcoded passwords or sensitive data
if echo "$STAGED_JAVA_FILES" | xargs grep -i -l "password\s*=\s*[\"']" 2>/dev/null; then
    print_message $RED "❌ Found hardcoded passwords. Please remove them before committing."
    ERRORS=1
fi

# Check for TODO comments
TODO_COUNT=$(echo "$STAGED_JAVA_FILES" | xargs grep -c "TODO" 2>/dev/null | awk -F: '{sum += $2} END {print sum+0}')
if [ "$TODO_COUNT" -gt 0 ]; then
    print_message $YELLOW "⚠️  Found $TODO_COUNT TODO comments. Consider addressing them."
fi

# Check for FIXME comments
FIXME_COUNT=$(echo "$STAGED_JAVA_FILES" | xargs grep -c "FIXME" 2>/dev/null | awk -F: '{sum += $2} END {print sum+0}')
if [ "$FIXME_COUNT" -gt 0 ]; then
    print_message $YELLOW "⚠️  Found $FIXME_COUNT FIXME comments. Consider addressing them."
fi

# 8. Check file formatting
print_message $BLUE "📝 Checking file formatting..."
for file in $STAGED_JAVA_FILES; do
    # Check for trailing whitespace
    if grep -q '[[:space:]]$' "$file"; then
        print_message $YELLOW "⚠️  Found trailing whitespace in $file"
    fi
    
    # Check for tabs (should use spaces)
    if grep -q $'\t' "$file"; then
        print_message $YELLOW "⚠️  Found tabs in $file. Consider using spaces instead."
    fi
done

# 9. Check for large files
print_message $BLUE "📏 Checking file sizes..."
for file in $STAGED_JAVA_FILES; do
    if [ -f "$file" ]; then
        size=$(wc -l < "$file")
        if [ "$size" -gt 500 ]; then
            print_message $YELLOW "⚠️  $file has $size lines. Consider breaking it into smaller files."
        fi
    fi
done

# Final result
if [ $ERRORS -eq 0 ]; then
    print_message $GREEN "🎉 All critical checks passed! Commit allowed."
    print_message $BLUE "📊 Pre-commit summary:"
    print_message $GREEN "  ✅ Compilation: PASSED"
    print_message $GREEN "  ✅ Checkstyle: PASSED"
    print_message $GREEN "  ✅ Critical checks: PASSED"
    exit 0
else
    print_message $RED "💥 Critical checks failed! Please fix the issues before committing."
    print_message $RED "Run the following commands to see detailed error reports:"
    print_message $YELLOW "  mvn clean compile"
    print_message $YELLOW "  mvn checkstyle:check"
    print_message $YELLOW "  mvn pmd:check"
    print_message $YELLOW "  mvn spotbugs:check"
    exit 1
fi
