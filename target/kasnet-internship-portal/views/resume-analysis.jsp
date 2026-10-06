<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Resume Analysis - Kasnet Internship Portal</title>
    <style>
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        :root {
            --primary: #1a237e;
            --accent: #2b6cb0;
            --accent-light: #ebf8ff;
            --text-dark: #1e293b;
            --text-muted: #64748b;
            --bg: #f8fafc;
            --card-bg: #ffffff;
            --border: #e2e8f0;
            --radius: 8px;
            --font: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }

        body {
            font-family: var(--font);
            background-color: var(--bg);
            color: var(--text-dark);
            line-height: 1.5;
        }

        /* Top Navbar */
        .navbar {
            background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
            color: #ffffff;
            padding: 12px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.12);
        }

        .navbar-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 1.15rem;
            font-weight: 700;
            text-decoration: none;
            color: #ffffff;
        }

        .logo-icon {
            width: 34px;
            height: 34px;
            background: rgba(255,255,255,0.2);
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1rem;
            font-weight: 700;
        }

        .navbar-menu {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .nav-link {
            color: rgba(255,255,255,0.85);
            text-decoration: none;
            font-size: 0.88rem;
            padding: 6px 12px;
            border-radius: 6px;
            transition: all 0.2s;
        }

        .nav-link:hover, .nav-link.active {
            color: #ffffff;
            background: rgba(255,255,255,0.15);
        }

        .btn-logout {
            background: #dc2626;
            color: #ffffff;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 600;
        }

        .container {
            max-width: 1100px;
            margin: 28px auto;
            padding: 0 20px;
        }

        .breadcrumb {
            font-size: 0.85rem;
            color: var(--text-muted);
            margin-bottom: 16px;
        }

        .breadcrumb a {
            color: var(--accent);
            text-decoration: none;
        }

        .page-header {
            margin-bottom: 24px;
        }

        .page-header h2 {
            font-size: 1.6rem;
            font-weight: 700;
            color: #0f172a;
        }

        .page-header p {
            font-size: 0.92rem;
            color: var(--text-muted);
        }

        .card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 32px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            margin-bottom: 24px;
        }

        /* Upload Placeholder Box */
        .upload-dropzone {
            border: 2px dashed var(--border);
            border-radius: var(--radius);
            padding: 48px 24px;
            text-align: center;
            background: #f8fafc;
            margin-bottom: 24px;
        }

        .dropzone-icon {
            font-size: 2.4rem;
            color: var(--accent);
            margin-bottom: 12px;
        }

        .dropzone-title {
            font-size: 1.1rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 6px;
        }

        .dropzone-sub {
            font-size: 0.88rem;
            color: var(--text-muted);
            margin-bottom: 18px;
        }

        .btn-disabled {
            background: #cbd5e1;
            color: #64748b;
            border: none;
            padding: 10px 22px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 600;
            cursor: not-allowed;
        }

        /* Notice banner */
        .notice-banner {
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            border-radius: 6px;
            padding: 14px 18px;
            color: #1e40af;
            font-size: 0.9rem;
            margin-bottom: 28px;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        /* Feature preview cards */
        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
            gap: 20px;
        }

        .feature-box {
            background: #ffffff;
            border: 1px solid var(--border);
            border-radius: 6px;
            padding: 20px;
        }

        .feature-box h4 {
            font-size: 1rem;
            font-weight: 700;
            color: #1e3a8a;
            margin-bottom: 8px;
        }

        .feature-box p {
            font-size: 0.86rem;
            color: var(--text-muted);
            line-height: 1.5;
        }
    </style>
</head>
<body>

    <!-- Top Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Kasnet Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>Kasnet Internship Portal</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link active">Resume Analysis</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Main Container -->
    <main class="container">
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a> &rsaquo;
            <span>Resume Analysis</span>
        </div>

        <div class="page-header">
            <h2>Resume Analysis &amp; ATS Feedback</h2>
            <p>Evaluate your technical resume for keyword density, structural formatting, and ATS compatibility.</p>
        </div>

        <div class="card">
            <!-- Notice Banner -->
            <div class="notice-banner">
                <div>
                    <strong>Module Status:</strong> The automated Resume Analysis scoring engine is currently undergoing pipeline integration. Analysis functionality will be enabled soon.
                </div>
            </div>

            <!-- Upload Area Placeholder -->
            <div class="upload-dropzone">
                <div class="dropzone-icon">
                    <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
                </div>
                <div class="dropzone-title">Resume Document Upload</div>
                <p class="dropzone-sub">Upload your resume in PDF or DOCX format (Maximum file size: 5MB)</p>
                <button type="button" class="btn-disabled" disabled>Analysis Feature Pending Integration</button>
            </div>

            <!-- Features Anticipated -->
            <h3 style="font-size: 1.1rem; font-weight: 700; color: #0f172a; margin-bottom: 14px;">Planned Analysis Metrics</h3>
            <div class="features-grid">
                <div class="feature-box">
                    <h4>ATS Score Calculation</h4>
                    <p>Automated verification against Applicant Tracking Systems to ensure parser readability and standard structure.</p>
                </div>
                <div class="feature-box">
                    <h4>Skill &amp; Keyword Gap Analysis</h4>
                    <p>Comparison of stated technical skills against industry standard job descriptions for full stack and cloud roles.</p>
                </div>
                <div class="feature-box">
                    <h4>Formatting &amp; Section Layout</h4>
                    <p>Guidance on section ordering, contact information visibility, project bullet points, and education details.</p>
                </div>
            </div>

            <div style="margin-top: 28px; padding-top: 18px; border-top: 1px solid var(--border);">
                <a href="${pageContext.request.contextPath}/dashboard" style="font-size: 0.88rem; color: var(--accent); font-weight: 600; text-decoration: none;">&larr; Back to Dashboard</a>
            </div>
        </div>
    </main>

</body>
</html>
