# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java-based email server built on Apache James. It provides a managed email server with dynamic configuration through a file-watching system that automatically applies configuration changes without restarts.

**Key Features:**
- Apache James server with MariaDB backend
- Real-time configuration management via JSON file watching
- Email account management (passwords in clear text or SHA-512)
- Domain management with optional external relay/gateway support (e.g., Amazon SES, SendGrid)
- Email redirections with catch-all support
- PEM-based certificates (auto-generates self-signed if not provided)
- Authentication enforcement (sender's "from" field must match authenticated user)

## Build System

This project uses Gradle with Java 11.

### Essential Commands

**Development setup:**
```bash
# Start MariaDB (REQUIRED for all development work)
./test-mariadb-start.sh

# Stop MariaDB
./test-mariadb-stop.sh
```

**Building:**
```bash
# Build with tests
./gradlew build

# Build without tests
./gradlew build -x test

# Clean and compile
./step-clean-compile.sh

# Clean only
./step-clean.sh
```

**Running tests:**
```bash
# All tests
./gradlew test

# Single test class
./gradlew test --tests com.foilen.james.components.mailet.ExactAndCatchAllRedirectionsTest

# Single test method
./gradlew test --tests com.foilen.james.components.mailet.ExactAndCatchAllRedirectionsTest.testMethod
```

**Local execution:**
```bash
# Run in Docker
./test-server-local.sh

# Run in Eclipse
# Use "Email - Application.launch" configuration
# Creates default test config in _workdir/email-manager-config.json
# Default test account: account@localhost.foilen-lab.com / qwerty
```

**Release:**
```bash
# Local release (with tests)
./create-local-release.sh

# Local release (no tests)
./create-local-release-no-tests.sh

# Public release
./create-public-release.sh
```

## Architecture

### Core Components

**Application Entry Point (Application.java:76)**
- Main class: `com.foilen.email.server.Application`
- Uses Google Guice for dependency injection
- Combines James modules: JPA, IMAP, POP3, SMTP, ActiveMQ queue, Lucene search
- Configures system properties from JSON configs before server startup

**Configuration System**
The system uses two separate JSON configuration files:

1. **James Config** (`james-config.json`): Static server configuration
   - Database connection details
   - Postmaster email
   - Domain relay/gateway mappings
   - Debug and notification settings

2. **Manager Config** (`manager-config.json`): Dynamic configuration (file-watched)
   - Domains list
   - User accounts (email + password/passwordSha512)
   - Redirections (including catch-all with `*@domain.com`)

**File Watching & Real-Time Updates**
- `ConfigurationServiceImpl` (ConfigurationServiceImpl.java:18) watches the manager config file
- Uses `OneFileUpdateNotifyer` to detect file changes
- Automatically dispatches updates to registered callbacks
- `UpdateJamesService` (UpdateJamesService.java:28) receives callbacks and synchronizes the database
- Implements retry logic (13-second intervals) if updates fail

**Database Synchronization (UpdateJamesService.java:66)**
- Uses `ListsComparator` pattern to diff existing vs desired state
- Database lock mechanism via `LockService` prevents concurrent modifications
- Synchronizes three main entities:
  - Domains (JAMES_DOMAIN table)
  - Accounts (JAMES_USER table) - updates passwords when changed
  - Redirections (FOILEN_REDIRECTIONS custom table)
- Cleanup phase removes orphaned records from related tables

**James Configuration Generation (JamesWorkDirManagement.java:81)**
- Generates James XML config files from FreeMarker templates (*.ftl)
- Template files in `src/main/resources/com/foilen/email/server/james/conf/`
- Creates keystores from PEM files or generates self-signed certificates
- Copies static config files and renders dynamic ones
- All configs written to `workdir/conf/`

### Mail Processing

**Custom Mailets**
- `ExactAndCatchAllRedirections` (ExactAndCatchAllRedirections.java:28): Handles email redirection logic
  - Checks exact email matches first
  - Falls back to catch-all (`*@domain.com`) if no exact match
  - Recursively processes redirections
  - Marks redirected emails with `isRedirection` header
  - Uses cache for performance (configurable TTL and max entries)

**Redirection Logic Flow:**
1. Check exact redirection in database
2. If not found, check if local account exists
3. If not local, check catch-all redirection for domain
4. Return original recipient if no matches

**SMTP Security**
- `ValidRcptHandler` (ValidRcptHandler.java): SMTP fastfail handler for recipient validation
- Prevents sender impersonation by validating authenticated user matches "from" field

### Database Layer

**Custom Components:**
- `JDBCDataSourceModule`: Guice module for DataSource binding
- `MariaDbDataSourceProvider`: Provides Apache DBCP2 connection pooling
- Lock service for distributed coordination
- Custom `FOILEN_REDIRECTIONS` table for redirection mappings

**Tables:**
- `JAMES_DOMAIN`: Managed domains
- `JAMES_USER`: Email accounts with SHA-512 password hashes
- `FOILEN_REDIRECTIONS`: Email redirections (FROM_USER, FROM_DOMAIN, TO_EMAIL)
- Plus standard Apache James tables (mailbox, subscriptions, etc.)

### Ports (Container:Host mapping)

- 25:10025 - SMTP with StartTLS
- 110:10110 - POP3 with StartTLS
- 143:10143 - IMAP with StartTLS
- 465:10465 - SMTP with Socket TLS
- 587:10587 - SMTP with StartTLS
- 993:10993 - IMAP with Socket TLS

## Dependencies

Version properties defined in `gradle.properties`:
- Apache James: 3.6.0
- Spring Framework: 5.3.30 (for JdbcTemplate)
- BouncyCastle: 1.70 (cryptography, PEM handling)
- Guava: 32.1.2-jre
- MariaDB JDBC: 3.2.0
- Apache Commons DBCP2: 2.10.0 (connection pooling)
- Foilen Smalltools: 2.5.1 (utilities, file watching, JSON)
- H2 Database: 2.2.224 (test only)
- JUnit: 5.10.0 / 4.13.2
- Mockito: 5.6.0

## Key Design Patterns

**List Comparator Pattern:**
The codebase extensively uses `ListsComparator` from smalltools to synchronize database state:
- Compares sorted lists of existing vs desired entities
- Callback handlers for: `both()` (exists in both), `leftOnly()` (delete), `rightOnly()` (create)
- See `UpdateJamesService.java:46` for domains example

**Event Callback System:**
Configuration updates propagate via event callbacks registered with `ConfigurationService`

**Template-Based Configuration:**
FreeMarker templates generate James XML configs with dynamic values injected

**File Watching:**
Automatic configuration reloading without server restart via filesystem monitoring

## Development Notes

- Always start MariaDB before running tests or the application
- The application creates default test configuration if none exists (localhost.foilen-lab.com domain)
- Logs are written to `workdir/logs/`
- James config files are generated to `workdir/conf/` on startup
- Application monitors resource usage every 60 seconds
- Default JVM args: `-Xmx4g -XX:+HeapDumpOnOutOfMemoryError`
- Main class has memory monitoring via `ApplicationResourceUsageTools`

## Testing

Test files mirror the source structure in `src/test/java/`. Key test:
- `ExactAndCatchAllRedirectionsTest`: Tests redirection logic with various scenarios
- `JamesWorkDirManagementTest`: Tests configuration file generation

Use H2 in-memory database for testing (configured in test dependencies).
