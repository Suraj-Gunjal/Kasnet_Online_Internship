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
    <title>Student Profile - Kasnet Internship Portal</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background-color: #f4f6f9;
            color: #333;
        }

        /* Top Navigation Bar */
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

        /* Main Container Layout */
        .container {
            display: flex;
            max-width: 1400px;
            margin: 24px auto;
            padding: 0 16px;
            gap: 24px;
        }

        /* Left Sidebar matching screenshot */
        .sidebar {
            width: 300px;
            background: #23585c; /* Teal/dark slate background matching screenshot */
            border-radius: 8px;
            padding: 24px 16px;
            color: #fff;
            flex-shrink: 0;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        /* Avatar silhouette container - NO photo upload per user instruction */
        .avatar-container {
            position: relative;
            width: 150px;
            height: 150px;
            border-radius: 50%;
            background: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 24px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            overflow: hidden;
        }

        .avatar-silhouette {
            width: 120px;
            height: 120px;
            fill: #2d3748;
        }

        /* Profile Completion Card */
        .completion-card {
            width: 100%;
            background: #39b54a; /* Green header matching screenshot */
            border-radius: 6px;
            overflow: hidden;
            margin-bottom: 20px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        .completion-header {
            padding: 10px 14px;
            font-size: 1.1rem;
            font-weight: 600;
            color: #111;
        }

        .completion-body {
            background: #ffffff;
            padding: 14px;
            color: #333;
        }

        .progress-bar-bg {
            height: 12px;
            background: #e2e8f0;
            border-radius: 6px;
            overflow: hidden;
            margin-bottom: 6px;
        }

        .progress-bar-fill {
            height: 100%;
            background: #48bb78;
            width: ${profile.profileCompletion > 0 ? profile.profileCompletion : 20}%;
            border-radius: 6px;
            transition: width 0.4s ease;
        }

        .completion-text {
            font-size: 0.85rem;
            color: #4a5568;
            text-align: right;
            font-weight: 600;
        }

        /* Details Card */
        .details-card {
            width: 100%;
            background: #39b54a;
            border-radius: 6px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        .details-header {
            padding: 10px 14px;
            font-size: 1.1rem;
            font-weight: 600;
            color: #111;
        }

        .details-body {
            background: #ffffff;
            color: #333;
        }

        .detail-item {
            padding: 12px 14px;
            border-bottom: 1px solid #edf2f7;
            font-size: 0.85rem;
        }

        .detail-item:last-child {
            border-bottom: none;
        }

        .detail-label {
            font-weight: 700;
            color: #2d3748;
            margin-bottom: 4px;
        }

        .detail-value {
            color: #4a5568;
        }

        /* Main Content Area */
        .main-content {
            flex-grow: 1;
            background: #ffffff;
            border-radius: 8px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            padding: 24px;
            min-width: 0;
        }

        /* Horizontal Tabs matching screenshots */
        .tabs-header {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            border-bottom: 2px solid #e2e8f0;
            margin-bottom: 28px;
            padding-bottom: 4px;
        }

        .tab-btn {
            background: none;
            border: none;
            outline: none;
            cursor: pointer;
            padding: 8px 16px;
            font-size: 1rem;
            font-weight: 700;
            color: #e65100; /* Orange-red matching screenshot tabs */
            border-radius: 6px 6px 0 0;
            transition: all 0.2s;
            text-decoration: none;
            display: inline-block;
        }

        .tab-btn:hover {
            background: rgba(230, 81, 0, 0.08);
        }

        .tab-btn.active {
            color: #2d3748;
            border: 1px solid #cbd5e0;
            border-bottom: 2px solid #ffffff;
            background: #ffffff;
            margin-bottom: -6px;
            border-radius: 6px 6px 0 0;
            padding-bottom: 12px;
        }

        /* Alert notifications */
        .alert-success {
            background: #def7ec;
            color: #03543f;
            border: 1px solid #84e1bc;
            padding: 12px 16px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 0.9rem;
        }

        /* Form Styles matching screenshots */
        .form-section-title {
            font-size: 1.15rem;
            font-weight: 700;
            text-decoration: underline;
            color: #1a202c;
            margin-top: 24px;
            margin-bottom: 16px;
        }

        .form-section-title:first-child {
            margin-top: 0;
        }

        .form-grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px 32px;
            margin-bottom: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .form-label {
            font-size: 1.05rem;
            font-weight: 700;
            color: #1a202c;
        }

        .form-input, .form-select, .form-textarea {
            width: 100%;
            padding: 10px 14px;
            background: #e2e8f0; /* Gray input fill matching screenshots */
            border: 1px solid #cbd5e0;
            border-radius: 4px;
            font-size: 0.95rem;
            color: #2d3748;
            outline: none;
            transition: border-color 0.2s, background 0.2s;
        }

        .form-input:focus, .form-select:focus, .form-textarea:focus {
            background: #edf2f7;
            border-color: #3182ce;
        }

        .form-input[readonly] {
            background: #edf2f7;
            color: #718096;
            cursor: not-allowed;
        }

        .form-hint {
            font-size: 0.78rem;
            color: #718096;
            margin-top: 4px;
        }

        /* Buttons matching screenshot */
        .btn-group {
            display: flex;
            gap: 12px;
            margin-top: 28px;
        }

        .btn-save {
            background: #48bb78; /* Green SAVE button matching screenshot */
            color: #ffffff;
            border: none;
            padding: 8px 24px;
            font-size: 0.9rem;
            font-weight: 700;
            border-radius: 4px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: background 0.2s;
        }

        .btn-save:hover {
            background: #38a169;
        }

        .btn-edit {
            background: #edf2f7;
            color: #2d3748;
            border: 1px solid #cbd5e0;
            padding: 8px 20px;
            font-size: 0.9rem;
            font-weight: 600;
            border-radius: 4px;
            cursor: pointer;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            transition: background 0.2s;
        }

        .btn-edit:hover {
            background: #e2e8f0;
        }

        .btn-next {
            background: #edf2f7;
            color: #2d3748;
            border: 1px solid #a0aec0;
            padding: 8px 24px;
            font-size: 0.9rem;
            font-weight: 600;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            transition: background 0.2s;
        }

        .btn-next:hover {
            background: #e2e8f0;
        }

        /* Skills Chips / Tag UI */
        .skills-tag-container {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-top: 8px;
            min-height: 36px;
            padding: 6px;
            border: 1px dashed #cbd5e0;
            border-radius: 4px;
            background: #f7fafc;
        }

        .skill-chip {
            background: #ebf8ff;
            color: #2b6cb0;
            border: 1px solid #bee3f8;
            border-radius: 16px;
            padding: 4px 12px;
            font-size: 0.85rem;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .skill-chip .chip-remove {
            cursor: pointer;
            font-weight: 800;
            color: #e53e3e;
            font-size: 1rem;
        }

        /* Table styles for items */
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
            margin-bottom: 24px;
            font-size: 0.9rem;
        }

        .data-table th, .data-table td {
            border: 1px solid #e2e8f0;
            padding: 10px 12px;
            text-align: left;
        }

        .data-table th {
            background: #f7fafc;
            color: #4a5568;
            font-weight: 700;
        }

        .data-table tr:hover {
            background: #f8fafc;
        }

        .btn-danger-sm {
            background: #feb2b2;
            color: #9b2c2c;
            border: none;
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.8rem;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-danger-sm:hover {
            background: #fc8181;
            color: #fff;
        }

        /* Responsive */
        @media (max-width: 900px) {
            .container {
                flex-direction: column;
            }
            .sidebar {
                width: 100%;
            }
            .form-grid-2 {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>

    <!-- Top Navigation Bar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/dashboard" class="navbar-brand">
            <div class="logo-icon">K</div>
            <span>Kasnet Internship Portal</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link active">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <span style="font-size: 0.9rem; color: #cbd5e0; margin-left: 8px;">Hi, ${user.firstName}</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container">

        <!-- Left Sidebar matching screenshot -->
        <aside class="sidebar">
            <!-- Silhouette Avatar - NO photo upload or resume buttons per instructions -->
            <div class="avatar-container">
                <svg class="avatar-silhouette" viewBox="0 0 24 24">
                    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
            </div>

            <!-- Profile Completion Card -->
            <div class="completion-card">
                <div class="completion-header">Profile Completion</div>
                <div class="completion-body">
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill"></div>
                    </div>
                    <div class="completion-text">${profile.profileCompletion > 0 ? profile.profileCompletion : 20}.0%</div>
                </div>
            </div>

            <!-- Details Card -->
            <div class="details-card">
                <div class="details-header">Details</div>
                <div class="details-body">
                    <div class="detail-item">
                        <div class="detail-label">Account Created Date</div>
                        <div class="detail-value">
                            <fmt:formatDate value="${user.createdAt}" pattern="EEE, dd-MM-yyyy HH:mm:ss" />
                        </div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Previous Login Date</div>
                        <div class="detail-value">
                            <c:choose>
                                <c:when test="${not empty profile.previousLogin}">
                                    <fmt:formatDate value="${profile.previousLogin}" pattern="EEE, dd-MM-yyyy HH:mm:ss" />
                                </c:when>
                                <c:otherwise>First Login Session</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Previous Spend Time</div>
                        <div class="detail-value">Active Session</div>
                    </div>
                </div>
            </div>
        </aside>

        <!-- Main Content Area -->
        <main class="main-content">

            <!-- Success notification -->
            <c:if test="${param.success == '1'}">
                <div class="alert-success">
                    ✓ Profile section details updated and saved successfully!
                </div>
            </c:if>

            <!-- Tabs Navigation Bar matching screenshots -->
            <div class="tabs-header">
                <a href="${pageContext.request.contextPath}/profile?tab=personal" class="tab-btn ${activeTab == 'personal' ? 'active' : ''}">Personal Details</a>
                <a href="${pageContext.request.contextPath}/profile?tab=education" class="tab-btn ${activeTab == 'education' ? 'active' : ''}">Education Details</a>
                <a href="${pageContext.request.contextPath}/profile?tab=achievements" class="tab-btn ${activeTab == 'achievements' ? 'active' : ''}">Achievement</a>
                <a href="${pageContext.request.contextPath}/profile?tab=skills" class="tab-btn ${activeTab == 'skills' ? 'active' : ''}">Skill's</a>
                <a href="${pageContext.request.contextPath}/profile?tab=certifications" class="tab-btn ${activeTab == 'certifications' ? 'active' : ''}">Certification</a>
                <a href="${pageContext.request.contextPath}/profile?tab=projects" class="tab-btn ${activeTab == 'projects' ? 'active' : ''}">Project</a>
                <a href="${pageContext.request.contextPath}/profile?tab=activities" class="tab-btn ${activeTab == 'activities' ? 'active' : ''}">Additional Activities</a>
            </div>

            <!-- TAB 1: PERSONAL DETAILS (image4.png reference) -->
            <c:if test="${activeTab == 'personal'}">
                <form action="${pageContext.request.contextPath}/personal-details" method="post">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">First name</label>
                            <input type="text" name="firstName" class="form-input" value="${user.firstName}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Last name</label>
                            <input type="text" name="lastName" class="form-input" value="${user.lastName}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Gender</label>
                            <select name="gender" class="form-select">
                                <option value="" disabled ${empty profile.gender ? 'selected' : ''}>Select Gender</option>
                                <option value="Male" ${profile.gender == 'Male' ? 'selected' : ''}>Male</option>
                                <option value="Female" ${profile.gender == 'Female' ? 'selected' : ''}>Female</option>
                                <option value="Other" ${profile.gender == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Date of Birth</label>
                            <input type="date" name="dob" class="form-input" value="${profile.dateOfBirth}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Mobile</label>
                            <input type="text" name="mobile" class="form-input" value="${user.mobile}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Email</label>
                            <input type="email" name="email" class="form-input" value="${user.email}" readonly>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Address</label>
                            <input type="text" name="address" class="form-input" placeholder="somewhere" value="${profile.address}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">City</label>
                            <input type="text" name="city" class="form-input" placeholder="PUNE" value="${user.city}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">State</label>
                            <input type="text" name="state" class="form-input" placeholder="Enter State" value="${profile.state}">
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">✔ SAVE</button>
                        <button type="button" class="btn-edit" onclick="window.location.reload();">↻ EDIT</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=education" class="btn-next">NEXT</a>
                    </div>
                </form>
            </c:if>

            <!-- TAB 2: EDUCATION DETAILS (image5.png & image6.png reference) -->
            <c:if test="${activeTab == 'education'}">
                <form action="${pageContext.request.contextPath}/education" method="post">
                    <!-- SSC/10th -->
                    <h3 class="form-section-title">SSC/10th</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">School Name</label>
                            <input type="text" name="schoolName_SSC" class="form-input" placeholder="Enter School Name" value="${educationMap['SSC'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Board</label>
                            <input type="text" name="board_SSC" class="form-input" placeholder="Enter 10th Board" value="${educationMap['SSC'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="text" name="year_SSC" class="form-input" placeholder="10th Passing year" value="${educationMap['SSC'].passingYear > 0 ? educationMap['SSC'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage</label>
                            <input type="text" name="percentage_SSC" class="form-input" placeholder="10th Percentage" value="${educationMap['SSC'].percentage > 0 ? educationMap['SSC'].percentage : ''}">
                        </div>
                    </div>

                    <!-- HSC/12th -->
                    <h3 class="form-section-title">HSC/12th</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">School Name</label>
                            <input type="text" name="schoolName_HSC" class="form-input" placeholder="Enter School Name" value="${educationMap['HSC'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Board</label>
                            <input type="text" name="board_HSC" class="form-input" placeholder="12 th Board" value="${educationMap['HSC'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream</label>
                            <select name="stream_HSC" class="form-select">
                                <option value="" disabled ${empty educationMap['HSC'].stream ? 'selected' : ''}>Select 12th Stream</option>
                                <option value="Science" ${educationMap['HSC'].stream == 'Science' ? 'selected' : ''}>Science</option>
                                <option value="Commerce" ${educationMap['HSC'].stream == 'Commerce' ? 'selected' : ''}>Commerce</option>
                                <option value="Arts" ${educationMap['HSC'].stream == 'Arts' ? 'selected' : ''}>Arts</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="text" name="year_HSC" class="form-input" placeholder="12th Passing year" value="${educationMap['HSC'].passingYear > 0 ? educationMap['HSC'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage</label>
                            <input type="text" name="percentage_HSC" class="form-input" placeholder="12th Percentage" value="${educationMap['HSC'].percentage > 0 ? educationMap['HSC'].percentage : ''}">
                        </div>
                    </div>

                    <!-- Graduation -->
                    <h3 class="form-section-title">Graduation</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">College Name</label>
                            <input type="text" name="schoolName_Graduation" class="form-input" placeholder="VIIT PUNE" value="${educationMap['Graduation'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">University</label>
                            <input type="text" name="board_Graduation" class="form-input" placeholder="University Name" value="${educationMap['Graduation'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream</label>
                            <select name="stream_Graduation" class="form-select">
                                <option value="" disabled ${empty educationMap['Graduation'].stream ? 'selected' : ''}>Select Graduation Stream</option>
                                <option value="B.Tech / B.E." ${educationMap['Graduation'].stream == 'B.Tech / B.E.' ? 'selected' : ''}>B.Tech / B.E.</option>
                                <option value="BCA" ${educationMap['Graduation'].stream == 'BCA' ? 'selected' : ''}>BCA</option>
                                <option value="B.Sc CS/IT" ${educationMap['Graduation'].stream == 'B.Sc CS/IT' ? 'selected' : ''}>B.Sc CS/IT</option>
                                <option value="Other" ${educationMap['Graduation'].stream == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="text" name="year_Graduation" class="form-input" placeholder="Graduation Passing year" value="${educationMap['Graduation'].passingYear > 0 ? educationMap['Graduation'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage</label>
                            <input type="text" name="percentage_Graduation" class="form-input" placeholder="Enter Percentage" value="${educationMap['Graduation'].percentage > 0 ? educationMap['Graduation'].percentage : ''}">
                        </div>
                    </div>

                    <!-- Master -->
                    <h3 class="form-section-title">Master</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">College Name</label>
                            <input type="text" name="schoolName_Master" class="form-input" placeholder="College Name" value="${educationMap['Master'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">University</label>
                            <input type="text" name="board_Master" class="form-input" placeholder="Enter University" value="${educationMap['Master'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream</label>
                            <select name="stream_Master" class="form-select">
                                <option value="" disabled ${empty educationMap['Master'].stream ? 'selected' : ''}>Select Master Stream</option>
                                <option value="M.Tech / M.E." ${educationMap['Master'].stream == 'M.Tech / M.E.' ? 'selected' : ''}>M.Tech / M.E.</option>
                                <option value="MCA" ${educationMap['Master'].stream == 'MCA' ? 'selected' : ''}>MCA</option>
                                <option value="M.Sc CS/IT" ${educationMap['Master'].stream == 'M.Sc CS/IT' ? 'selected' : ''}>M.Sc CS/IT</option>
                                <option value="MBA" ${educationMap['Master'].stream == 'MBA' ? 'selected' : ''}>MBA</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="text" name="year_Master" class="form-input" placeholder="Master Passing year" value="${educationMap['Master'].passingYear > 0 ? educationMap['Master'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage</label>
                            <input type="text" name="percentage_Master" class="form-input" placeholder="Enter Percentage" value="${educationMap['Master'].percentage > 0 ? educationMap['Master'].percentage : ''}">
                        </div>
                    </div>

                    <!-- Diploma -->
                    <h3 class="form-section-title">Diploma</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">School/College Name</label>
                            <input type="text" name="schoolName_Diploma" class="form-input" placeholder="Enter School/College name" value="${educationMap['Diploma'].schoolCollegeName}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Board</label>
                            <input type="text" name="board_Diploma" class="form-input" placeholder="Enter Board, If not Enter NA" value="${educationMap['Diploma'].boardUniversity}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Stream</label>
                            <select name="stream_Diploma" class="form-select">
                                <option value="" disabled ${empty educationMap['Diploma'].stream ? 'selected' : ''}>Select Stream</option>
                                <option value="Polytechnic" ${educationMap['Diploma'].stream == 'Polytechnic' ? 'selected' : ''}>Polytechnic</option>
                                <option value="CS / IT" ${educationMap['Diploma'].stream == 'CS / IT' ? 'selected' : ''}>CS / IT</option>
                                <option value="Mechanical" ${educationMap['Diploma'].stream == 'Mechanical' ? 'selected' : ''}>Mechanical</option>
                                <option value="Other" ${educationMap['Diploma'].stream == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Passing Year</label>
                            <input type="text" name="year_Diploma" class="form-input" placeholder="Diploma Passing year" value="${educationMap['Diploma'].passingYear > 0 ? educationMap['Diploma'].passingYear : ''}">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Percentage</label>
                            <input type="text" name="percentage_Diploma" class="form-input" placeholder="Enter Percentage, If not Enter NA" value="${educationMap['Diploma'].percentage > 0 ? educationMap['Diploma'].percentage : ''}">
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">✔ SAVE</button>
                        <button type="button" class="btn-edit" onclick="window.location.reload();">↻ EDIT</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=achievements" class="btn-next">NEXT</a>
                    </div>
                </form>
            </c:if>

            <!-- TAB 3: ACHIEVEMENTS (image7.png reference) -->
            <c:if test="${activeTab == 'achievements'}">
                <form action="${pageContext.request.contextPath}/achievements" method="post">
                    <input type="hidden" name="action" value="save">

                    <!-- Certification Sub-section -->
                    <h3 class="form-section-title">Certification</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Certification</label>
                            <select name="hasCertification" class="form-select">
                                <option value="Yes" selected>Yes</option>
                                <option value="No">No</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Certification Name</label>
                            <input type="text" name="certName" class="form-input" placeholder="Certification Name">
                        </div>
                        <div></div>
                        <div class="form-group">
                            <label class="form-label">Certification Technology</label>
                            <input type="text" name="certTech" class="form-input" placeholder="Certification Technology">
                        </div>
                    </div>

                    <!-- Project Sub-section -->
                    <h3 class="form-section-title">Project</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Project Name</label>
                            <input type="text" name="projectName" class="form-input" placeholder="Enter Project Name, If not Enter NA">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Project Details</label>
                            <input type="text" name="projectDetails" class="form-input" placeholder="If not Enter NA">
                        </div>
                    </div>

                    <!-- Extra Curriculum Activities Sub-section -->
                    <h3 class="form-section-title">Extra Curriculum Activities</h3>
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Extra Activities</label>
                            <input type="text" name="extraActivities" class="form-input" placeholder="Activities, If not Enter NA">
                        </div>
                    </div>

                    <!-- Saved achievements list -->
                    <c:if test="${not empty achievementList}">
                        <h4 style="margin-top: 24px; color: #2d3748;">Saved Achievements</h4>
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Type</th>
                                    <th>Title</th>
                                    <th>Details / Tech</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${achievementList}" var="ach">
                                    <tr>
                                        <td><strong>${ach.achievementType}</strong></td>
                                        <td>${ach.title}</td>
                                        <td>${ach.technology != null ? ach.technology : ach.description}</td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/achievements" method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="achievementId" value="${ach.achievementId}">
                                                <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this achievement?');">Delete</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">✔ SAVE</button>
                        <button type="button" class="btn-edit" onclick="window.location.reload();">↻ EDIT</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=skills" class="btn-next">NEXT</a>
                    </div>
                </form>
            </c:if>

            <!-- TAB 4: SKILLS (image8.png reference) -->
            <c:if test="${activeTab == 'skills'}">
                <form action="${pageContext.request.contextPath}/skills" method="post">
                    <!-- Technical Skills -->
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Technical Skills</label>
                            <input type="text" id="technicalInput" name="technicalSkills" class="form-input" placeholder="Enter Technical Skills"
                                   value="<c:forEach items='${technicalSkills}' var='s' varStatus='st'>${s.skillName}${not st.last ? ', ' : ''}</c:forEach>">
                            <span class="form-hint">e.g. C, CPP, JAVA, PHP, SQL, Python</span>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Soft Skills:</label>
                            <input type="text" id="softInput" name="softSkills" class="form-input" placeholder="Enter SoftSkills"
                                   value="<c:forEach items='${softSkills}' var='s' varStatus='st'>${s.skillName}${not st.last ? ', ' : ''}</c:forEach>">
                            <span class="form-hint">e.g. Positive Attitude, Good Communication Skills, Time Management, Confidence</span>
                        </div>
                    </div>

                    <!-- Domain -->
                    <div class="form-grid-2" style="margin-top: 16px;">
                        <div class="form-group">
                            <label class="form-label">Domain:</label>
                            <input type="text" id="domainInput" name="domainSkills" class="form-input" placeholder="Enter Domain"
                                   value="<c:forEach items='${domainSkills}' var='s' varStatus='st'>${s.skillName}${not st.last ? ', ' : ''}</c:forEach>">
                            <span class="form-hint">e.g. Cloud Computing, Data mining, Text Mining, Machine Learning, Image Processing</span>
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit" class="btn-save">✔ SAVE</button>
                        <button type="button" class="btn-edit" onclick="window.location.reload();">↻ EDIT</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=certifications" class="btn-next">NEXT</a>
                    </div>
                </form>
            </c:if>

            <!-- TAB 5: CERTIFICATIONS (Dedicated section) -->
            <c:if test="${activeTab == 'certifications'}">
                <h3 class="form-section-title">Manage Certifications</h3>
                <form action="${pageContext.request.contextPath}/certifications" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Certification Name *</label>
                            <input type="text" name="certName" class="form-input" placeholder="e.g. AWS Certified Cloud Practitioner" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Issuing Provider / Organization</label>
                            <input type="text" name="provider" class="form-input" placeholder="e.g. Amazon Web Services / Coursera / Oracle">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Technology / Domain</label>
                            <input type="text" name="technology" class="form-input" placeholder="e.g. Java, Cloud Computing">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Certificate ID / License No.</label>
                            <input type="text" name="certIdNumber" class="form-input" placeholder="e.g. CERT-12345">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Issue Date</label>
                            <input type="date" name="issueDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Expiry Date (if applicable)</label>
                            <input type="date" name="expiryDate" class="form-input">
                        </div>
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label class="form-label">Credential Verification URL</label>
                            <input type="url" name="credentialUrl" class="form-input" placeholder="https://www.credly.com/badges/...">
                        </div>
                    </div>
                    <div class="btn-group">
                        <button type="submit" class="btn-save">+ Add Certification</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=projects" class="btn-next">NEXT</a>
                    </div>
                </form>

                <c:if test="${not empty certificationList}">
                    <h4 style="margin-top: 24px; color: #2d3748;">Your Certifications</h4>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Provider</th>
                                <th>Tech</th>
                                <th>Issue Date</th>
                                <th>Credential</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${certificationList}" var="c">
                                <tr>
                                    <td><strong>${c.certName}</strong></td>
                                    <td>${c.provider}</td>
                                    <td>${c.technology}</td>
                                    <td>${c.issueDate}</td>
                                    <td>
                                        <c:if test="${not empty c.credentialUrl}">
                                            <a href="${c.credentialUrl}" target="_blank" style="color:#3182ce;">View</a>
                                        </c:if>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/certifications" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="certId" value="${c.certId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this certification?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </c:if>

            <!-- TAB 6: PROJECTS (Dedicated section) -->
            <c:if test="${activeTab == 'projects'}">
                <h3 class="form-section-title">Manage Projects</h3>
                <form action="${pageContext.request.contextPath}/projects" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Project Name *</label>
                            <input type="text" name="projectName" class="form-input" placeholder="e.g. Student Internship Portal" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Technologies Used</label>
                            <input type="text" name="technologiesUsed" class="form-input" placeholder="e.g. Java, JSP, Servlets, MySQL">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Your Role</label>
                            <input type="text" name="role" class="form-input" placeholder="e.g. Full Stack Developer / Team Lead">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Project Type</label>
                            <select name="projectType" class="form-select">
                                <option value="Academic">Academic</option>
                                <option value="Personal">Personal</option>
                                <option value="Professional">Professional</option>
                                <option value="Open Source">Open Source</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Team Size</label>
                            <input type="number" name="teamSize" class="form-input" value="1" min="1">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Start Date</label>
                            <input type="date" name="startDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">End Date</label>
                            <input type="date" name="endDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">GitHub Repository URL</label>
                            <input type="url" name="githubUrl" class="form-input" placeholder="https://github.com/username/project">
                        </div>
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label class="form-label">Project Description</label>
                            <textarea name="description" class="form-textarea" rows="3" placeholder="Briefly describe what the project does..."></textarea>
                        </div>
                    </div>
                    <div class="btn-group">
                        <button type="submit" class="btn-save">+ Add Project</button>
                        <a href="${pageContext.request.contextPath}/profile?tab=activities" class="btn-next">NEXT</a>
                    </div>
                </form>

                <c:if test="${not empty projectList}">
                    <h4 style="margin-top: 24px; color: #2d3748;">Your Projects</h4>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Project Name</th>
                                <th>Tech Stack</th>
                                <th>Role</th>
                                <th>Type</th>
                                <th>Links</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${projectList}" var="p">
                                <tr>
                                    <td><strong>${p.projectName}</strong></td>
                                    <td>${p.technologiesUsed}</td>
                                    <td>${p.role}</td>
                                    <td>${p.projectType}</td>
                                    <td>
                                        <c:if test="${not empty p.githubUrl}">
                                            <a href="${p.githubUrl}" target="_blank" style="color:#3182ce;">GitHub</a>
                                        </c:if>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/projects" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="projectId" value="${p.projectId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this project?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </c:if>

            <!-- TAB 7: ADDITIONAL ACTIVITIES (Hackathons & Inter-College Competitions) -->
            <c:if test="${activeTab == 'activities'}">
                <!-- Section A: Hackathons -->
                <h3 class="form-section-title">A. Hackathons</h3>
                <form action="${pageContext.request.contextPath}/hackathons" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Hackathon Name *</label>
                            <input type="text" name="hackathonName" class="form-input" placeholder="e.g. Smart India Hackathon" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Organizer</label>
                            <input type="text" name="organizer" class="form-input" placeholder="e.g. AICTE / Tech Fest">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Event Date</label>
                            <input type="date" name="eventDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Position / Result</label>
                            <input type="text" name="positionResult" class="form-input" placeholder="e.g. 1st Place / Finalist / Participant">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Team Name</label>
                            <input type="text" name="teamName" class="form-input" placeholder="e.g. ByteBusters">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Description</label>
                            <input type="text" name="description" class="form-input" placeholder="Problem statement solved...">
                        </div>
                    </div>
                    <button type="submit" class="btn-save">+ Add Hackathon</button>
                </form>

                <c:if test="${not empty hackathonList}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Hackathon</th>
                                <th>Organizer</th>
                                <th>Date</th>
                                <th>Position</th>
                                <th>Team</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${hackathonList}" var="h">
                                <tr>
                                    <td><strong>${h.hackathonName}</strong></td>
                                    <td>${h.organizer}</td>
                                    <td>${h.eventDate}</td>
                                    <td>${h.positionResult}</td>
                                    <td>${h.teamName}</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/hackathons" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="hackathonId" value="${h.hackathonId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this hackathon entry?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                <!-- Section B: Inter-College Project Competitions -->
                <h3 class="form-section-title" style="margin-top: 36px;">B. Inter-College Project Competitions</h3>
                <form action="${pageContext.request.contextPath}/competitions" method="post" style="margin-bottom: 24px;">
                    <input type="hidden" name="action" value="add">
                    <div class="form-grid-2">
                        <div class="form-group">
                            <label class="form-label">Competition Name *</label>
                            <input type="text" name="competitionName" class="form-input" placeholder="e.g. TechExpo 2026" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Host College / Organizer</label>
                            <input type="text" name="collegeOrganizer" class="form-input" placeholder="e.g. COEP Pune">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Event Date</label>
                            <input type="date" name="eventDate" class="form-input">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Project Name</label>
                            <input type="text" name="projectName" class="form-input" placeholder="Project exhibited...">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Position / Result</label>
                            <input type="text" name="positionResult" class="form-input" placeholder="e.g. Runner Up / 3rd Prize">
                        </div>
                        <div class="form-group">
                            <label class="form-label">Team Name</label>
                            <input type="text" name="teamName" class="form-input" placeholder="Team name...">
                        </div>
                        <div class="form-group" style="grid-column: 1 / -1;">
                            <label class="form-label">Description</label>
                            <input type="text" name="description" class="form-input" placeholder="Competition highlights...">
                        </div>
                    </div>
                    <button type="submit" class="btn-save">+ Add Competition</button>
                </form>

                <c:if test="${not empty competitionList}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Competition</th>
                                <th>Organizer</th>
                                <th>Date</th>
                                <th>Project</th>
                                <th>Result</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${competitionList}" var="comp">
                                <tr>
                                    <td><strong>${comp.competitionName}</strong></td>
                                    <td>${comp.collegeOrganizer}</td>
                                    <td>${comp.eventDate}</td>
                                    <td>${comp.projectName}</td>
                                    <td>${comp.positionResult}</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/competitions" method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="competitionId" value="${comp.competitionId}">
                                            <button type="submit" class="btn-danger-sm" onclick="return confirm('Delete this competition entry?');">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                <div class="btn-group" style="margin-top: 32px;">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn-save" style="text-decoration:none;">Go to Dashboard &rarr;</a>
                </div>
            </c:if>

        </main>
    </div>

</body>
</html>
