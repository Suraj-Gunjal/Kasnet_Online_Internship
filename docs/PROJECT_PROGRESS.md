# Kasnet Online Internship Portal — Project Progress & Engineering Guide

> **Document Purpose:**  
> This document stores all the project tasks completed, architecture decisions, database structure, and integration instructions so that any AI model, senior developer, or team member can immediately understand the current state of the application and continue work seamlessly without any missing context.

---

## 1. Project Overview & Context

- **Application Name:** Kasnet Online Internship Portal
- **Nature of Project:** A modular, production-ready Student Internship Portal module developed from scratch to be integrated into an already-hosted internship website.
- **Key Flow:**
  1. Student / Admin lands on `login.jsp`.
  2. New user registers on `register.jsp`.
  3. Credentials securely stored in MySQL with **BCrypt (12 rounds)** password hashing.
  4. Authentication establishes a secure HTTP session (`userId`, `userName`, `userEmail`, `userRole`).
  5. Authenticated student is guided to **Dashboard** (`/dashboard`) and **Student Profile** (`/profile`).
  6. Multi-tab profile management:
     - **Personal Details** (First Name, Last Name, Gender, DOB, Mobile, Email [readonly], Address, City, State)
     - **Education Details** (SSC, HSC, Graduation, Master, Diploma)
     - **Achievements** (Certifications, Projects, Extra-Curricular Activities)
     - **Skills** (Technical Skills, Soft Skills, Domain Skills)
     - **Certifications** (Dedicated CRUD section)
     - **Projects** (Dedicated CRUD section: Academic, Personal, Professional, Open Source)
     - **Additional Activities** (A. Hackathons, B. Inter-College Project Competitions)
  7. **Dynamic Profile Completion Calculation** stored in database and displayed in sidebar progress bar.
  8. **Assignments Module** (`/assignments`): View due dates, marks, statuses (Pending, Submitted, Late, Evaluated), and file submission upload.
  9. **Lecture Videos Module** (`/lectures`): Curated lectures, video player modal, progress tracking, and mark-as-completed action.
  10. **Special Client Requirement:** Profile photo and resume upload buttons were explicitly removed from the student profile UI per user instruction, while maintaining database and backend compatibility.

---

## 2. Mandatory Technology Stack

| Layer | Component | Specification |
|---|---|---|
| **Frontend** | JSP 2.3, HTML5, JavaScript (Vanilla ES6) | **Inline `<style>` per page strictly maintained** (no external framework dependency) |
| **Backend** | Java 11, Java Servlets 3.1 (`javax.servlet`) | Strict MVC architecture; business logic isolated in DAO/Service layers |
| **Database** | MySQL 5.7+ / 8.0 | Compatible with **MySQL Workbench 6.3**; standard InnoDB tables with utf8mb4 |
| **Connectivity** | JDBC with `PreparedStatement` | Centralized `DBConnection.java`; zero raw string concatenations (SQL-injection immune) |
| **Security** | BCrypt (`jbcrypt 0.4`) | Salt rounds = 12; session fixation prevention; server-side validation |
| **Build Tool** | Apache Maven | Clean `pom.xml` configuration with WAR packaging |
| **Web Server** | Apache Tomcat 8.5 / 9.x | Deployment descriptor `web.xml` configured with all routes and error handlers |

---

## 3. Database Schema & Architecture (`database.sql`)

The database script is located at:  
`d:\My Workspace\Kasnet_Online_Internship\database.sql`

Compatible with MySQL Workbench 6.3. All tables use `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`.

### Tables Summary:

1. **`users`**
   - Columns: `user_id` (PK, AI), `first_name`, `last_name`, `email` (UNIQUE), `password_hash`, `mobile`, `country_code`, `role` (ENUM: 'STUDENT','ADMIN'), `stream` (ENUM: 'Graduation','Post Graduation','Diploma'), `college_name`, `city`, `referral_code`, `is_active`, `created_at`, `updated_at`.
   - Indexes on `email` and `mobile`.

2. **`student_profiles`**
   - Columns: `profile_id` (PK, AI), `user_id` (FK -> users.user_id ON DELETE CASCADE, UNIQUE), `gender` (ENUM), `date_of_birth`, `address`, `state`, `profile_photo_path`, `resume_path`, `resume_filename`, `resume_uploaded_at`, `profile_completion` (INT, 0-100), `last_login`, `previous_login`, `created_at`, `updated_at`.

