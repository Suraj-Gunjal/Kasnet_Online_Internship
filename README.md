# My Internship Technologies Portal

> **Production-Ready Java EE (MVC) Web Application Module**  
> Designed for seamless integration into the hosted platform at [onlineinternship.in](https://www.onlineinternship.in/).  
> Configured with Indian Standard Time (IST / Asia/Kolkata, UTC+05:30), strict input validations, secure BCrypt authentication, and a clean professional UI.

---

## Table of Contents
1. [Tech Stack](#tech-stack)
2. [Prerequisites](#prerequisites)
3. [Quick Setup Guide (Run in 3 Minutes)](#quick-setup-guide-run-in-3-minutes)
4. [Pre-configured Test Credentials](#pre-configured-test-credentials)
5. [Automated Verification Test Suite](#automated-verification-test-suite)
6. [Alternative: Deploying to Standalone Tomcat](#alternative-deploying-to-standalone-tomcat)
7. [Application Sitemap & Routes](#application-sitemap--routes)
8. [Architecture & Project Structure](#architecture--project-structure)
9. [Indian Standard Time (IST) Configuration](#indian-standard-time-ist-configuration)
10. [Troubleshooting Guide](#troubleshooting-guide)
11. [Continuation & Documentation](#continuation--documentation)

---

## Tech Stack

| Layer | Technology | Details |
|---|---|---|
| **Language** | Java 11 / Java 17 / Java 21 | Compatible with Java 11+ LTS bytecode |
| **Framework** | Java Servlets 3.1 & JSP 2.3 | Standard Java EE (`javax.servlet`) specification |
| **Architecture** | MVC Pattern | Strict Model-View-Controller separation |
| **Database** | MySQL 5.7+ / 8.0+ | Compatible with MySQL Workbench 6.3+ (InnoDB, UTF-8) |
| **Persistence** | JDBC with `PreparedStatement` | SQL-injection safe, clean resource closing |
| **Security** | BCrypt (`jbcrypt:0.4`) | Salt rounds = 12, timing-attack safe |
| **Styling** | Per-Page Inline CSS3 & Clean SVGs | **Zero external CSS framework lock-in**, 100% responsive, zero cartoon emojis |
| **Build System** | Apache Maven 3.x | Build wrapper `mvn.bat` included in repository |
| **Timezone** | Indian Standard Time (IST) | `Asia/Kolkata` (UTC+05:30) enforced end-to-end |

---

## Prerequisites

Before setting up the project, make sure your device has:
1. **Java Development Kit (JDK 11 or higher)**
   - Check version: `java -version`
2. **MySQL Server 5.7 or 8.0** (running locally on port `3306`)
   - Check status: `mysql --version`
3. **Git** (optional, to clone repository)

---

## Quick Setup Guide (Run in 3 Minutes)

### Step 1: Clone / Open Project Directory
```powershell
# Open terminal in the project folder:
cd "d:\My Workspace\Kasnet_Online_Internship"
```

### Step 2: Initialize MySQL Database
For a brand-new installation only, open your MySQL client (Command Line or MySQL Workbench) and run `database.sql`.
For an existing installation, do not run that full script; apply only the assignment-instruction migration below.

```powershell
# Using MySQL Command Line:
mysql -u root -p < database.sql
```
*Or open `database.sql` in MySQL Workbench, select all (`Ctrl+A`), and click the Execute lightning button.*

This script automatically creates:
- The database: `online_internship`
- All 15 relational tables with foreign keys and indexes
- Seeded curriculum modules, sample assignments, and sample lectures.

### Step 3: Configure Database Password
Do not store database credentials in source files. In PowerShell, set the database settings in the same
terminal session used to launch the portal:

```powershell
$env:KASNET_DB_USER = "root"
$env:KASNET_DB_PASSWORD = "<your-local-MySQL-password>"
```

You can also configure `KASNET_DB_URL`, or use the JVM properties `kasnet.db.url`,
`kasnet.db.user`, and `kasnet.db.password`. For standalone Tomcat, configure these for the
Tomcat service account before restarting the service.

### Step 4: Run Locally (One Single Command)
We have included a configured embedded Tomcat runner in Maven. You do **not** need to install standalone Tomcat:

**On Windows (PowerShell / CMD):**
```powershell
.\mvn.bat tomcat7:run
```

**On macOS / Linux (if Maven is installed):**
```bash
mvn tomcat7:run
```

Wait until you see:
```text
INFO: Starting ProtocolHandler ["http-bio-8080"]
```

### Step 5: Open in Your Browser
Open your browser and navigate to:
👉 **`http://localhost:8080/internship/`**

*(To stop the server at any time, press `Ctrl + C` in the terminal).*

### Assignment instruction migration

For an existing installation, apply `database/migrations/20261021_assignment_instructions.sql` to the selected `online_internship` database. Do **not** run `database.sql` against an existing database; it contains destructive reset statements. For example, from PowerShell:

```powershell
Get-Content -Raw .\database\migrations\20261021_assignment_instructions.sql | mysql -u root -p online_internship
```

Instruction PDFs are stored outside the deployed web application at `<catalina.base>/uploads/assignment-instructions`. Change the `assignmentInstructionsUploadDir` context parameter in `src/main/webapp/WEB-INF/web.xml` to another absolute or Tomcat-base-relative upload directory when needed; the application places its `assignment-instructions` subdirectory under that path.

The assignment-instruction servlet allows a 5 MiB file in a 6 MiB multipart request. Student submission uploads
allow 10 MiB files in an 11 MiB request. Multipart text fields are read from servlet parts directly so upload
handling does not depend on Tomcat's URL-parameter parsing limit.

---

## Pre-configured Test Credentials

The application provides pre-configured credentials for quick testing:

| Role | Email | Password | Destination Dashboard |
|---|---|---|---|
| **Student (Test ID)** | `test@myinternshiptech.com` *(or `test@kasnet.in`)* | `Test@12345` | Student Portal (`/dashboard`) |
| **System Administrator** | `admin@myinternshiptech.com` *(or `admin@kasnet.in`)* | `Admin@12345` | Admin Console (`/admin`) |

You can also click **Register** on the landing page to register a fresh student account. New accounts are immediately persisted in your local MySQL database.

---

## Automated Verification Test Suite

You can verify database connectivity, validations, password hashing, and profile calculations in 5 seconds without starting the web browser:

```powershell
.\mvn.bat exec:java '-Dexec.mainClass=com.kasnet.util.SystemVerifier'
```

Output:
```text
==================================================
 MY INTERNSHIP TECHNOLOGIES - SYSTEM VERIFICATION
==================================================
[TEST 1] Verifying MySQL connection... PASSED (Connected to online_internship)
[TEST 2] Verifying Input Validations... PASSED (Phone 10-digits, Percentage 0-100, Email, Date verified)
[TEST 3] Verifying Hardcoded Student Test ID (test@myinternshiptech.com / test@kasnet.in)... PASSED
[TEST 4] Verifying Admin Account... PASSED (Authenticated as Admin)
[TEST 5] Verifying Admin User Monitoring Query... PASSED
[TEST 6] Verifying Profile Completeness Calculation... PASSED
[TEST 7] Verifying Education Percentage & Record Persistence... PASSED
==================================================
 ALL VERIFICATION TESTS PASSED SUCCESSFULLY!
==================================================
```

---

## Alternative: Deploying to Standalone Tomcat

If you are deploying to a standalone **Apache Tomcat 8.5 / 9.0** installation:

1. **Package the WAR file:**
   ```powershell
   .\mvn.bat clean package
   ```
2. **Locate the artifact:**
   `target/kasnet-internship-portal.war`
3. **Deploy:**
   Copy `target/kasnet-internship-portal.war` into your Tomcat's `webapps/` folder.
4. **Start Tomcat:**
   - Windows: `C:\path\to\tomcat\bin\startup.bat`
   - Linux/Mac: `/path/to/tomcat/bin/startup.sh`
5. **Access the portal:**
   `http://localhost:8080/kasnet-internship-portal/`

---

## Application Sitemap & Routes

| Path | Component | Access | Description |
|---|---|---|---|
| `/` or `/index.jsp` | `index.jsp` | Public | Landing page with highlights, curriculum tracks, and portal actions. |
| `/register` | `RegisterServlet` | Public | Student signup with 10-digit phone and email validation. |
| `/login` | `LoginServlet` | Public | Split-panel login with CAPTCHA and BCrypt authentication. |
| `/dashboard` | `DashboardServlet` | Student | Central student dashboard with 5 core feature cards and overview metrics. |
| `/profile` | `ProfileServlet` | Student | 5-section profile management with dynamic completeness score in **IST**. |
| `/assignments` | `AssignmentServlet` | Authenticated student | Assignment instructions, deadlines, submission uploads, and graded scores. |
| `/admin/assignments` | `AdminAssignmentServlet` | Admin only | Create, edit, publish, deactivate, and republish assignments. |
| `/assignment-instructions` | `AssignmentInstructionPdfServlet` | Authenticated student/admin | Serves an assignment's private instruction PDF. |
| `/lectures` | `LectureServlet` | Student | Video curriculum, duration tracker, and module completion markers. |
| `/question-bank` | `QuestionBankServlet` | Student | Subject Question Bank with dropdown (`Java`, `Python`, `Web`, `DBMS`, `Cloud`). |
| `/resume-analysis` | `ResumeAnalysisServlet`| Student | Resume document upload dropzone and ATS analysis placeholder. |
| `/admin` | `AdminServlet` | Admin | Clean user monitoring table, registration audit, and search filters. |
| `/logout` | `LogoutServlet` | Authenticated | Secure session termination and redirect to login. |

---

## Architecture & Project Structure

```text
Kasnet_Online_Internship/
├── pom.xml                                  # Maven dependencies & build configuration
├── database.sql                             # Full MySQL schema & seed data
├── README.md                                # Local setup & developer documentation
├── docs/
│   ├── PROJECT_PROGRESS.md                  # Comprehensive technical progress & handover guide
│   └── screenshots/                         # Reference mockups & logo_final.png
└── src/main/
    ├── java/com/kasnet/
    │   ├── controller/                      # Servlets (Controllers)
    │   │   ├── LoginServlet.java
    │   │   ├── RegisterServlet.java
    │   │   ├── LogoutServlet.java
    │   │   ├── DashboardServlet.java
    │   │   ├── ProfileServlet.java
    │   │   ├── PersonalDetailsServlet.java
    │   │   ├── EducationServlet.java
    │   │   ├── SkillsServlet.java
    │   │   ├── CertificationServlet.java
    │   │   ├── ProjectServlet.java
    │   │   ├── HackathonServlet.java
    │   │   ├── CompetitionServlet.java
    │   │   ├── AssignmentServlet.java
    │   │   ├── LectureServlet.java
    │   │   ├── QuestionBankServlet.java
    │   │   ├── ResumeAnalysisServlet.java
    │   │   └── AdminServlet.java
    │   ├── model/                           # JavaBeans / POJOs (Models)
    │   │   ├── User.java
    │   │   ├── StudentProfile.java
    │   │   ├── Education.java
    │   │   ├── Skill.java
    │   │   ├── Certification.java
    │   │   ├── Project.java
    │   │   ├── Hackathon.java
    │   │   ├── Competition.java
    │   │   ├── Assignment.java
    │   │   ├── Lecture.java
    │   │   └── DashboardStats.java
    │   ├── dao/                             # JDBC Data Access Objects
    │   │   ├── UserDAO.java
    │   │   ├── ProfileDAO.java
    │   │   ├── EducationDAO.java
    │   │   ├── SkillDAO.java
    │   │   ├── CertificationDAO.java
    │   │   ├── ProjectDAO.java
    │   │   ├── HackathonDAO.java
    │   │   ├── CompetitionDAO.java
    │   │   ├── AssignmentDAO.java
    │   │   ├── LectureDAO.java
    │   │   └── DashboardDAO.java
    │   ├── service/                         # Application Services
    │   │   ├── AuthenticationService.java
    │   │   ├── ProfileService.java
    │   │   └── FileUploadService.java
    │   └── util/                            # Utilities
    │       ├── DBConnection.java            # JDBC connection pool manager (Asia/Kolkata timezone)
    │       ├── PasswordUtil.java            # BCrypt hashing & verification
    │       ├── ValidationUtil.java          # 10-digit phone, regex email, date validation
    │       ├── SessionUtil.java             # Session auth verification
    │       └── SystemVerifier.java          # Offline automated test suite
    └── webapp/
        ├── index.jsp                        # Public landing page
        ├── login.jsp                        # Login view
        ├── register.jsp                     # Registration view
        ├── assets/
        │   └── images/
        │       └── logo.png                 # Official brand logo
        ├── views/
        │   ├── dashboard.jsp                # Student logged-in homepage
        │   ├── profile.jsp                  # 5-section profile view
        │   ├── assignments.jsp              # Assignments module
        │   ├── lectures.jsp                 # Lectures module
        │   ├── question-bank.jsp            # Question bank with subject selector
        │   ├── resume-analysis.jsp          # Resume analysis interface
        │   ├── admin.jsp                    # Admin monitoring console
        │   ├── error-404.jsp                # 404 handler
        │   ├── error-500.jsp                # 500 handler
        │   └── unauthorized.jsp             # Session expired handler
        └── WEB-INF/
            └── web.xml                      # Deployment descriptor (servlets, filters, IST timezone)
```

---

## Indian Standard Time (IST) Configuration

All user-visible timestamps (e.g., Account Created Date, Previous Login Date, Assignment Submission Date, and Admin Registration Logs) are configured to use **Indian Standard Time (IST / Asia/Kolkata, UTC+05:30)**:

1. **JDBC Connection (`DBConnection.java`):**
   ```text
   jdbc:mysql://localhost:3306/online_internship?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
   ```
2. **Servlet Descriptor (`web.xml`):**
   ```xml
   <context-param>
       <param-name>javax.servlet.jsp.jstl.fmt.timeZone</param-name>
       <param-value>Asia/Kolkata</param-value>
   </context-param>
   ```
3. **JSP Formatting Tag (`views/profile.jsp`):**
   ```jsp
   <fmt:timeZone value="Asia/Kolkata">
       <fmt:formatDate value="${user.createdAt}" pattern="dd-MM-yyyy hh:mm:ss a" /> IST
   </fmt:timeZone>
   ```

---

## Troubleshooting Guide

### 1. `Port 8080 already in use`
If port 8080 is used by another application (e.g., Oracle, Jenkins, another Tomcat):
- Change the port in `pom.xml` under `tomcat7-maven-plugin`:
  ```xml
  <configuration>
      <port>8085</port>
      <path>/internship</path>
  </configuration>
  ```
- Access via `http://localhost:8085/internship/`.

### 2. `Access denied for user 'root'@'localhost'`
- Check the password in `src/main/java/com/kasnet/util/DBConnection.java`.
- Test connecting in terminal: `mysql -u root -p`.

### 3. `Public Key Retrieval is not allowed`
- This occurs on MySQL 8.0+ when using `caching_sha2_password`. It is already handled in `DBConnection.java` by the `allowPublicKeyRetrieval=true` flag.

### 4. Background Java Process Locking Logs on Windows
If `mvn clean` errors with `Failed to delete target\tomcat\logs\...`:
- Stop any running Java process in PowerShell:
  ```powershell
  Get-Process -Name java | Stop-Process -Force
  ```
- Re-run `.\mvn.bat clean package`.

---

## Continuation & Documentation

For detailed historical logs, database schema column definitions, and design decisions, refer to:
- [`docs/PROJECT_PROGRESS.md`](docs/PROJECT_PROGRESS.md)
