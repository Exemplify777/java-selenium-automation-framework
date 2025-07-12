# Use OpenJDK 11 as base image
FROM openjdk:11-jdk-slim

# Set maintainer information
LABEL maintainer="automation-team@example.com"
LABEL description="Selenium Test Automation Framework"
LABEL version="1.0.0"

# Set environment variables
ENV MAVEN_VERSION=3.9.5
ENV MAVEN_HOME=/opt/maven
ENV PATH=$MAVEN_HOME/bin:$PATH
ENV JAVA_OPTS="-Xmx2g -Xms1g"

# Install system dependencies
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    gnupg \
    software-properties-common \
    ca-certificates \
    apt-transport-https \
    && rm -rf /var/lib/apt/lists/*

# Install Maven
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && mv /opt/apache-maven-${MAVEN_VERSION} /opt/maven \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Install Google Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list' \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y firefox-esr \
    && rm -rf /var/lib/apt/lists/*

# Install Microsoft Edge
RUN curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.gpg \
    && install -o root -g root -m 644 microsoft.gpg /etc/apt/trusted.gpg.d/ \
    && sh -c 'echo "deb [arch=amd64,arm64,armhf signed-by=/etc/apt/trusted.gpg.d/microsoft.gpg] https://packages.microsoft.com/repos/edge stable main" > /etc/apt/sources.list.d/microsoft-edge-dev.list' \
    && apt-get update \
    && apt-get install -y microsoft-edge-stable \
    && rm -rf /var/lib/apt/lists/*

# Create application directory
WORKDIR /app

# Copy Maven configuration first for better layer caching
COPY pom.xml .
COPY checkstyle.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ ./src/
COPY .github/ ./.github/

# Create necessary directories
RUN mkdir -p target/screenshots target/reports target/allure-results logs

# Set permissions
RUN chmod -R 755 /app

# Create a non-root user for security
RUN groupadd -r automation && useradd -r -g automation -u 1001 automation
RUN chown -R automation:automation /app
USER automation

# Expose port for potential web server (reports)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -version || exit 1

# Default command - run smoke tests
CMD ["mvn", "test", "-Dsurefire.suiteXmlFiles=src/test/resources/testng-smoke.xml", "-Dheadless=true"]