3. **`education_details`**
   - Columns: `edu_id` (PK, AI), `user_id` (FK -> users), `level` (ENUM: 'SSC','HSC','Graduation','Master','Diploma'), `school_college_name`, `board_university`, `stream`, `passing_year`, `percentage`, `created_at`, `updated_at`.
   - Unique key on `(user_id, level)`.

4. **`achievements`**
   - Columns: `achievement_id` (PK, AI), `user_id` (FK -> users), `achievement_type` (ENUM: 'Certification','Project','ExtraCurricular','Other'), `title`, `description`, `technology`, `extra_activities`, `created_at`, `updated_at`.

5. **`skills`**
   - Columns: `skill_id` (PK, AI), `user_id` (FK -> users), `skill_name`, `skill_type` (ENUM: 'Technical','Soft','Domain'), `proficiency` (ENUM: 'Beginner','Intermediate','Advanced','Expert'), `created_at`.
   - Unique key on `(user_id, skill_name, skill_type)`.

6. **`certifications`**
   - Columns: `cert_id` (PK, AI), `user_id` (FK -> users), `cert_name`, `provider`, `technology`, `cert_id_number`, `issue_date`, `expiry_date`, `credential_url`, `created_at`, `updated_at`.

7. **`projects`**
   - Columns: `project_id` (PK, AI), `user_id` (FK -> users), `project_name`, `description`, `technologies_used`, `role`, `project_type` (ENUM: 'Academic','Personal','Professional','Open Source'), `team_size`, `start_date`, `end_date`, `project_url`, `github_url`, `created_at`, `updated_at`.

8. **`hackathons`**
   - Columns: `hackathon_id` (PK, AI), `user_id` (FK -> users), `hackathon_name`, `organizer`, `event_date`, `position_result`, `description`, `team_name`, `certificate_path`, `created_at`, `updated_at`.

9. **`intercollege_competitions`**
   - Columns: `competition_id` (PK, AI), `user_id` (FK -> users), `competition_name`, `college_organizer`, `event_date`, `project_name`, `position_result`, `description`, `team_name`, `certificate_path`, `created_at`, `updated_at`.

10. **`assignments`**
    - Columns: `assignment_id` (PK, AI), `title`, `subject`, `description`, `assignment_number`, `due_date`, `total_marks`, `is_active`, `created_at`.

11. **`assignment_submissions`**
    - Columns: `submission_id` (PK, AI), `assignment_id` (FK -> assignments), `user_id` (FK -> users), `submission_date`, `file_path`, `status` (ENUM: 'Pending','Submitted','Late','Evaluated'), `marks_obtained`, `remarks`, `resubmitted`, `updated_at`.

12. **`lectures`**
    - Columns: `lecture_id` (PK, AI), `title`, `subject`, `description`, `instructor`, `duration_minutes`, `video_url`, `lecture_number`, `lecture_date`, `is_active`, `created_at`.

13. **`lecture_progress`**
    - Columns: `progress_id` (PK, AI), `lecture_id` (FK -> lectures), `user_id` (FK -> users), `is_completed`, `watch_progress_percent`, `last_watched`. Unique on `(lecture_id, user_id)`.

14. **`login_activity`**
    - Columns: `activity_id` (PK, AI), `user_id` (FK -> users), `login_time`, `ip_address`, `user_agent`.

---

## 4. Complete File Inventory

### Configuration & Docs
- `pom.xml`: Complete dependencies (Servlet API, JSP API, JSTL, MySQL Connector, jbcrypt, commons-fileupload, commons-io, gson, slf4j).
- `database.sql`: 499-line schema with constraints, safe drop/create, indexes, and sample seed data.
- `README.md`: Developer guide, build instructions, troubleshooting, integration walkthrough.
- `docs/PROJECT_PROGRESS.md`: This tracking document.
- `docs/screenshots/`: Reference images (image1.png to image8.png) extracted directly from the requirement docx.

### Backend - Controller Layer (`src/main/java/com/kasnet/controller/`)
- `LoginServlet.java`: Handles user authentication, CAPTCHA validation, and session initiation.
- `RegisterServlet.java`: Handles account creation with duplicate checks, password strength rules, and BCrypt hashing.
- `LogoutServlet.java`: Invalidates session and redirects to login.
- `DashboardServlet.java`: Aggregates real-time statistics (assignments, lectures, portfolio) and renders dashboard.
- `ProfileServlet.java`: Central profile controller loading all 7 sections dynamically for the student.
- `PersonalDetailsServlet.java`: Processes updates to `users` and `student_profiles` tables.
- `EducationServlet.java`: Processes batch updates for SSC, HSC, Graduation, Master, and Diploma.
- `AchievementServlet.java`: Handles add/delete for certifications, projects, and extra-curricular achievements.
- `SkillsServlet.java`: Parses and saves comma-separated technical, soft, and domain skills.
- `CertificationServlet.java`: Full CRUD operations for student certifications.
- `ProjectServlet.java`: Full CRUD operations for student projects.
- `HackathonServlet.java`: Full CRUD operations for hackathon participations.
- `CompetitionServlet.java`: Full CRUD operations for inter-college competitions.
- `AssignmentServlet.java`: Manages student assignment retrieval and multipart submission uploads.
- `LectureServlet.java`: Manages lecture listings, video modal view, and completion tracking.
- `ResumeUploadServlet.java`: Provided for backward-compatibility and integration completeness.

