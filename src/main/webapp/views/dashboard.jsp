<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    if (request.getAttribute("activeSection") == null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
%>
<c:set var="selectedSection" value="${requestScope.activeSection}" />
<c:if test="${empty selectedSection}">
    <c:set var="selectedSection" value="student-profile" />
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - My Internship Technologies Portal</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        :root { --font: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; }
        body { font-family: var(--font); background-color: #f8fafc; color: #1e293b; line-height: 1.5; }
        .navbar { position: fixed; z-index: 1000; top: 0; left: 0; right: 0; background: linear-gradient(135deg, #1a237e 0%, #283593 100%); color: #ffffff; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 8px rgba(0,0,0,0.12); }
        .navbar-branding { display: flex; align-items: center; flex: 0 0 auto; }
        .menu-toggle { width: 34px; height: 34px; display: grid; align-content: center; justify-items: center; gap: 5px; flex: 0 0 auto; margin-right: 10px; border: 0; border-radius: 6px; color: #fff; background: rgba(255,255,255,0.2); cursor: pointer; }
        .menu-toggle:focus-visible { outline: 3px solid #bfdbfe; outline-offset: 2px; }
        .menu-toggle span { width: 19px; height: 2px; border-radius: 2px; background: currentColor; }
        .navbar-brand { display: flex; align-items: center; gap: 12px; font-size: 1.15rem; font-weight: 700; text-decoration: none; color: #ffffff; }
        .navbar-menu { display: flex; align-items: center; gap: 14px; }
        .nav-link { color: rgba(255,255,255,0.85); text-decoration: none; font-size: 0.88rem; padding: 6px 12px; border-radius: 6px; transition: all 0.2s; }
        .nav-link:hover, .nav-link.active { color: #ffffff; background: rgba(255,255,255,0.15); }
        .btn-logout { background: #dc2626; color: #ffffff; padding: 6px 14px; border-radius: 6px; text-decoration: none; font-size: 0.85rem; font-weight: 600; }
        .btn-logout:hover { background: #b91c1c; }
        .layout { width: 100%; min-height: calc(100vh - 58px); display: grid; grid-template-columns: 270px minmax(0, 1fr); align-items: stretch; gap: 0; margin: 58px 0 0; padding: 0; transition: grid-template-columns .25s ease; }
        .sidebar { position: fixed; z-index: 900; top: 58px; bottom: 0; left: 0; width: 270px; overflow-y: auto; padding: 24px 18px; border: 0; border-right: 1px solid #dce4f1; border-radius: 0; background: #f9fbff; box-shadow: 4px 0 18px rgba(30, 41, 86, .045); transition: transform .25s ease; }
        .sidebar-title { margin: 0 8px 18px; padding: 0 4px 14px; border-bottom: 1px solid #e1e8f3; color: #64748b; font-size: 12px; font-weight: 700; letter-spacing: .1em; text-transform: uppercase; }
        .menu-group { margin: 8px 0; overflow: hidden; border: 1px solid #dfe6f1; border-radius: 10px; background: #fff; box-shadow: 0 2px 5px rgba(30, 41, 86, .035); transition: border-color .18s, box-shadow .18s; }
        .menu-group[open] { border-color: #b8c9f2; box-shadow: 0 4px 12px rgba(37, 99, 235, .08); }
        .menu-group summary { position: relative; padding: 13px 38px 13px 14px; color: #25345f; cursor: pointer; font-weight: 700; list-style: none; transition: color .18s, background .18s; }
        .menu-group summary::-webkit-details-marker { display: none; }
        .menu-group summary::after { position: absolute; top: 50%; right: 16px; width: 8px; height: 8px; border-right: 2px solid #71809e; border-bottom: 2px solid #71809e; content: ""; transform: translateY(-70%) rotate(45deg); transition: transform .18s, border-color .18s; }
        .menu-group[open] summary::after { border-color: #2563eb; transform: translateY(-30%) rotate(225deg); }
        .menu-group summary:hover, .menu-group[open] summary { color: #1d4ed8; background: #f1f5ff; }
        .submenu { display: grid; gap: 4px; padding: 5px 9px 10px 13px; border-top: 1px solid #edf1f7; }
        .submenu a { padding: 10px 11px; border-left: 2px solid #dbeafe; border-radius: 0 7px 7px 0; color: #475569; font-size: 14px; line-height: 1.4; text-decoration: none; transition: color .18s, background .18s, border-color .18s; }
        .submenu a:hover, .submenu a:focus { border-left-color: #2563eb; color: #1d4ed8; background: #f5f8ff; }
        .submenu a.active { border-left: 4px solid #1d4ed8; color: #1e40af; background: #eaf1ff; font-weight: 700; }
        .content { grid-column: 2; display: grid; align-content: start; gap: 20px; min-width: 0; padding: 28px; transition: padding .25s ease; }
        .panel { min-height: 230px; padding: 30px; animation: panel-in .24s ease both; }
        .panel { border: 1px solid #e1e8f3; border-radius: 16px; background: #fff; box-shadow: 0 8px 24px rgba(30, 41, 86, .07); }
        @keyframes panel-in { from { opacity: 0; transform: translateY(6px); } to { opacity: 1; transform: translateY(0); } }
        .sidebar-hidden .layout { grid-template-columns: minmax(0, 1fr); }
        .sidebar-hidden .sidebar { transform: translateX(-100%); pointer-events: none; }
        .sidebar-hidden .content { grid-column: 1; width: 100%; max-width: 1180px; justify-self: center; }
        h1 { margin: 0 0 8px; color: #172554; font-size: 28px; }
        h2 { margin: 0 0 18px; font-size: 21px; }
        h3 { margin: 26px 0 12px; padding-bottom: 9px; border-bottom: 1px solid #e8edf5; color: #253b7a; font-size: 17px; }
        .intro, .muted { color: #64748b; line-height: 1.6; }
        .completion { margin-top: 20px; }
        .completion-line { display: flex; justify-content: space-between; gap: 12px; margin-bottom: 8px; font-size: 14px; font-weight: 700; }
        .progress { height: 10px; overflow: hidden; border-radius: 20px; background: #e5e7eb; }
        .progress-fill { height: 100%; border-radius: inherit; background: #2563eb; }
        .summary-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(min(100%, 290px), 420px)); gap: 10px; }
        .summary-item { min-width: 0; padding: 12px; border: 1px solid #e1e8f3; border-radius: 10px; background: linear-gradient(145deg, #fff, #f8faff); }
        .summary-label { display: block; margin-bottom: 5px; color: #64748b; font-size: 12px; font-weight: 700; text-transform: uppercase; }
        .summary-value { overflow-wrap: anywhere; color: #172554; line-height: 1.5; }
        .record-list { display: grid; gap: 10px; }
        .record { padding: 13px; border: 1px solid #e1e8f3; border-radius: 10px; background: #fcfdff; }
        .record strong { display: block; margin-bottom: 5px; }
        .record p { margin: 4px 0; color: #64748b; font-size: 14px; line-height: 1.5; overflow-wrap: anywhere; }
        .button { display: inline-flex; align-items: center; justify-content: center; min-height: 42px; padding: 10px 16px; border: 0; border-radius: 7px; color: #fff; background: #1d4ed8; font-weight: 700; text-decoration: none; cursor: pointer; }
        .button:hover { background: #1e40af; }
        .button-secondary { margin-left: 8px; border: 1px solid #cbd5e1; color: #1e3a8a; background: #fff; }
        .empty { padding: 13px; border-radius: 8px; color: #64748b; background: #f8fafc; }
        .report-links { display: flex; flex-wrap: wrap; gap: 10px; }
        @media (max-width: 760px) {
            .layout { grid-template-columns: 1fr; }
            .sidebar { top: 58px; width: min(300px, 86vw); }
            .content, .sidebar-hidden .content { grid-column: 1; padding: 18px 14px; }
        }
        @media (max-width: 480px) {
            .summary-grid { grid-template-columns: 1fr; }
            .panel { min-height: 190px; padding: 20px; }
            .layout { padding: 12px; }
        }
    </style>
</head>
<body>
    <header class="navbar">
        <div class="navbar-branding">
            <button class="menu-toggle" id="sidebarToggle" type="button" aria-label="Hide dashboard sidebar" aria-controls="dashboardSidebar" aria-expanded="true">
                <span></span><span></span><span></span>
            </button>
            <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>My Internship Technologies</span>
            </a>
        </div>
        <nav class="navbar-menu" aria-label="Main navigation">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active" aria-current="page">Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link">Resume Analysis</a>
            <span style="font-size: 0.88rem; color: #cbd5e0; margin-left: 6px;">Hi, <c:out value="${user.firstName}"/></span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </nav>
    </header>

    <div class="layout">
        <aside class="sidebar" id="dashboardSidebar" aria-label="Dashboard sections">
            <h2 class="sidebar-title">Dashboard</h2>
            <details class="menu-group" data-menu-key="student-profile">
                <summary>Student Profile</summary>
                <div class="submenu"><a href="${pageContext.request.contextPath}/dashboard?section=student-profile" class="${selectedSection eq 'student-profile' ? 'active' : ''}" aria-current="${selectedSection eq 'student-profile' ? 'page' : 'false'}">Student Profile</a></div>
            </details>
            <details class="menu-group" data-menu-key="assignment">
                <summary>Assignment</summary>
                <div class="submenu"><a href="${pageContext.request.contextPath}/dashboard?section=assignment" class="${selectedSection eq 'assignment' ? 'active' : ''}" aria-current="${selectedSection eq 'assignment' ? 'page' : 'false'}">Assignment Details</a></div>
            </details>
            <details class="menu-group" data-menu-key="test">
                <summary>Test</summary>
                <div class="submenu">
                    <a href="${pageContext.request.contextPath}/dashboard?section=test-report" class="${selectedSection eq 'test-report' ? 'active' : ''}" aria-current="${selectedSection eq 'test-report' ? 'page' : 'false'}">Test Report</a>
                    <a href="${pageContext.request.contextPath}/dashboard?section=test-summary-report" class="${selectedSection eq 'test-summary-report' ? 'active' : ''}" aria-current="${selectedSection eq 'test-summary-report' ? 'page' : 'false'}">Test Summary Report</a>
                </div>
            </details>
            <details class="menu-group" data-menu-key="projects">
                <summary>Projects</summary>
                <div class="submenu"><a href="${pageContext.request.contextPath}/dashboard?section=projects" class="${selectedSection eq 'projects' ? 'active' : ''}" aria-current="${selectedSection eq 'projects' ? 'page' : 'false'}">Project Report</a></div>
            </details>
            <details class="menu-group" data-menu-key="certificate">
                <summary>Certificate</summary>
                <div class="submenu"><a href="${pageContext.request.contextPath}/dashboard?section=certificates" class="${selectedSection eq 'certificates' ? 'active' : ''}" aria-current="${selectedSection eq 'certificates' ? 'page' : 'false'}">Certificate</a></div>
            </details>
            <details class="menu-group" data-menu-key="report">
                <summary>Report</summary>
                <div class="submenu">
                    <a href="${pageContext.request.contextPath}/dashboard?section=report-details" class="${selectedSection eq 'report-details' ? 'active' : ''}" aria-current="${selectedSection eq 'report-details' ? 'page' : 'false'}">Report Details</a>
                    <a href="${pageContext.request.contextPath}/dashboard?section=internship-conclusion-report" class="${selectedSection eq 'internship-conclusion-report' ? 'active' : ''}" aria-current="${selectedSection eq 'internship-conclusion-report' ? 'page' : 'false'}">Internship Conclusion Report</a>
                </div>
            </details>
        </aside>

        <main class="content">
            <c:if test="${selectedSection eq 'student-profile'}">
            <section class="panel">
                <h1>Student Profile Summary</h1>
                <p class="intro">A summary of the information saved in your student profile.</p>
                <div class="completion">
                    <div class="completion-line"><span>Profile completion</span><span><c:out value="${profileCompletion}"/>%</span></div>
                    <div class="progress" role="progressbar" aria-label="Profile completion" aria-valuemin="0" aria-valuemax="100" aria-valuenow="${profileCompletion}">
                        <div class="progress-fill" data-completion="${profileCompletion}"></div>
                    </div>
                </div>
                <h3>Personal Information</h3>
                <div class="summary-grid">
                    <div class="summary-item"><span class="summary-label">Name</span><div class="summary-value"><c:out value="${user.fullName}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Email</span><div class="summary-value"><c:out value="${user.email}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Mobile</span><div class="summary-value"><c:out value="${user.mobile}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">College</span><div class="summary-value"><c:out value="${user.collegeName}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Stream</span><div class="summary-value"><c:out value="${user.stream}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">City</span><div class="summary-value"><c:out value="${user.city}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Gender</span><div class="summary-value"><c:out value="${profile.gender}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Date of Birth</span><div class="summary-value"><c:out value="${profile.dateOfBirth}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">Address</span><div class="summary-value"><c:out value="${profile.address}" default="Not provided"/></div></div>
                    <div class="summary-item"><span class="summary-label">State</span><div class="summary-value"><c:out value="${profile.state}" default="Not provided"/></div></div>
                </div>
                <h3>Education</h3>
                <c:choose>
                    <c:when test="${not empty educationList}">
                        <div class="record-list">
                            <c:forEach var="education" items="${educationList}">
                                <article class="record">
                                    <strong><c:out value="${education.level}"/></strong>
                                    <p><c:out value="${education.schoolCollegeName}"/> <c:if test="${not empty education.boardUniversity}">— <c:out value="${education.boardUniversity}"/></c:if></p>
                                    <p><c:if test="${not empty education.stream}"><c:out value="${education.stream}"/> · </c:if><c:if test="${education.passingYear > 0}">Year <c:out value="${education.passingYear}"/> · </c:if><c:out value="${education.percentage}"/>%</p>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No education records have been saved yet.</p></c:otherwise>
                </c:choose>
                <h3>Skills</h3>
                <c:choose>
                    <c:when test="${not empty skillList}">
                        <div class="record-list">
                            <c:forEach var="skill" items="${skillList}">
                                <article class="record"><strong><c:out value="${skill.skillName}"/></strong><p><c:out value="${skill.skillType}"/> <c:if test="${not empty skill.proficiency}">· <c:out value="${skill.proficiency}"/></c:if></p></article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No skills have been saved yet.</p></c:otherwise>
                </c:choose>
                <h3>Additional Activities and Achievements</h3>
                <c:choose>
                    <c:when test="${not empty achievementList or not empty hackathonList or not empty competitionList}">
                        <div class="record-list">
                            <c:forEach var="achievement" items="${achievementList}">
                                <article class="record">
                                    <strong><c:out value="${achievement.title}"/></strong>
                                    <p><c:out value="${achievement.achievementType}"/> <c:if test="${not empty achievement.technology}">· <c:out value="${achievement.technology}"/></c:if></p>
                                    <p><c:out value="${achievement.description}"/> <c:if test="${not empty achievement.extraActivities}"><c:out value="${achievement.extraActivities}"/></c:if></p>
                                </article>
                            </c:forEach>
                            <c:forEach var="hackathon" items="${hackathonList}">
                                <article class="record">
                                    <strong><c:out value="${hackathon.hackathonName}"/></strong>
                                    <p><c:out value="${hackathon.organizer}"/> <c:if test="${not empty hackathon.eventDate}">· <c:out value="${hackathon.eventDate}"/></c:if></p>
                                    <p><c:out value="${hackathon.positionResult}"/> <c:if test="${not empty hackathon.description}">· <c:out value="${hackathon.description}"/></c:if></p>
                                </article>
                            </c:forEach>
                            <c:forEach var="competition" items="${competitionList}">
                                <article class="record">
                                    <strong><c:out value="${competition.competitionName}"/></strong>
                                    <p><c:out value="${competition.collegeOrganizer}"/> <c:if test="${not empty competition.eventDate}">· <c:out value="${competition.eventDate}"/></c:if></p>
                                    <p><c:if test="${not empty competition.projectName}">Project: <c:out value="${competition.projectName}"/> · </c:if><c:out value="${competition.positionResult}"/></p>
                                    <p><c:out value="${competition.description}"/></p>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No additional activities or achievements have been saved yet.</p></c:otherwise>
                </c:choose>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'assignment'}">
            <section class="panel">
                <h2>Assignment Details</h2>
                <p class="intro">Summary of your active internship assignments and submission status.</p>
                <c:choose>
                    <c:when test="${not empty assignmentList}">
                        <div class="record-list">
                            <c:forEach var="assignment" items="${assignmentList}">
                                <article class="record">
                                    <strong><c:out value="${assignment.title}"/></strong>
                                    <p><c:if test="${not empty assignment.subject}"><c:out value="${assignment.subject}"/> · </c:if>Status: <c:out value="${assignment.status}" default="Pending"/></p>
                                    <p><c:if test="${not empty assignment.dueDate}">Due: <c:out value="${assignment.dueDate}"/> · </c:if><c:if test="${assignment.totalMarks > 0}">Total marks: <c:out value="${assignment.totalMarks}"/></c:if></p>
                                    <c:if test="${not empty assignment.remarks}"><p>Feedback: <c:out value="${assignment.remarks}"/></p></c:if>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No active assignments are available.</p></c:otherwise>
                </c:choose>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'test-report'}">
            <section class="panel">
                <h2>Test Report</h2>
                <p class="empty">No test report data is available yet.</p>
            </section>
            </c:if>
            <c:if test="${selectedSection eq 'test-summary-report'}">
            <section class="panel">
                <h2>Test Summary Report</h2>
                <p class="empty">No test summary data is available yet.</p>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'projects'}">
            <section class="panel">
                <h2>Project Report</h2>
                <c:choose>
                    <c:when test="${not empty projectList}">
                        <div class="record-list">
                            <c:forEach var="project" items="${projectList}">
                                <article class="record">
                                    <strong><c:out value="${project.projectName}"/></strong>
                                    <p><c:out value="${project.description}"/></p>
                                    <p><c:if test="${not empty project.role}">Role: <c:out value="${project.role}"/> · </c:if><c:if test="${not empty project.technologiesUsed}">Technologies: </c:if><c:out value="${project.technologiesUsed}"/></p>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No projects have been saved yet.</p></c:otherwise>
                </c:choose>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'certificates'}">
            <section class="panel">
                <h2>Certificate</h2>
                <c:choose>
                    <c:when test="${not empty certificationList}">
                        <div class="record-list">
                            <c:forEach var="certificate" items="${certificationList}">
                                <article class="record">
                                    <strong><c:out value="${certificate.certName}"/></strong>
                                    <p><c:out value="${certificate.provider}"/> <c:if test="${not empty certificate.technology}">· <c:out value="${certificate.technology}"/></c:if></p>
                                    <c:if test="${not empty certificate.credentialUrl}"><p><a href="<c:out value='${certificate.credentialUrl}'/>" target="_blank" rel="noopener noreferrer">View credential</a></p></c:if>
                                </article>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise><p class="empty">No certificates have been saved yet.</p></c:otherwise>
                </c:choose>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'report-details'}">
            <section class="panel">
                <h2>Report Details</h2>
                <p class="intro">Download a report containing your saved profile, education, skills, projects, and certificates.</p>
                <div class="report-links">
                    <a class="button" href="${pageContext.request.contextPath}/dashboard?download=report&amp;format=pdf">Download PDF</a>
                    <a class="button" href="${pageContext.request.contextPath}/dashboard?download=report&amp;format=word">Download Word</a>
                    <a class="button" href="${pageContext.request.contextPath}/dashboard?download=report&amp;format=csv">Download CSV</a>
                </div>
            </section>
            </c:if>

            <c:if test="${selectedSection eq 'internship-conclusion-report'}">
            <section class="panel">
                <h2>Internship Conclusion Report</h2>
                <p class="empty">An internship conclusion report is not available yet.</p>
            </section>
            </c:if>
        </main>
    </div>
<script>
    (function () {
        var toggle = document.getElementById("sidebarToggle");
        var menuGroups = document.querySelectorAll(".menu-group[data-menu-key]");
        var menuStateKey = "mit-dashboard-open-menus";
        var openMenus = {};

        function readStoredValue(key) {
            try {
                return window.localStorage.getItem(key);
            } catch (error) {
                return null;
            }
        }

        function storeValue(key, value) {
            try {
                window.localStorage.setItem(key, value);
            } catch (error) {
                // Navigation remains usable if browser storage is unavailable.
            }
        }

        var navigationEntries = window.performance && window.performance.getEntriesByType
            ? window.performance.getEntriesByType("navigation")
            : [];
        var navigationType = navigationEntries.length > 0
            ? navigationEntries[0].type
            : window.performance && window.performance.navigation
                ? window.performance.navigation.type === 1 ? "reload" : "navigate"
                : "navigate";
        var cameFromDashboardSection = false;
        try {
            var previousPage = new URL(document.referrer);
            cameFromDashboardSection = navigationType === "navigate"
                && previousPage.origin === window.location.origin
                && previousPage.pathname === window.location.pathname;
        } catch (error) {
            cameFromDashboardSection = false;
        }

        if (!cameFromDashboardSection) {
            storeValue(menuStateKey, "{}");
        } else {
            try {
                openMenus = JSON.parse(readStoredValue(menuStateKey) || "{}");
            } catch (error) {
                openMenus = {};
            }
        }

        menuGroups.forEach(function (group) {
            var key = group.dataset.menuKey;
            group.open = openMenus[key] === true;
            group.addEventListener("toggle", function () {
                openMenus[key] = group.open;
                storeValue(menuStateKey, JSON.stringify(openMenus));
            });
        });

        function updateToggleState() {
            var expanded = !document.body.classList.contains("sidebar-hidden");
            toggle.setAttribute("aria-expanded", String(expanded));
            toggle.setAttribute("aria-label", expanded ? "Hide dashboard sidebar" : "Show dashboard sidebar");
        }

        updateToggleState();
        toggle.addEventListener("click", function () {
            document.body.classList.toggle("sidebar-hidden");
            updateToggleState();
        });
    }());

    document.querySelectorAll(".progress-fill[data-completion]").forEach(function (bar) {
        var completion = Number(bar.dataset.completion);
        if (Number.isFinite(completion)) {
            bar.style.width = Math.max(0, Math.min(100, completion)) + "%";
        }
    });
</script>
</body>
</html>
