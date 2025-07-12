#!/bin/bash

# Script to install Git hooks for the Java Selenium Automation Framework

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

print_message $BLUE "🔧 Installing Git hooks for Java Selenium Automation Framework..."

# Check if we're in a Git repository
if [ ! -d ".git" ]; then
    print_message $RED "❌ This is not a Git repository. Please run this script from the root of your Git repository."
    exit 1
fi

# Create hooks directory if it doesn't exist
HOOKS_DIR=".git/hooks"
if [ ! -d "$HOOKS_DIR" ]; then
    mkdir -p "$HOOKS_DIR"
    print_message $BLUE "📁 Created hooks directory: $HOOKS_DIR"
fi

# Install pre-commit hook
PRE_COMMIT_HOOK="$HOOKS_DIR/pre-commit"
PRE_COMMIT_SCRIPT="scripts/pre-commit.sh"

if [ ! -f "$PRE_COMMIT_SCRIPT" ]; then
    print_message $RED "❌ Pre-commit script not found: $PRE_COMMIT_SCRIPT"
    exit 1
fi

# Copy the pre-commit script
cp "$PRE_COMMIT_SCRIPT" "$PRE_COMMIT_HOOK"
chmod +x "$PRE_COMMIT_HOOK"

print_message $GREEN "✅ Pre-commit hook installed successfully"

# Create commit-msg hook for conventional commits
COMMIT_MSG_HOOK="$HOOKS_DIR/commit-msg"
cat > "$COMMIT_MSG_HOOK" << 'EOF'
#!/bin/bash

# Commit message hook to enforce conventional commit format
# Format: type(scope): description
# Types: feat, fix, docs, style, refactor, test, chore

commit_regex='^(feat|fix|docs|style|refactor|test|chore)(\(.+\))?: .{1,50}'

error_msg="Invalid commit message format!

Commit message should follow the conventional commit format:
type(scope): description

Types:
  feat:     A new feature
  fix:      A bug fix
  docs:     Documentation only changes
  style:    Changes that do not affect the meaning of the code
  refactor: A code change that neither fixes a bug nor adds a feature
  test:     Adding missing tests or correcting existing tests
  chore:    Changes to the build process or auxiliary tools

Examples:
  feat(login): add remember me functionality
  fix(driver): resolve browser initialization issue
  docs: update README with setup instructions
  test(login): add negative test cases"

if ! grep -qE "$commit_regex" "$1"; then
    echo "$error_msg" >&2
    exit 1
fi
EOF

chmod +x "$COMMIT_MSG_HOOK"
print_message $GREEN "✅ Commit message hook installed successfully"

# Create prepare-commit-msg hook to add branch name to commit message
PREPARE_COMMIT_MSG_HOOK="$HOOKS_DIR/prepare-commit-msg"
cat > "$PREPARE_COMMIT_MSG_HOOK" << 'EOF'
#!/bin/bash

# Automatically add branch name to commit message

COMMIT_MSG_FILE=$1
COMMIT_SOURCE=$2
SHA1=$3

# Get the current branch name
BRANCH_NAME=$(git symbolic-ref --short HEAD 2>/dev/null)

# Only add branch name if it's not main/master/develop
if [[ "$BRANCH_NAME" != "main" && "$BRANCH_NAME" != "master" && "$BRANCH_NAME" != "develop" ]]; then
    # Check if branch name is already in the commit message
    if ! grep -q "$BRANCH_NAME" "$COMMIT_MSG_FILE"; then
        # Add branch name to the end of the commit message
        echo "" >> "$COMMIT_MSG_FILE"
        echo "Branch: $BRANCH_NAME" >> "$COMMIT_MSG_FILE"
    fi
fi
EOF

chmod +x "$PREPARE_COMMIT_MSG_HOOK"
print_message $GREEN "✅ Prepare commit message hook installed successfully"

# Create post-commit hook for notifications
POST_COMMIT_HOOK="$HOOKS_DIR/post-commit"
cat > "$POST_COMMIT_HOOK" << 'EOF'
#!/bin/bash

# Post-commit hook for notifications and cleanup

# Get commit information
COMMIT_HASH=$(git rev-parse HEAD)
COMMIT_MESSAGE=$(git log -1 --pretty=%B)
AUTHOR=$(git log -1 --pretty=%an)
BRANCH=$(git symbolic-ref --short HEAD 2>/dev/null || echo "detached")

echo "✅ Commit successful!"
echo "📝 Hash: $COMMIT_HASH"
echo "👤 Author: $AUTHOR"
echo "🌿 Branch: $BRANCH"
echo "💬 Message: $COMMIT_MESSAGE"

# Optional: Run quick smoke test after commit (uncomment if desired)
# echo "🧪 Running quick smoke test..."
# mvn test -Dtest="*SmokeTest" -q || echo "⚠️ Smoke test failed"
EOF

chmod +x "$POST_COMMIT_HOOK"
print_message $GREEN "✅ Post-commit hook installed successfully"

# Summary
print_message $BLUE "📋 Installation Summary:"
print_message $GREEN "  ✅ pre-commit: Code quality checks before commit"
print_message $GREEN "  ✅ commit-msg: Enforce conventional commit format"
print_message $GREEN "  ✅ prepare-commit-msg: Add branch name to commit message"
print_message $GREEN "  ✅ post-commit: Show commit information and optional tests"

print_message $YELLOW "📝 Notes:"
print_message $YELLOW "  • Pre-commit hook will run code quality checks before each commit"
print_message $YELLOW "  • Commit messages must follow conventional commit format"
print_message $YELLOW "  • Branch name will be automatically added to commit messages"
print_message $YELLOW "  • You can skip hooks with 'git commit --no-verify' if needed"

print_message $GREEN "🎉 Git hooks installation completed successfully!"

# Test the installation
print_message $BLUE "🧪 Testing hook installation..."
if [ -x "$PRE_COMMIT_HOOK" ] && [ -x "$COMMIT_MSG_HOOK" ]; then
    print_message $GREEN "✅ All hooks are executable and ready to use"
else
    print_message $RED "❌ Some hooks are not executable. Please check the installation."
    exit 1
fi