### Backend - Model Layer (`src/main/java/com/kasnet/model/`)
- `User.java`: User entity POJO with all registration and contact attributes.
- `StudentProfile.java`: Profile entity with completion score, timestamps, and login tracking.
- `Education.java`: Multi-level education record POJO.
- `Achievement.java`: Student achievement POJO.
- `Skill.java`: Skill POJO (Technical, Soft, Domain).
- `Certification.java`: Certification POJO.
- `Project.java`: Project POJO with repository URLs and team details.
- `Hackathon.java`: Hackathon activity POJO.
- `Competition.java`: Inter-college project competition POJO.
- `Assignment.java`: Assignment and submission joined POJO.
- `Lecture.java`: Lecture and watch progress joined POJO.
- `DashboardStats.java`: DTO aggregating student dashboard metrics.

### Backend - DAO Layer (`src/main/java/com/kasnet/dao/`)
- `UserDAO.java`: Registration, login lookup, email/mobile duplicate checks, last login updates.
- `ProfileDAO.java`: Profile retrieval, personal details update, dynamic completion score computation.
- `EducationDAO.java`: Multi-level education retrieval, upsert (`ON DUPLICATE KEY UPDATE`), delete.
- `AchievementDAO.java`: Achievement add, list, delete with user ownership checks.
- `SkillDAO.java`: Type-specific skill querying and batch replacement.
- `CertificationDAO.java`: Certification CRUD operations.
- `ProjectDAO.java`: Project CRUD operations.
- `HackathonDAO.java`: Hackathon CRUD operations.
- `CompetitionDAO.java`: Competition CRUD operations.
- `AssignmentDAO.java`: Active assignment querying with left-joined student submission status.
- `LectureDAO.java`: Lecture querying with left-joined progress and mark-completed upsert.
- `DashboardDAO.java`: Multi-table aggregate query builder for dashboard statistics.

### Backend - Service Layer (`src/main/java/com/kasnet/service/`)
- `AuthenticationService.java`: Business logic for BCrypt hashing, duplicate checks, login verification.
- `ProfileService.java`: Full profile aggregation and validation logic.
- `FileUploadService.java`: File validation and safe disk storage.

### Backend - Utility Layer (`src/main/java/com/kasnet/util/`)
- `DBConnection.java`: Centralized JDBC connection factory with safe resource closing helpers.
- `PasswordUtil.java`: BCrypt hashing and verification utility.
- `ValidationUtil.java`: Email, mobile, password strength, and input sanitization helpers.
- `FileUtil.java`: Safe UUID filename generation, extension checking, size verification.
- `SessionUtil.java`: Centralized session authentication and route protection helper.

### Frontend - JSP Views (`src/main/webapp/`)
- `login.jsp`: Split-panel layout matching `image1.png`, CAPTCHA box, password show/hide eye toggle, inline `<style>`.
- `register.jsp`: 2-column card layout matching `image2.png` and `image3.png`, real-time strength meter, stream dropdown, CAPTCHA, inline `<style>`.
- `index.jsp`: Entry redirect to `/dashboard` or `/login`.
- `views/dashboard.jsp`: Visual dashboard with welcome banner, completion bar, 8 metric cards, shortcuts, inline `<style>`.
- `views/profile.jsp`: Comprehensive profile view matching `image4.png` to `image8.png`:
  * Teal sidebar with silhouette avatar, Profile Completion box, Details box (created date, previous login, spend time).
  * Tab 1: Personal Details (First name, Last name, Gender, DOB, Mobile, Email [readonly], Address, City, State, SAVE, EDIT, NEXT).
  * Tab 2: Education Details (SSC, HSC, Graduation, Master, Diploma with School/College, Board/Univ, Stream, Year, Percentage, SAVE, EDIT, NEXT).
  * Tab 3: Achievements (Certification, Project, Extra-Curricular Activities, SAVE, EDIT, NEXT).
  * Tab 4: Skills (Technical Skills, Soft Skills, Domain, SAVE, EDIT, NEXT).
  * Tab 5: Certifications (Add form, table with external links, delete).
  * Tab 6: Projects (Add form, table with GitHub links, delete).
  * Tab 7: Additional Activities (A. Hackathons, B. Inter-College Competitions with forms and tables).
  * *Note: Photo upload and resume upload buttons were omitted from the UI per explicit instruction.*
