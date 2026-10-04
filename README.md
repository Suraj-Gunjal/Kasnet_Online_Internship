# Kasnet Online Internship Portal

## Project Overview

A complete, production-quality **Student Internship Portal** built as a Java EE web application using Servlets, JSP, and MySQL. The module is designed to be integrated into the existing Kasnet internship website.

The portal provides student registration, login, a multi-section profile management system, assignment tracking, lecture viewing, and more — all wired end-to-end from JSP → Servlet → Service → DAO → MySQL.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | JSP, HTML5, CSS3 (inline per-page), JavaScript |
| Backend | Java 11, Java Servlets (javax.servlet 3.1) |
| Architecture | MVC (Model-View-Controller) |
| Database | MySQL 5.7+ / MySQL Workbench 6.3 compatible |
| DB Access | JDBC with PreparedStatement |
| Password Hashing | BCrypt (jbcrypt 0.4) |
| File Upload | javax.servlet multipart / Apache Commons FileUpload |
| Build Tool | Maven 3.x |
| Server | Apache Tomcat 8.5 / 9.x |

---

## Folder Structure

```
Kasnet_Online_Internship/
├── pom.xml                          ← Maven build file
├── database.sql                     ← Complete MySQL schema + sample data
├── README.md                        ← This file
├── docs/
│   └── PROJECT_TRACKER.md           ← Task tracking for AI continuation
└── src/
    └── main/
        ├── java/
        │   └── com/kasnet/
        │       ├── controller/      ← Servlets (MVC Controllers)
        │       │   ├── LoginServlet.java
        │       │   ├── RegisterServlet.java
        │       │   ├── LogoutServlet.java
        │       │   ├── DashboardServlet.java
        │       │   ├── ProfileServlet.java
        │       │   ├── PersonalDetailsServlet.java
        │       │   ├── EducationServlet.java
        │       │   ├── AchievementServlet.java
        │       │   ├── SkillsServlet.java
        │       │   ├── CertificationServlet.java
        │       │   ├── ProjectServlet.java
        │       │   ├── HackathonServlet.java
        │       │   ├── CompetitionServlet.java
        │       │   ├── ResumeUploadServlet.java
        │       │   ├── AssignmentServlet.java
        │       │   └── LectureServlet.java
        │       ├── model/           ← POJOs / JavaBeans
        │       │   ├── User.java
        │       │   ├── StudentProfile.java
        │       │   ├── Education.java
        │       │   ├── Achievement.java
        │       │   ├── Skill.java
        │       │   ├── Certification.java
        │       │   ├── Project.java
        │       │   ├── Hackathon.java
        │       │   ├── Competition.java
        │       │   ├── Assignment.java
        │       │   ├── Lecture.java
        │       │   └── DashboardStats.java
        │       ├── dao/             ← Data Access Objects
        │       │   ├── UserDAO.java
        │       │   ├── ProfileDAO.java
        │       │   ├── EducationDAO.java
        │       │   ├── AchievementDAO.java
        │       │   ├── SkillDAO.java
        │       │   ├── CertificationDAO.java
        │       │   ├── ProjectDAO.java
        │       │   ├── HackathonDAO.java
        │       │   ├── CompetitionDAO.java
        │       │   ├── AssignmentDAO.java
        │       │   ├── LectureDAO.java
        │       │   └── DashboardDAO.java
        │       ├── service/         ← Business Logic Layer
        │       │   ├── AuthenticationService.java
        │       │   ├── ProfileService.java
        │       │   └── FileUploadService.java
        │       └── util/            ← Utility Classes
        │           ├── DBConnection.java
        │           ├── PasswordUtil.java
        │           ├── ValidationUtil.java
        │           ├── FileUtil.java
        │           └── SessionUtil.java
        ├── webapp/
        │   ├── login.jsp            ← Login page
        │   ├── register.jsp         ← Registration page
        │   ├── WEB-INF/
        │   │   └── web.xml          ← Deployment descriptor
        │   ├── views/
        │   │   ├── dashboard.jsp    ← Student Dashboard
        │   │   ├── profile.jsp      ← Profile (multi-tab)
        │   │   ├── assignments.jsp  ← Assignment management
        │   │   ├── lectures.jsp     ← Lecture videos
        │   │   ├── resume-upload.jsp
        │   │   ├── error-404.jsp
        │   │   ├── error-500.jsp
        │   │   └── unauthorized.jsp
        │   └── uploads/
        │       ├── resumes/         ← PDF/DOC resume files
        │       └── photos/          ← Profile photos
        └── resources/
```

---

## Database Setup

### 1. Install MySQL 5.7+ or MySQL Workbench 6.3

### 2. Run the schema script

```sql
-- In MySQL Workbench or MySQL CLI:
source /path/to/database.sql;
-- OR
mysql -u root -p < database.sql
```

This creates:
- Database: `online_internship`
- All 15 tables with FKs, indexes, and sample data
- 2 sample assignments, 3 sample lectures

### 3. Verify

```sql
USE online_internship;
SHOW TABLES;
```

---

## JDBC Configuration

Edit `src/main/java/com/kasnet/util/DBConnection.java`:

