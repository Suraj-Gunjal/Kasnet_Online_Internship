<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - Kasnet Internship Portal</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background-color: #f0f2f5;
            color: #1a1a2e;
        }

        /* Top Navbar */
        .navbar {
            background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
            color: #fff;
            padding: 12px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
        }

        .navbar-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 1.25rem;
            font-weight: 700;
            text-decoration: none;
            color: #fff;
        }

        .navbar-brand .logo-icon {
            width: 36px;
            height: 36px;
            background: rgba(255,255,255,0.2);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
        }

        .navbar-menu {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .nav-link {
            color: rgba(255,255,255,0.85);
            text-decoration: none;
            font-size: 0.9rem;
            padding: 6px 12px;
            border-radius: 6px;
            transition: all 0.2s;
        }

        .nav-link:hover, .nav-link.active {
            color: #fff;
            background: rgba(255,255,255,0.15);
        }

        .btn-logout {
            background: #e53935;
            color: #fff;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 600;
            transition: background 0.2s;
        }

        .btn-logout:hover {
            background: #c62828;
        }

        /* Layout Container */
        .container {
            max-width: 1400px;
            margin: 24px auto;
            padding: 0 20px;
        }

        /* Welcome Banner */
        .welcome-banner {
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            border-radius: 16px;
            padding: 32px 36px;
            color: #ffffff;
            margin-bottom: 28px;
            box-shadow: 0 8px 24px rgba(30, 60, 114, 0.2);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
        }

        .welcome-text h1 {
            font-size: 1.9rem;
            font-weight: 800;
            margin-bottom: 8px;
        }

        .welcome-text p {
            font-size: 0.95rem;
            color: rgba(255,255,255,0.85);
        }

        .completion-badge {
            background: rgba(255,255,255,0.15);
            backdrop-filter: blur(8px);
            border: 1px solid rgba(255,255,255,0.25);
            border-radius: 12px;
            padding: 16px 24px;
            text-align: center;
            min-width: 200px;
        }

        .completion-badge .percent {
            font-size: 2rem;
            font-weight: 800;
            color: #4ade80;
        }

        .completion-badge .label {
            font-size: 0.8rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: rgba(255,255,255,0.9);
            margin-top: 4px;
        }

        /* Section Headings */
        .section-header {
            font-size: 1.25rem;
            font-weight: 700;
            color: #1a237e;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 20px;
            margin-bottom: 32px;
        }

        .stat-card {
            background: #ffffff;
            border-radius: 14px;
            padding: 22px 24px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.05);
            border: 1px solid #e5e7eb;
            display: flex;
            align-items: center;
            gap: 18px;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .stat-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
        }

        .stat-icon {
            width: 54px;
            height: 54px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            flex-shrink: 0;
        }

        .icon-blue { background: #e0e7ff; color: #4338ca; }
        .icon-green { background: #dcfce7; color: #15803d; }
        .icon-amber { background: #fef3c7; color: #b45309; }
        .icon-purple { background: #f3e8ff; color: #7e22ce; }

        .stat-content {
            display: flex;
            flex-direction: column;
        }

        .stat-value {
            font-size: 1.8rem;
            font-weight: 800;
            color: #111827;
            line-height: 1.1;
        }

        .stat-title {
            font-size: 0.85rem;
            color: #6b7280;
            margin-top: 4px;
            font-weight: 500;
        }

        /* Quick Actions Grid */
        .actions-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
            margin-bottom: 32px;
        }

        .action-card {
            background: #ffffff;
            border-radius: 12px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.04);
            border: 1px solid #e5e7eb;
            text-decoration: none;
            color: #1f2937;
            display: flex;
            flex-direction: column;
            gap: 10px;
            transition: all 0.2s;
        }

        .action-card:hover {
            border-color: #3b82f6;
            background: #f8fafc;
            transform: translateY(-2px);
        }

        .action-card .action-icon {
            font-size: 22px;
        }

        .action-card .action-title {
            font-size: 1rem;
            font-weight: 700;
            color: #1e3a8a;
        }

        .action-card .action-desc {
            font-size: 0.8rem;
            color: #6b7280;
            line-height: 1.4;
        }

        /* Info Card */
        .info-card {
            background: #ffffff;
            border-radius: 14px;
            padding: 24px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.05);
            border: 1px solid #e5e7eb;
        }

        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 16px;
        }

        .info-item {
            padding: 12px 16px;
            background: #f9fafb;
            border-radius: 8px;
            border: 1px solid #f3f4f6;
        }

        .info-label {
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
            color: #9ca3af;
            letter-spacing: 0.5px;
        }

        .info-value {
            font-size: 0.95rem;
            font-weight: 600;
            color: #1f2937;
            margin-top: 4px;
        }
    </style>
