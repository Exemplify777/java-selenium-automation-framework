#!/bin/bash

# Docker Test Execution Script
# This script provides easy commands to run tests in Docker containers

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

# Function to show usage
show_usage() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  local           Run tests locally without Selenium Grid"
    echo "  grid            Run tests with Selenium Grid"
    echo "  smoke           Run smoke tests"
    echo "  regression      Run regression tests"
    echo "  parallel        Run tests in parallel"
    echo "  debug           Run tests with Chrome debug (VNC available on port 5900)"
    echo "  build           Build the Docker image"
    echo "  clean           Clean up Docker containers and images"
    echo "  reports         Start report server"
    echo "  logs            Show logs from test execution"
    echo ""
    echo "Options:"
    echo "  -e, --environment   Environment (dev, staging, prod) [default: dev]"
    echo "  -b, --browser       Browser (chrome, firefox, edge) [default: chrome]"
    echo "  -h, --headless      Run in headless mode [default: true]"
    echo "  -t, --threads       Number of threads for parallel execution [default: 2]"
    echo "  --help              Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 local -e staging -b firefox"
    echo "  $0 grid -t 4"
    echo "  $0 smoke --headless false"
    echo "  $0 debug"
}

# Default values
ENVIRONMENT="dev"
BROWSER="chrome"
HEADLESS="true"
THREADS="2"
COMMAND=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        local|grid|smoke|regression|parallel|debug|build|clean|reports|logs)
            COMMAND="$1"
            shift
            ;;
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -b|--browser)
            BROWSER="$2"
            shift 2
            ;;
        -h|--headless)
            HEADLESS="$2"
            shift 2
            ;;
        -t|--threads)
            THREADS="$2"
            shift 2
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            print_message $RED "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Check if command is provided
if [[ -z "$COMMAND" ]]; then
    print_message $RED "Error: No command provided"
    show_usage
    exit 1
fi

# Create necessary directories
mkdir -p target/reports target/screenshots target/allure-results logs

# Export environment variables
export ENVIRONMENT
export BROWSER
export HEADLESS
export THREADS

case $COMMAND in
    local)
        print_message $BLUE "Running tests locally..."
        print_message $YELLOW "Environment: $ENVIRONMENT, Browser: $BROWSER, Headless: $HEADLESS"
        docker-compose -f docker-compose.local.yml up --build test-automation-local
        ;;
    
    grid)
        print_message $BLUE "Running tests with Selenium Grid..."
        print_message $YELLOW "Environment: $ENVIRONMENT, Browser: $BROWSER, Threads: $THREADS"
        docker-compose up --build --scale chrome-node=2 --scale firefox-node=1
        ;;
    
    smoke)
        print_message $BLUE "Running smoke tests..."
        docker run --rm \
            -v "$(pwd)/target/reports:/app/target/reports" \
            -v "$(pwd)/target/screenshots:/app/target/screenshots" \
            -v "$(pwd)/logs:/app/logs" \
            -e ENVIRONMENT="$ENVIRONMENT" \
            -e BROWSER="$BROWSER" \
            -e HEADLESS="$HEADLESS" \
            test-automation:latest \
            mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml
        ;;
    
    regression)
        print_message $BLUE "Running regression tests..."
        docker run --rm \
            -v "$(pwd)/target/reports:/app/target/reports" \
            -v "$(pwd)/target/screenshots:/app/target/screenshots" \
            -v "$(pwd)/logs:/app/logs" \
            -e ENVIRONMENT="$ENVIRONMENT" \
            -e BROWSER="$BROWSER" \
            -e HEADLESS="$HEADLESS" \
            test-automation:latest \
            mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-regression.xml
        ;;
    
    parallel)
        print_message $BLUE "Running tests in parallel..."
        docker run --rm \
            -v "$(pwd)/target/reports:/app/target/reports" \
            -v "$(pwd)/target/screenshots:/app/target/screenshots" \
            -v "$(pwd)/logs:/app/logs" \
            -e ENVIRONMENT="$ENVIRONMENT" \
            -e BROWSER="$BROWSER" \
            -e HEADLESS="$HEADLESS" \
            -e THREAD_COUNT="$THREADS" \
            test-automation:latest \
            mvn test -Dsurefire.suiteXmlFiles=src/test/resources/testng-parallel.xml
        ;;
    
    debug)
        print_message $BLUE "Starting debug environment..."
        print_message $YELLOW "VNC will be available on localhost:5900 (no password required)"
        docker-compose -f docker-compose.local.yml up --build chrome-debug test-with-chrome
        ;;
    
    build)
        print_message $BLUE "Building Docker image..."
        docker build -t test-automation:latest .
        print_message $GREEN "Docker image built successfully!"
        ;;
    
    clean)
        print_message $BLUE "Cleaning up Docker containers and images..."
        docker-compose down --remove-orphans
        docker-compose -f docker-compose.local.yml down --remove-orphans
        docker system prune -f
        print_message $GREEN "Cleanup completed!"
        ;;
    
    reports)
        print_message $BLUE "Starting report server..."
        print_message $YELLOW "Reports will be available at http://localhost:8080"
        docker-compose up report-server
        ;;
    
    logs)
        print_message $BLUE "Showing test execution logs..."
        if [[ -f "logs/automation.log" ]]; then
            tail -f logs/automation.log
        else
            print_message $YELLOW "No log file found. Run tests first."
        fi
        ;;
    
    *)
        print_message $RED "Unknown command: $COMMAND"
        show_usage
        exit 1
        ;;
esac

print_message $GREEN "Command '$COMMAND' completed!"