```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/online_internship?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "YOUR_MYSQL_PASSWORD"; // ← change this
```

> **IMPORTANT**: Never commit real credentials to version control. Use environment variables or a `db.properties` file in production.

---

## How to Run

### Prerequisites
- Java JDK 11+
- Apache Tomcat 8.5 or 9.x
- MySQL 5.7+
- Maven 3.x

### Steps

1. **Clone / copy the project** into your workspace.

2. **Set up the database** (see Database Setup above).

3. **Configure DB credentials** in `DBConnection.java`.

4. **Build the WAR**:
   ```bash
   mvn clean package
   ```

5. **Deploy to Tomcat**:
   - Copy `target/kasnet-internship-portal.war` to Tomcat's `webapps/` directory.
   - OR use the Maven Tomcat plugin: `mvn tomcat7:run`

6. **Access the application**:
   ```
   http://localhost:8080/kasnet-internship-portal/login
   ```

### Import into Eclipse/IntelliJ

- **Eclipse**: File → Import → Existing Maven Projects → Select folder
- **IntelliJ**: File → Open → Select `pom.xml`

---

## Default / Test Credentials

> The database.sql includes a placeholder admin account. Create a real account via the registration page.

| Field | Value |
|---|---|
| Registration URL | `http://localhost:8080/kasnet-internship-portal/register` |
| Login URL | `http://localhost:8080/kasnet-internship-portal/login` |

Register as a **Student** to access the full portal.

---

## File Upload Configuration

Upload files are stored at:
```
{TOMCAT_WEBAPPS}/{app}/uploads/resumes/
{TOMCAT_WEBAPPS}/{app}/uploads/photos/
```

The real path is resolved at runtime using `request.getServletContext().getRealPath("/uploads/resumes/")`.

Configuration:
- Max file size: **5 MB** (set in `FileUtil.java` and `@MultipartConfig`)
- Allowed resume types: PDF, DOC, DOCX
- Allowed photo types: JPG, JPEG, PNG
- Filenames are UUID-based (safe, non-guessable)

> For production, consider mounting a persistent storage path instead of inside the webapp directory, and configure the path in `web.xml` context-params.

---

## Session Management

- Session timeout: **30 minutes** (configured in `web.xml`)
- Session attribute keys: managed centrally via `SessionUtil.java`
- After login: `userId`, `userName`, `userEmail`, `userRole` stored in session
- Logout: session invalidated via `LogoutServlet`
- All protected pages check session via `SessionUtil.requireLogin()`

---

## Security Features

- ✅ BCrypt password hashing (jbcrypt, ROUNDS=12)
- ✅ PreparedStatement everywhere (no SQL injection)
- ✅ Server-side + client-side validation
- ✅ Session-based authentication
- ✅ Authorization: userId taken from session, never from URL/form params
- ✅ Safe file upload: UUID filenames, type + size validation
- ✅ No stack traces exposed to users
- ✅ CAPTCHA on login and registration
- ✅ HTML entity escaping in output

---

## How to Integrate with Existing Website

1. **Copy Java source** (`src/main/java/com/kasnet/`) into the existing project's source tree.
2. **Copy JSP views** (`src/main/webapp/views/*.jsp`) into the existing webapp.
3. **Merge `web.xml`** servlet mappings into the existing `web.xml`.
4. **Run `database.sql`** on the existing database (creates new tables, won't touch existing ones).
5. **Add Maven dependencies** from `pom.xml` to the existing `pom.xml`.
6. **Configure `DBConnection.java`** to use the existing DB connection settings.
7. **Update context paths** in JSPs if the context path differs.

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Add `mysql-connector-java.jar` to Tomcat's `lib/` or Maven dependencies |
| `BCrypt class not found` | Ensure `jbcrypt-0.4.jar` is in WEB-INF/lib or Maven |
| Database connection refused | Verify MySQL is running on port 3306 |
| 500 error on JSP | Check Tomcat logs in `logs/catalina.out` |
| File upload fails | Ensure `uploads/` directory is writable by Tomcat |
| Session expires immediately | Check `session-timeout` in `web.xml` |
| JSTL tags not resolved | Ensure `jstl-1.2.jar` is in WEB-INF/lib |

---

## API / URL Reference

| URL | Method | Description |
|---|---|---|
| `/login` | GET/POST | Login page |
| `/register` | GET/POST | Registration |
| `/logout` | GET/POST | Logout |
| `/dashboard` | GET | Student dashboard |
| `/profile` | GET | Full profile (multi-tab) |
| `/personal-details` | POST | Save personal details |
| `/education` | POST | Save education details |
| `/achievements` | POST | Add/edit/delete achievement |
| `/skills` | POST | Save skills |
| `/certifications` | POST | CRUD certifications |
| `/projects` | POST | CRUD projects |
| `/hackathons` | POST | CRUD hackathons |
| `/competitions` | POST | CRUD competitions |
| `/resume-upload` | GET/POST | Resume management |
| `/assignments` | GET/POST | View/submit assignments |
| `/lectures` | GET/POST | View/complete lectures |

---

*Last updated: October 2026*
