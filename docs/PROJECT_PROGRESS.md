# My Internship Technologies Portal — Comprehensive Project Progress & Handover Guide

> **Document Purpose:**  
> This document records all architectural decisions, components implemented, validations, database structures, and testing results for the **My Internship Technologies Portal**. Any AI model, senior engineer, or team member can consult this document to immediately understand the project state and seamlessly resume work where it was left off.

---

## 1. Project Overview & Architecture

- **Project Name:** My Internship Technologies Portal
- **Nature of Application:** Production-grade Java EE MVC Web Application module designed for seamless integration into an existing hosted student internship portal (`https://www.onlineinternship.in/`).
- **Architecture Pattern:** Strict **Model-View-Controller (MVC)**
  - **Model:** POJOs / JavaBeans (`com.kasnet.model.*`)
  - **View:** JSP 2.3 with JSTL (`src/main/webapp/views/*`, `index.jsp`, `login.jsp`, `register.jsp`)
  - **Controller:** Java Servlets (`com.kasnet.controller.*`)
  - **Service Layer:** Business operations (`com.kasnet.service.*`)
  - **DAO Layer:** JDBC with `PreparedStatement` and connection cleanup (`com.kasnet.dao.*`)
  - **Utility Layer:** Validation, Session, Password (BCrypt), DB connection (`com.kasnet.util.*`)

---

## 2. Technology Stack & Environment

| Layer | Component | Details |
|---|---|---|
| **Language** | Java 11 / Java 21 | Compatible with Java 11+ runtime |
| **Frameworks** | Java Servlets 3.1 (`javax.servlet`) | Java EE standard (Tomcat 8.5 / 9.0 compatible) |
| **Presentation** | JSP 2.3, JSTL 1.2, HTML5, Vanilla JavaScript | **Inline `<style>` per page strictly maintained** (no shared monolithic CSS or external CSS framework lock-in) |
| **Database** | MySQL 5.7+ / 8.0 | Compatible with **MySQL Workbench 6.3**; utf8mb4 InnoDB tables |
| **Security** | BCrypt (`org.mindrot:jbcrypt:0.4`) | Salt rounds = 12; salted hashes; timing-attack safe |
| **Build Tool** | Apache Maven 3.x | Build wrapper script `mvn.bat` included; WAR artifact packaged at `target/kasnet-internship-portal.war` |
| **Server** | Apache Tomcat 7.0.47+ / 8.5 / 9.x | Context deployment descriptor configured via `web.xml`; bundled TLDs in `WEB-INF/tld` |
| **Dependencies** | Modern LTS / Clean Dependencies | Pruned unused multi-release jars (`gson`, `commons-fileupload`); SLF4J pinned to stable LTS `1.7.36` |

---

## 3. End-to-End User Flow & Implemented Features

### A. Public Landing Page (`index.jsp`)
- Accessible before logging in at root URL `/` (configured as primary welcome file).
- Clean, professional design derived from `https://www.onlineinternship.in/`.
- Clear navigation bar with **Login** and **Register** buttons (or **My Dashboard** if already logged in).
- Hero section explaining practical online internship tracks with call-to-action buttons.
- Statistics Bar: 15,000+ Students Mentored, 100+ Live Projects, 50+ Industry Mentors, 100% Self-Paced.
- Industry Internship Tracks: Java Full Stack, Python & Machine Learning, Web Technologies, Cloud & DevOps.
- How It Works 4-step workflow: Profile Setup &rarr; Lectures &amp; Question Bank &rarr; Practical Assignments &rarr; Verified Certification.
- **Registration Success Modal Popup:** Triggered automatically when redirected from registration (`?registered=success`), showing:  
  *"Congratulations! Account created successfully."* with *"Proceed to Login"* and *"Close"* actions.

### B. Registration Module (`register.jsp` & `RegisterServlet.java`)
- Self-registration restricted to **Student** role (Admin option removed from public registration).
- **Academic Information Reordered strictly as:**
  1. **College Name:** Text field with institution validation.
  2. **Stream:** Dropdown (Graduation, Post Graduation, Diploma, Engineering, etc.).
  3. **Branch Name:** Comprehensive dropdown covering all engineering/technical branches (Computer Engineering, AI & Machine Learning, Data Science, IT, Mechanical, Electrical, Civil, Robotics, Biotechnology, etc.).
  4. **City:** Student's current residing or college city.