- `views/assignments.jsp`: Grid of assignment cards with due dates, marks, statuses, and file submission upload, inline `<style>`.
- `views/lectures.jsp`: Video learning grid with lecture cards, duration tags, video player modal, and mark-done actions, inline `<style>`.
- `views/error-404.jsp`: Clean 404 error page with navigation buttons.
- `views/error-500.jsp`: Graceful 500 error page concealing stack traces.
- `views/unauthorized.jsp`: Session expiration and access denied page.
- `views/resume-upload.jsp`: Redirect fallback.

---

## 5. Key Design Decisions & Requirement Adherence

1. **Inline CSS Constraint:**
   Every single JSP page contains its own independent `<style>` section. No monolithic shared CSS file was used. Each page is completely self-styled and responsive across desktop, tablet, and mobile.

2. **Removal of Photo and Resume Upload Buttons:**
   As instructed by the user ("*Also I don't need the profile photo or the resume upload button, so remove them*"), the UI elements for uploading profile photos in the sidebar and uploading resumes at the bottom of the tabs were completely omitted from `profile.jsp`, while maintaining database columns and backend classes so that future integration into the main website won't face schema or class missing issues.

3. **User ID Authentication Source:**
   In all controller servlets (`PersonalDetailsServlet`, `EducationServlet`, `SkillsServlet`, `CertificationServlet`, `ProjectServlet`, `HackathonServlet`, `CompetitionServlet`, `AssignmentServlet`, `LectureServlet`), the `userId` is obtained **strictly from `SessionUtil.getUserId(req)`** and **never from hidden form fields or query parameters**, preventing IDOR (Insecure Direct Object Reference) vulnerabilities.

4. **Dynamic Profile Completion:**
   `ProfileDAO.calculateProfileCompletion(userId)` calculates profile completion mathematically (Personal: 20%, Education: 20%, Achievements: 15%, Skills: 15%, Certifications: 15%, Projects: 15%) and updates `student_profiles.profile_completion`. The resulting value dynamically feeds the sidebar and dashboard.

---

## 6. How to Run & Verify

1. **MySQL Setup:**
   ```bash
   mysql -u root -p < database.sql
   ```
2. **Configure Credentials:**
   Open `src/main/java/com/kasnet/util/DBConnection.java` and update `DB_PASSWORD` if your local MySQL root password is not `root`.
3. **Build the WAR with Maven:**
   ```bash
   mvn clean package
   ```
4. **Deploy to Apache Tomcat:**
   Copy `target/kasnet-internship-portal.war` to Tomcat's `webapps/` folder, or run:
   ```bash
   mvn tomcat7:run
   ```
5. **Access in Browser:**
   - Login: `http://localhost:8080/kasnet-internship-portal/login`
   - Register: `http://localhost:8080/kasnet-internship-portal/register`
   - Dashboard: `http://localhost:8080/kasnet-internship-portal/dashboard`
   - Profile: `http://localhost:8080/kasnet-internship-portal/profile`

---

## 7. Instructions for Any Future AI Model Resuming Work

If you are an AI model tasked with continuing, extending, or integrating this project:
- **Do not alter the MVC separation:** keep SQL queries inside the DAOs (`com.kasnet.dao.*`) and presentation strictly in JSP.
- **Do not introduce a single global CSS file:** maintain the inline `<style>` requirement inside each JSP.
- **When adding new sections:**
  1. Add corresponding table or columns to `database.sql`.
  2. Create/update Model class in `com.kasnet.model`.
  3. Create/update DAO class in `com.kasnet.dao` using `PreparedStatement`.
  4. Create/update Controller Servlet in `com.kasnet.controller` and verify `SessionUtil.requireLogin(req, resp)`.
  5. Add tab or view to `profile.jsp` or new view in `src/main/webapp/views/`.
  6. Register servlet in `web.xml`.
- **Integration with Hosted Website:**
  Copy the `com.kasnet` package into the target web application's Java source directory, copy `views/*.jsp` and `login.jsp`/`register.jsp` to the target web directory, and merge the `<servlet>` and `<servlet-mapping>` blocks from `web.xml`.
