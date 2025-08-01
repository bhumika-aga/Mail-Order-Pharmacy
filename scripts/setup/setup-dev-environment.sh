#!/bin/bash

# Development environment setup script for Mail Order Pharmacy
# This script sets up IDEs and development tools

set -e

echo "=== Development Environment Setup ==="

# Create .editorconfig for consistent coding style
echo "Creating .editorconfig..."
cat > .editorconfig << 'EOF'
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true
indent_style = space
indent_size = 4

[*.{js,jsx,ts,tsx,json}]
indent_size = 2

[*.{yml,yaml}]
indent_size = 2

[*.md]
trim_trailing_whitespace = false
EOF

# Create .gitignore if it doesn't exist
if [ ! -f ".gitignore" ]; then
    echo "Creating .gitignore..."
    cat > .gitignore << 'EOF'
# Compiled class files
*.class

# Log files
*.log

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# Node.js
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*
.npm
.yarn-integrity

# React build
build/
dist/

# Environment variables
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# IDEs
.idea/
*.iws
*.iml
*.ipr
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Database files
*.db
*.h2.db
data/

# Docker
.docker/

# Logs
logs/
*.log
EOF
fi

# Create VS Code settings
echo "Creating VS Code workspace settings..."
mkdir -p .vscode

cat > .vscode/settings.json << 'EOF'
{
    "editor.tabSize": 4,
    "editor.insertSpaces": true,
    "editor.detectIndentation": false,
    "java.format.settings.url": "./eclipse-formatter.xml",
    "java.format.settings.profile": "GoogleStyle",
    "typescript.preferences.indentStyle": "indent",
    "javascript.preferences.indentStyle": "indent",
    "files.associations": {
        "*.yml": "yaml",
        "*.yaml": "yaml"
    },
    "java.server.launchMode": "Standard",
    "java.configuration.updateBuildConfiguration": "automatic"
}
EOF

cat > .vscode/extensions.json << 'EOF'
{
    "recommendations": [
        "vscjava.vscode-java-pack",
        "ms-vscode.vscode-typescript-next",
        "bradlc.vscode-tailwindcss",
        "esbenp.prettier-vscode",
        "ms-vscode.vscode-docker",
        "redhat.vscode-yaml",
        "ms-vscode.vscode-json"
    ]
}
EOF

cat > .vscode/launch.json << 'EOF'
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Auth Microservice",
            "request": "launch",
            "mainClass": "com.pharmacy.auth.AuthMicroserviceApplication",
            "projectName": "auth-microservice",
            "args": "--spring.profiles.active=dev"
        },
        {
            "type": "java",
            "name": "Drugs Microservice",
            "request": "launch",
            "mainClass": "com.pharmacy.drugs.DrugsMicroserviceApplication",
            "projectName": "drugs-microservice",
            "args": "--spring.profiles.active=dev"
        },
        {
            "type": "java",
            "name": "Subscription Microservice",
            "request": "launch",
            "mainClass": "com.pharmacy.subscription.SubscriptionMicroserviceApplication",
            "projectName": "subscription-microservice",
            "args": "--spring.profiles.active=dev"
        },
        {
            "type": "java",
            "name": "Refill Microservice",
            "request": "launch",
            "mainClass": "com.pharmacy.refill.RefillMicroserviceApplication",
            "projectName": "refill-microservice",
            "args": "--spring.profiles.active=dev"
        },
        {
            "type": "java",
            "name": "Swagger Aggregator",
            "request": "launch",
            "mainClass": "com.pharmacy.swagger.SwaggerAggregatorApplication",
            "projectName": "swagger-aggregator",
            "args": "--spring.profiles.active=dev"
        }
    ]
}
EOF

echo "Development environment setup completed ✓"
echo ""
echo "Created files:"
echo "  - .editorconfig (coding style configuration)"
echo "  - .gitignore (Git ignore patterns)"
echo "  - .vscode/settings.json (VS Code settings)"
echo "  - .vscode/extensions.json (recommended extensions)"
echo "  - .vscode/launch.json (debug configurations)"
echo ""
echo "Recommended VS Code extensions will be suggested when you open the project."