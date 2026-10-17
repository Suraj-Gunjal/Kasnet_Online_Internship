# Developer Onboarding & Project Handover Context

> **Welcome to the My Internship Technologies Portal project!**  
> This document was prepared specifically for you so you can step into this codebase, understand all the architecture decisions, run the project locally within 2 minutes, and continue building without running into hidden traps.

---

## 1. Project Background & Vision

- **What is this project?**  
  A complete, production-grade student internship web application module built for **My Internship Technologies**.
- **The Big Picture (Integration Target):**  
  This module will ultimately be integrated into an already-hosted platform: **[`https://www.onlineinternship.in/`](https://www.onlineinternship.in/)**.
- **Why this matters for your code:**  
  Because it must merge cleanly into an existing hosted environment, **DO NOT** convert this into a throwaway demo, and **DO NOT** introduce monolithic frameworks (like Spring Boot or Hibernate) or global CSS libraries (like Bootstrap/Tailwind) that would collide with the parent website.

---

## 2. Golden Rules (Please Read Before Writing Any Code!)

| Rule | Rationale / Instruction |
|---|---|
| **1. Strict MVC Architecture** | Keep JSP files purely for presentation (`<c:...>`, `<fmt:...>`, HTML, JS). **Never** write SQL queries or heavy business logic in JSP scriptlets. Keep SQL inside `dao/` classes and application logic inside `service/` classes. |
| **2. Scoped Inline Styles Only** | Each JSP page has its own `<style>` block. **Do not create one giant global CSS file** or pull in external CSS frameworks (Bootstrap, Tailwind). The parent website already has its own global styles; scoped styles guarantee zero CSS collisions when merged. |
| **3. Professional UI & Zero Emojis** | The UI uses clean inline vector SVGs instead of cartoon emojis. Maintain this clean, professional aesthetic for all forms, cards, and navigation. |
| **4. No Profile Photo or Resume Upload** | As per client specification, profile photo upload and resume upload buttons were intentionally **removed** (or kept as non-blocking placeholders). Do not re-add upload buttons unless requested. |
| **5. Strict Validation Everywhere** | Phone numbers must be exactly 10 digits (`^[0-9]{10}$`), emails must be valid RFC 5322 format, percentages must be 0–100, and required fields cannot be empty. |
| **6. Indian Standard Time (IST)** | All dates and timestamps are standardized to **`Asia/Kolkata` (UTC+05:30)** across JDBC, `web.xml`, and JSP format tags. |
| **7. SQL Security** | Always use parameterized `PreparedStatement` in JDBC. **Never concatenate user input directly into SQL strings.** |

---

## 3. Tech Stack At A Glance

- **Backend:** Java 11 / 17 / 21, Java EE Servlets 3.1 (`javax.servlet`)
- **Frontend / Templating:** JSP 2.3, JSTL 1.2, Vanilla JavaScript, Scoped CSS3, SVG icons
- **Database:** MySQL 5.7+ / 8.0+ (InnoDB, UTF-8, fully compatible with MySQL Workbench 6.3)
- **Data Access:** Pure JDBC via `PreparedStatement`
- **Security:** `jBCrypt` (v0.4, Work Factor 12) for salted, timing-attack-safe password hashing
- **Build Tool:** Apache Maven 3.x (with local wrapper `mvn.bat`)
- **Server:** Apache Tomcat 7.0.47+ / 8.5 / 9.x (embedded Tomcat runner included)

---

## 4. Quick Setup Guide (Run in 2 Minutes)

### Step 1: Database Setup
1. Ensure your local MySQL server is running on port `3306`.
2. Open MySQL Workbench (or CLI) and execute the provided [`database.sql`](file:///d:/My%20Workspace/Kasnet_Online_Internship/database.sql) script:
   ```sql
   -- database.sql creates the database 'online_internship' and all tables with seed data
   SOURCE d:/My Workspace/Kasnet_Online_Internship/database.sql;
   ```
3. Check your database credentials in [`src/main/java/com/kasnet/util/DBConnection.java`](file:///d:/My%20Workspace/Kasnet_Online_Internship/src/main/java/com/kasnet/util/DBConnection.java):
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/online_internship?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
   private static final String USER = "root";
   private static final String PASSWORD = "root"; // <-- Change if your MySQL password differs
   ```

### Step 2: Build & Run Embedded Tomcat
In the project root folder, run:
```powershell
.\mvn.bat tomcat7:run
```
*(Or `mvn tomcat7:run` if Maven is in your system PATH).*

### Step 3: Access Application
Open your browser and navigate to:
👉 **`http://localhost:8080/internship/`**

### Pre-Configured Test Credentials:
- **Student Account:**
  - Email: `test@kasnet.in`
  - Password: `Test@12345`
  - Redirects to: `/internship/dashboard`
- **Admin Account:**
  - Email: `admin@kasnet.in`
  - Password: `Admin@12345`
  - Redirects to: `/internship/admin`

---

## 5. Project Directory & Package Structure

```
Kasnet_Online_Internship/
├── pom.xml                               # Maven project dependencies & plugin config
├── database.sql                          # MySQL schema, table definitions & seed accounts
├── mvn.bat                               # Windows Maven execution wrapper
├── README.md                             # Repository setup documentation
├── HANDOVER.md                           # (This file) Developer context guide
├── docs/
│   ├── PROJECT_PROGRESS.md               # Continuous architectural log & milestone tracker
│   └── screenshots/                      # UI reference screenshots & logo_final.png
└── src/
    └── main/
        ├── java/com/kasnet/
        │   ├── model/                    # JavaBean POJOs (DTOs)
        │   │   ├── User.java             # User entity (ID, email, role, hash, last_login)
        │   │   ├── StudentProfile.java   # Profile entity with completeness %
        │   │   ├── Education.java        # SSC, HSC, Diploma, Degree records
        │   │   ├── Certification.java    # Certificates (name, issuer, date, url)
        │   │   ├── Project.java          # Academic/internship projects
        │   │   ├── Hackathon.java        # Hackathon participation details
        │   │   ├── Competition.java      # Inter-college competitions
        │   │   ├── Skill.java            # Technical & soft skills
        │   │   ├── Achievement.java      # Honors & awards
        │   │   ├── Assignment.java       # Practical coding tasks
        │   │   ├── Lecture.java          # Video lecture items
        │   │   └── DashboardStats.java   # Aggregated numbers for dashboard widgets
        │   │
        │   ├── dao/                      # JDBC Data Access Objects (100% PreparedStatements)
        │   │   ├── UserDAO.java          # Authenticate, register, email check, timestamp
        │   │   ├── ProfileDAO.java       # CRUD for personal info & completeness
        │   │   ├── EducationDAO.java     # CRUD for education records
        │   │   ├── CertificationDAO.java # CRUD for certificates
        │   │   ├── ProjectDAO.java       # CRUD for projects
        │   │   ├── HackathonDAO.java     # CRUD for hackathons
        │   │   ├── CompetitionDAO.java   # CRUD for competitions
        │   │   ├── SkillDAO.java         # CRUD for skills
        │   │   ├── AchievementDAO.java   # CRUD for achievements
        │   │   ├── AssignmentDAO.java    # Fetch assignments by user/role
        │   │   ├── LectureDAO.java       # Fetch syllabus lectures
        │   │   └── DashboardDAO.java     # Fetch KPI statistics
        │   │
        │   ├── service/                  # Business Logic Layer
        │   │   ├── AuthenticationService.java  # BCrypt verification, login date update
        │   │   ├── ProfileService.java         # Profile completeness calculation (0-100%)
        │   │   └── FileUploadService.java      # Safe file upload handling
        │   │
        │   ├── controller/               # Java Servlets (MVC Routing & Request Handling)
        │   │   ├── LoginServlet.java           # /login (GET view, POST authenticate)
        │   │   ├── RegisterServlet.java        # /register (GET view, POST register)
        │   │   ├── LogoutServlet.java          # /logout (Session invalidation)
        │   │   ├── DashboardServlet.java       # /dashboard (Logged-in homepage)
        │   │   ├── ProfileServlet.java         # /profile (Main 5-section profile view)
        │   │   ├── PersonalDetailsServlet.java # /personal-details (Save personal info)
        │   │   ├── EducationServlet.java       # /education (Add/delete education)
        │   │   ├── CertificationServlet.java   # /certifications (Add/delete certs)
        │   │   ├── ProjectServlet.java         # /projects (Add/delete projects)
        │   │   ├── HackathonServlet.java       # /hackathons (Add/delete hackathons)
        │   │   ├── CompetitionServlet.java     # /competitions (Add/delete competitions)
        │   │   ├── SkillsServlet.java          # /skills (Add/delete skills)
        │   │   ├── AssignmentServlet.java      # /assignments (Student tasks)
        │   │   ├── LectureServlet.java         # /lectures (Curriculum viewer)
        │   │   ├── QuestionBankServlet.java    # /question-bank (Subject-wise preparation)
        │   │   ├── ResumeAnalysisServlet.java  # /resume-analysis (ATS evaluation portal)
        │   │   └── AdminServlet.java           # /admin (Admin monitoring table)
        │   │
        │   └── util/                     # Utilities & Helpers
        │       ├── DBConnection.java     # JDBC Singleton with IST timezone
        │       ├── PasswordUtil.java     # BCrypt hashing & checking
        │       ├── SessionUtil.java      # Check login state & role authorization
        │       ├── ValidationUtil.java   # Centralized regex & field validation
        │       ├── FileUtil.java         # Path sanitization & file types
        │       └── SystemVerifier.java   # Automated diagnostic runner
        │
        └── webapp/                       # Web Presentation Root
            ├── index.jsp                 # Public Landing Page (mirroring onlineinternship.in)
            ├── login.jsp                 # Sign-in page with SVG icons & CAPTCHA
            ├── register.jsp              # Student registration with strict client checks
            ├── assets/
            │   └── images/logo.png       # Official Kasnet portal branding
            ├── views/                    # Protected Views (Dispatched from Servlets)
            │   ├── dashboard.jsp         # Student dashboard & quick access cards
            │   ├── profile.jsp           # 5-section tabbed profile & live progress bar
            │   ├── assignments.jsp       # Practical project assignments
            │   ├── lectures.jsp          # Structured video lectures
            │   ├── question-bank.jsp     # Subject dropdown interview questions
            │   ├── resume-analysis.jsp   # Clean ATS resume analysis placeholder
            │   └── admin.jsp             # Clean administrative user monitoring table
            └── WEB-INF/
                ├── web.xml               # Servlet mappings, security, IST timezone context
                └── tld/                  # Bundled JSTL 1.2 TLDs (c.tld, fmt.tld, fn.tld)
```

---

## 6. Current Feature Status & How Things Work

### 1. Landing Page (`index.jsp` & `/`)
- Derived directly from `https://www.onlineinternship.in/`.
- Shows internship tracks (Java Full Stack, Python/ML, Web Tech, Cloud/DevOps).
- Dynamic: If user is logged in, buttons change to "Go to Dashboard".
- If redirected from registration (`/?registered=success`), a clean modal popup displays: *"Congratulations! Account created successfully."*

### 2. Registration (`register.jsp` & `RegisterServlet`)
- Self-registration is strictly for **Students** (Admin option removed).
- Validations:
  - Mobile: Exactly 10 digits (`^[0-9]{10}$`).
  - Email: RFC 5322 regex + checks MySQL for existing account.
  - Percentage: Range 0–100.
  - Required fields cannot be empty.
- After success, redirects user to `index.jsp?registered=success` **without** auto-logging in.

### 3. Login (`login.jsp` & `LoginServlet`)
- Authenticates against BCrypt password hashes in MySQL `users` table.
- Stores `userId`, `userEmail`, `userRole`, and `userName` in `HttpSession`.
- Updates `last_login` timestamp in IST.
- Routes role: `STUDENT` &rarr; `/dashboard`, `ADMIN` &rarr; `/admin`.

### 4. Student Profile (`/profile` & `profile.jsp`)
- Reorganized into **5 clean sections**:
  1. **Personal Information:** Name, DOB, Phone, College, City, Stream, Referral.
  2. **Education Details:** SSC, HSC/Diploma, Graduation, Post-Graduation.
  3. **Certifications & Skills:** Certifications with credential links, technical/soft skills.
  4. **Projects:** Academic and internship projects.
  5. **Additional Activities:** Hackathons and Inter-College Competitions.
- **Profile Completeness Bar:** Fully functional! Handled in `ProfileService.java`. It calculates a weighted percentage based on completed fields (required sections contribute most, optional sections don't block progress).
- **Timezone:** Displays `Account Created Date` and `Previous Login Date` formatted in Indian Standard Time (`dd-MM-yyyy hh:mm:ss a IST`).

### 5. Logged-in Modules
- **Assignments (`/assignments`):** Tracks practical project assignments with status (Pending/Submitted/Approved).
- **Lectures (`/lectures`):** Curriculum syllabus with lecture viewer.
- **Question Bank (`/question-bank`):** Contains subject dropdown (Java, Python, Web Development, SQL Database). Each selection routes to its subject-specific view.
- **Resume Analysis (`/resume-analysis`):** Clean, professional ATS analysis portal (placeholder layout ready for scoring algorithms).
- **Admin View (`/admin`):** Lightweight monitoring table listing all registered students, emails, colleges, and registration dates.

---

## 7. Key Engineering Decisions & "Gotchas" (Save Hours of Debugging!)

### ⚠️ Gotcha #1: Tomcat Bytecode Scanning & Multi-Release JARs
- **The Issue:** Modern libraries compiled for Java 9+ have `module-info.class` inside `META-INF/versions/9/`. Older Tomcat BCEL bytecode scanners crash with `ClassFormatException: Invalid byte tag in constant pool: 19`.
- **The Fix:** We pruned unnecessary dependencies (`gson`, `commons-fileupload`, `commons-io`) and pinned SLF4J to stable LTS `1.7.36`.
- **Rule:** If adding any new dependency to `pom.xml`, make sure it does not introduce unneeded modular JARs, or verify it runs cleanly with `.\mvn.bat tomcat7:run`.

### ⚠️ Gotcha #2: JSTL TLD Bundling
- In `tomcat7:run` mode, embedded Tomcat runs directly from `src/main/webapp/`.
- We extracted `c.tld`, `fmt.tld`, and `fn.tld` into [`src/main/webapp/WEB-INF/tld/`](file:///d:/My%20Workspace/Kasnet_Online_Internship/src/main/webapp/WEB-INF/tld/).
- This guarantees JSTL tags resolve immediately without scanning classpath JARs. **Do not delete the `WEB-INF/tld` folder.**

### ⚠️ Gotcha #3: Windows Background Java File Lock
- If you run `.\mvn.bat clean` and see:
  `Failed to delete ... target\tomcat\logs\access_log...`
- It means a background `java.exe` is still holding the log file.
- **Fix:** In PowerShell, simply run:
  ```powershell
  Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
  ```

### ⚠️ Gotcha #4: Indian Standard Time (IST)
- The database connection string has `serverTimezone=Asia/Kolkata`.
- `web.xml` has `<param-name>javax.servlet.jsp.jstl.fmt.timeZone</param-name><param-value>Asia/Kolkata</param-value>`.
- Any new date/time fields formatted in JSP should use `<fmt:timeZone value="Asia/Kolkata">`.

---

## 8. Where to Pick Up / Potential Next Tasks

Depending on what your current sprint or manager asks for, here are the natural next areas to work on:

1. **Question Bank Content:**
   - Add database tables or structured questions/answers for Java, Python, Web Development, and SQL.
2. **Resume Analysis Logic:**
   - Implement keyword extraction or scoring algorithms inside `ResumeAnalysisServlet.java`.
3. **Assignment Submission:**
   - Implement submission handling (e.g. GitHub link submission or code input) in `AssignmentServlet.java`.
4. **Parent Site Integration:**
   - Review header/navigation bar to align with the final hosting environment on `onlineinternship.in`.

---

## 9. Quick Diagnostic Tool

Whenever you want to test if the database, JDBC, password hashing, and DAOs are working properly without launching the browser, run our built-in verifier:

```powershell
.\mvn.bat test-compile exec:java -Dexec.mainClass="com.kasnet.util.SystemVerifier"
```

---

*Good luck with the project! If you are using an AI assistant (like Claude, Gemini, or ChatGPT), simply share this `HANDOVER.md` file with it and it will have 100% of the context needed to help you write code.*