- Enforces strict client-side and server-side validation:
  - **Phone:** Exactly 10 numeric digits (`/^[0-9]{10}$/`).
  - **Email:** RFC 5322 regex validation and duplicate email check in MySQL.
  - **Required fields:** First Name, Last Name, Mobile, Email, Password, Confirm Password, College Name, Stream, Branch Name, City.
  - **Password Strength & Match:** Minimum 8 characters with strength indicator and matching confirmation.
  - **CAPTCHA:** Server/client CAPTCHA verification to prevent automated bot signups.
- **Enterprise-Grade UI Styling:** All emojis removed in favor of professional inline vector SVG icons (User, Phone, Email, Lock, Eye Toggle, College, Location, Shield, Refresh) and clean typography.
- After successful registration, user is redirected to the landing page (`/?registered=success`) displaying the congratulatory popup **without automatically logging them in**.

### C. Login Module (`login.jsp`, `LoginServlet.java` & `AuthenticationService.java`)
- Clean split-panel login form with CAPTCHA and role selection.
- **Official Brand Logo Integration:** Integrated official logo image (`assets/images/logo.png`, imported from `docs/screenshots/logo_final.png`) in the branding panel across Login, Register, Landing page, Dashboard, and internal views.
- **Enterprise-Grade UI Styling:** All emojis removed in favor of clean inline vector SVGs for features and alert indicators.
- Server-side credentials validation against MySQL with BCrypt comparison.
- **Hardcoded Test Student Account:**
  - **Email:** `test@kasnet.in`
  - **Password:** `Test@12345`
  - **Role:** `STUDENT`
  - Guaranteed to authenticate whether database is freshly initialized or populated.
- **Admin Account:**
  - **Email:** `admin@kasnet.in`
  - **Password:** `Admin@12345`
  - **Role:** `ADMIN`
- **Login Successful Popup:** On successful credential check, displays a clean *"Login successful"* popup modal before guiding the user to the homepage/dashboard.
- Prevents session fixation by regenerating session IDs on login.

### D. Admin Monitoring Module (`admin.jsp` & `AdminServlet.java`)
- Accessible at `/admin` only for authenticated users with role `ADMIN`.
- Simple, non-bloated monitoring console:
  - Metric Summary: Total Registered Users, Total Students, System Administrators.
  - Data Table: User ID, Full Name, Email, Mobile, Role Badge, Stream, College Name, City, Registration Timestamp, Status.
  - Instant client-side search/filter by name, email, college, or stream.

### E. Logged-in Homepage / Dashboard (`views/dashboard.jsp` & `DashboardServlet.java`)
- Centralized student workspace providing access to the **5 Core Sections**:
  1. **Profile** (`/profile`): Direct link with real-time profile completion score.
  2. **Assignments** (`/assignments`): Assignment submission counts (submitted vs. pending) and upload portal.
  3. **Lectures** (`/lectures`): Video completion metrics and curated module access.
  4. **Question Bank** (`/question-bank`): Has an embedded **Subject Dropdown** (`Java`, `Python`, `Web`, `DBMS`, `Cloud`) with a "Go" action to jump straight into subject-specific question banks.
  5. **Resume Analysis** (`/resume-analysis`): Direct link to the resume ATS review section.
- Student Account Details summary grid.
- Dynamic recalculation of completion score upon every page visit.

### F. Subject Question Bank Module (`views/question-bank.jsp` & `QuestionBankServlet.java`)
- Accessible at `/question-bank`.
- Features a **Subject Dropdown Selector**:
  - `java`: Java Full Stack Development (Core Java, Collections, Multithreading, Servlets, JSP, JDBC, Design Patterns).
  - `python`: Python & Machine Learning (Data Structures, OOP, NumPy, Pandas, Scikit-Learn).
  - `web`: Web Development & Frontend (HTML5, Responsive CSS3, JavaScript ES6+, DOM, REST APIs).
  - `db`: Database Management Systems (Relational Modeling, Normalization, Queries, Joins, Transactions).
  - `cloud`: Cloud Computing & DevOps (Virtualization, Cloud Storage, Docker Containers, CI/CD).
- Selecting any subject instantly redirects/renders the dedicated basic question bank view for that subject.
- Overview catalog mode displays all available subject tracks when no subject is selected.