</head>
<body>

    <!-- Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">
            <div class="logo-icon">K</div>
            <span>Kasnet Internship Portal</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link active">Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <span style="font-size: 0.9rem; color: #cbd5e0; margin-left: 8px;">Hi, ${user.firstName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container">

        <!-- Welcome Banner -->
        <div class="welcome-banner">
            <div class="welcome-text">
                <h1>Welcome back, ${user.firstName} ${user.lastName}! 👋</h1>
                <p>Track your internship tasks, learning progress, certifications and project portfolio all in one place.</p>
            </div>
            <div class="completion-badge">
                <div class="percent">${stats.profileCompletion}%</div>
                <div class="label">Profile Completion</div>
            </div>
        </div>

        <!-- Internship Progress Stats -->
        <div class="section-header">📊 Learning &amp; Internship Overview</div>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon icon-blue">📝</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.totalAssignments}</span>
                    <span class="stat-title">Total Assignments</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-amber">⏳</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.pendingAssignments}</span>
                    <span class="stat-title">Pending Assignments</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-green">📺</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.completedLectures} / ${stats.totalLectures}</span>
                    <span class="stat-title">Lectures Completed</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-purple">🎓</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.certificationsCount}</span>
                    <span class="stat-title">Certifications Earned</span>
                </div>
            </div>
        </div>

        <!-- Student Portfolio Stats -->
        <div class="section-header">🚀 Portfolio &amp; Extracurriculars</div>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon icon-blue">💻</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.projectsCount}</span>
                    <span class="stat-title">Projects Built</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-purple">🏆</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.hackathonsCount}</span>
                    <span class="stat-title">Hackathons Participated</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-green">🥇</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.competitionsCount}</span>
                    <span class="stat-title">Competitions Entered</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon icon-amber">✅</div>
                <div class="stat-content">
                    <span class="stat-value">${stats.submittedAssignments}</span>
                    <span class="stat-title">Assignments Submitted</span>
                </div>
            </div>
        </div>

        <!-- Quick Actions Navigation -->
        <div class="section-header">⚡ Quick Shortcuts</div>
        <div class="actions-grid">
            <a href="${pageContext.request.contextPath}/profile?tab=personal" class="action-card">
                <div class="action-icon">👤</div>
                <div class="action-title">Update Profile</div>
                <div class="action-desc">Keep your personal and academic details up to date.</div>
            </a>
            <a href="${pageContext.request.contextPath}/assignments" class="action-card">
                <div class="action-icon">📋</div>
                <div class="action-title">Assignments</div>
                <div class="action-desc">Check due dates and upload internship task submissions.</div>
            </a>
            <a href="${pageContext.request.contextPath}/lectures" class="action-card">
                <div class="action-icon">🎥</div>
                <div class="action-title">Lecture Videos</div>
                <div class="action-desc">Watch domain lectures and track video completion.</div>
            </a>
            <a href="${pageContext.request.contextPath}/profile?tab=projects" class="action-card">
                <div class="action-icon">🛠️</div>
                <div class="action-title">Add Projects</div>
                <div class="action-desc">Showcase academic, personal, or internship projects.</div>
            </a>
            <a href="${pageContext.request.contextPath}/profile?tab=skills" class="action-card">
                <div class="action-icon">💡</div>
                <div class="action-title">Manage Skills</div>
                <div class="action-desc">Add technical skills, soft skills, and domain proficiencies.</div>
            </a>
            <a href="${pageContext.request.contextPath}/profile?tab=activities" class="action-card">
                <div class="action-icon">🏅</div>
                <div class="action-title">Activities &amp; Hackathons</div>
                <div class="action-desc">Document hackathon ranks and project competition wins.</div>
            </a>
        </div>

        <!-- Student Account Info Summary -->
        <div class="section-header">📌 Student Account Details</div>
        <div class="info-card">
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Student Name</div>
                    <div class="info-value">${user.firstName} ${user.lastName}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Email Address</div>
                    <div class="info-value">${user.email}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Mobile Number</div>
                    <div class="info-value">${user.mobile}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Stream</div>
                    <div class="info-value">${user.stream != null ? user.stream : 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">College</div>
                    <div class="info-value">${user.collegeName != null ? user.collegeName : 'N/A'}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">City</div>
                    <div class="info-value">${user.city != null ? user.city : 'N/A'}</div>
                </div>
            </div>
        </div>

    </div>

</body>
</html>