### G. Resume Analysis Module (`views/resume-analysis.jsp` & `ResumeAnalysisServlet.java`)
- Accessible at `/resume-analysis`.
- Clean UI layout with dropzone placeholder for resume upload (PDF/DOCX max 5MB).
- Backend functionality intentionally kept blank for future AI model integration as requested.
- Informational cards on planned ATS score evaluation, keyword gap analysis, and formatting review.

### H. Reorganized 5-Section Student Profile (`views/profile.jsp` & `ProfileServlet.java`)
All original fields are preserved and organized into **exactly 5 sections**:
1. **Personal Information** (`/profile?tab=personal`):
   - **Part A - Personal Details:** First Name, Last Name, Gender, DOB (`type="date"`), Mobile (validated to 10 digits), Email (readonly), Address, City, State.
   - **Part B - Skills (Optional):** Technical Skills, Soft Skills, Domain Proficiencies.
   - **Part C - Achievements (Optional):** Certifications, Projects, Extra-Curricular Activities, and saved achievements table with delete action.
2. **Education** (`/profile?tab=education`):
   - **Engineering Year-wise Performance (Optional):** Positioned at the very top of the Education tab, strictly before the 10th and 12th sections.
     - Supports: First Year, Second Year, Third Year, Final Year.
     - Evaluation Type: Percentage (0-100%) or CGPA (0-10.0 scale).
     - Form fields: Year Level, Evaluation Type, Score/CGPA, Passing Year, College Name.
     - Dynamic frontend & backend validation adapted to evaluation type.
     - **Tabular Display:** Displays all recorded engineering year performances with formatted score badges.
     - **Interactive Edit & Delete:** In-place edit form populator (`editEngMark()`) and one-click deletion with confirmation.
   - Form sections for SSC/10th, HSC/12th, Diploma, Graduation, and Master.
   - Strict range validation on percentage inputs (`min="0" max="100" step="0.01"`).
   - Sanity validation on passing year inputs (`min="1950" max="2035"`).
3. **Certification** (`/profile?tab=certifications`):
   - Add Certification form (Name, Provider, Technology, Credential ID, Dates, Credential URL) and table of saved certifications. (Optional)
4. **Projects** (`/profile?tab=projects`):
   - Add Project form (Name, Tech Stack, Role, Type, Team Size, Dates, GitHub URL, Description) and table of saved projects. (Optional)
5. **Additional Activities** (`/profile?tab=activities`):
   - **A. Hackathons (Optional):** Hackathon Name, Organizer, Date, Position/Result, Team Name, Description.
   - **B. Inter-College Project Competitions (Optional):** Competition Name, Host College, Date, Project Name, Result, Team Name, Description.
- **Silhouette Avatar Placeholder:** Photo upload and resume upload buttons are removed as requested; clean silhouette avatar is displayed.
- **Profile Completeness Bar:** Fully functional and dynamically calculated in `ProfileDAO.calculateProfileCompletion()`:
  - Basic Account: 20%
  - Personal Details: 20%
  - Education Records: 25%
  - Skills: 15%
  - Projects: 10%
  - Certifications or Activities: 10%
  - Total = 100%. Optional sections do not block saving or penalize completion unnecessarily.

---

## 4. Database Schema Structure (`database.sql`)

The database name is `online_internship` (compatible with MySQL 5.7+ / 8.0 and MySQL Workbench 6.3).

### Key Tables:
1. `users`: Stores user identity, BCrypt password hash, role (`STUDENT`, `ADMIN`), stream, `branch` (Engineering/technical branch), college, city, mobile, timestamps.
2. `student_profiles`: 1-to-1 profile record linked to `users.user_id` storing gender, date_of_birth, address, state, `profile_completion`, previous_login, last_login.
3. `engineering_year_marks`: Stores year-wise engineering evaluation (`First Year`, `Second Year`, `Third Year`, `Final Year`), score type (`Percentage`, `CGPA`), score value, passing year, and college name.
4. `education_details`: Stores educational records by level (`SSC`, `HSC`, `Graduation`, `Master`, `Diploma`) with percentage and passing year.
5. `skills`: Stores technical, soft, and domain skills.
6. `certifications`: Stores verified certifications, providers, and verification URLs.
7. `projects`: Stores portfolio projects, technologies, and GitHub links.
8. `hackathons`: Stores hackathon participation and awards.
9. `intercollege_competitions`: Stores project exhibition entries and prizes.
10. `achievements`: Stores legacy achievements and extra-curricular highlights.
11. `assignments` & `assignment_submissions`: Stores internship assignment briefs and student submissions.
12. `lectures` & `lecture_progress`: Stores video lectures and student completion progress.
13. `login_activity`: Audit log of user login sessions and timestamps.

---

## 5. Test Credentials

| Role | Email | Password | Intended Page |
|---|---|---|---|
| **Hardcoded Student Test ID** | `test@myinternshiptech.com` *(or `test@kasnet.in`)* | `Test@12345` | `/dashboard` (Student Portal) |
| **Admin Account** | `admin@myinternshiptech.com` *(or `admin@kasnet.in`)* | `Admin@12345` | `/admin` (Monitoring Console) |
| **Registered Student** | `surajgunjaldev@gmail.com` | Registered Password | `/dashboard` |

---

## 6. How to Run Locally

### Prerequisites:
- Java JDK 11 or higher (JDK 21 installed)
- MySQL 8.0 running locally on port 3306 (user `root`, password configured in `DBConnection.java`)
- Apache Tomcat 8.5 / 9.0 (or Tomcat embedded / WAR deploy)

### Steps to Build & Run:
1. **Database Setup:**
   ```powershell
   mysql -u root -p < database.sql
   ```
2. **Build the WAR File:**
   ```powershell
   .\mvn.bat clean package
   ```
   The build produces: `target/kasnet-internship-portal.war`.
3. **Run via Embedded Maven Tomcat Plugin (Recommended & Quickest):**
   ```powershell
   .\mvn.bat tomcat7:run
   ```
   Access the application in your browser at:
   ```
   http://localhost:8080/internship/
   ```

4. **Run via Standalone Tomcat (8.5 / 9.0):**
   - Copy `target/kasnet-internship-portal.war` into Tomcat's `webapps/` directory.
   - Start Tomcat: `bin/startup.bat`.
   - Access the application in browser:
     ```
     http://localhost:8080/kasnet-internship-portal/
     ```

5. **Run System Verifier Test Suite:**
   ```powershell
   .\mvn.bat exec:java '-Dexec.mainClass=com.kasnet.util.SystemVerifier'
   ```

---

## 7. Verification Test Results

```
==================================================
 KASNET INTERNSHIP PORTAL - SYSTEM VERIFICATION
==================================================
[TEST 1] Verifying MySQL connection... PASSED (Connected to online_internship)
[TEST 2] Verifying Input Validations... PASSED (Phone 10-digits, Percentage 0-100, Email, Date verified)
[TEST 3] Verifying Hardcoded Student Test ID (test@kasnet.in)... PASSED (Authenticated as Student)
[TEST 4] Verifying Admin Account (admin@kasnet.in)... PASSED (Authenticated as Admin)
[TEST 5] Verifying Admin User Monitoring Query... PASSED (Retrieved 2 registered accounts)
[TEST 6] Verifying Profile Completeness Calculation... PASSED (Dynamic Score calculated)
[TEST 7] Verifying Education Percentage & Record Persistence... PASSED (Education record saved with 88.5%)
==================================================
 ALL VERIFICATION TESTS PASSED SUCCESSFULLY!
==================================================
```

---

## 8. Summary for Future Work & Integration

- All JSPs use **inline `<style>` tags**, adhering strictly to the architectural rule to avoid giant monolithic CSS files.
- Servlets, DAOs, and Models are cleanly separated following standard MVC.
- When integrating with the live host website:
  1. Set the database URL, username, and password in `com.kasnet.util.DBConnection.java` (or extract to environment variables / JNDI DataSource).
  2. The WAR is ready for zero-downtime deployment into Apache Tomcat.
  3. Resume Analysis parsing engine can be wired directly into `ResumeAnalysisServlet.java`.
- **Developer Handover & Continuity:**
  - A dedicated developer handover guide has been created at [`HANDOVER.md`](file:///d:/My%20Workspace/Kasnet_Online_Internship/HANDOVER.md) with complete architectural guidelines, setup steps, gotchas, and roadmap for team members and AI assistants.
