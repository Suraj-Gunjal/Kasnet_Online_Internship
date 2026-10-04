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
    <title>My Assignments - Kasnet Internship Portal</title>
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

        .container {
            max-width: 1300px;
            margin: 24px auto;
            padding: 0 20px;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            flex-wrap: wrap;
            gap: 16px;
        }

        .page-title {
            font-size: 1.8rem;
            font-weight: 800;
            color: #1a237e;
        }

        .page-subtitle {
            font-size: 0.9rem;
            color: #6b7280;
            margin-top: 4px;
        }

        .alert-success {
            background: #def7ec;
            color: #03543f;
            border: 1px solid #84e1bc;
            padding: 12px 16px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 0.9rem;
        }

        /* Assignment Cards Grid */
        .cards-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
            gap: 24px;
        }

        .card {
            background: #ffffff;
            border-radius: 14px;
            padding: 24px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
        }

        .card-top {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 12px;
        }

        .assign-num {
            background: #e0e7ff;
            color: #3730a3;
            font-size: 0.75rem;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 20px;
            letter-spacing: 0.5px;
        }

        .status-badge {
            font-size: 0.75rem;
            font-weight: 700;
            padding: 4px 12px;
            border-radius: 20px;
            text-transform: uppercase;
        }

        .status-pending { background: #fef3c7; color: #92400e; }
        .status-submitted { background: #dcfce7; color: #166534; }
        .status-late { background: #fee2e2; color: #991b1b; }
        .status-evaluated { background: #e0f2fe; color: #075985; }

        .card-title {
            font-size: 1.2rem;
            font-weight: 700;
            color: #111827;
            margin-bottom: 6px;
        }

        .card-subject {
            font-size: 0.85rem;
            color: #6366f1;
            font-weight: 600;
            margin-bottom: 12px;
        }

        .card-desc {
            font-size: 0.88rem;
            color: #4b5563;
            line-height: 1.5;
            margin-bottom: 16px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .card-meta {
            display: flex;
            justify-content: space-between;
            font-size: 0.82rem;
            color: #6b7280;
            border-top: 1px solid #f3f4f6;
            padding-top: 12px;
            margin-bottom: 18px;
        }

        .meta-due {
            font-weight: 600;
            color: #dc2626;
        }

        .form-upload {
            background: #f9fafb;
            padding: 12px;
            border-radius: 8px;
            border: 1px dashed #d1d5db;
        }

        .form-upload input[type="file"] {
            font-size: 0.82rem;
            width: 100%;
            margin-bottom: 8px;
        }

        .btn-submit-task {
            background: #1a237e;
            color: #ffffff;
            border: none;
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 0.85rem;
            font-weight: 600;
            cursor: pointer;
            width: 100%;
            transition: background 0.2s;
        }

        .btn-submit-task:hover {
            background: #283593;
        }

        .submitted-info {
            background: #f0fdf4;
            border: 1px solid #bbf7d0;
            border-radius: 8px;
            padding: 10px 14px;
            font-size: 0.85rem;
            color: #15803d;
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
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link active">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link">Lectures</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Content -->
    <div class="container">
        <div class="page-header">
            <div>
                <h1 class="page-title">Internship Assignments</h1>
                <p class="page-subtitle">Complete and upload assigned coursework tasks for review and evaluation.</p>
            </div>
        </div>

        <c:if test="${param.success == '1'}">
            <div class="alert-success">
                ✓ Assignment solution submitted successfully!
            </div>
        </c:if>

        <div class="cards-grid">
            <c:forEach items="${assignmentList}" var="asn">
                <div class="card">
                    <div>
                        <div class="card-top">
                            <span class="assign-num">${asn.assignmentNumber}</span>
                            <span class="status-badge status-${asn.status.toLowerCase()}">${asn.status}</span>
                        </div>
                        <h2 class="card-title">${asn.title}</h2>
                        <div class="card-subject">📚 ${asn.subject}</div>
                        <p class="card-desc">${asn.description}</p>
                    </div>

                    <div>
                        <div class="card-meta">
                            <span>Max Marks: <strong>${asn.totalMarks}</strong></span>
                            <span class="meta-due">Due: <strong><fmt:formatDate value="${asn.dueDate}" pattern="dd MMM yyyy" /></strong></span>
                        </div>

                        <c:choose>
                            <c:when test="${asn.status == 'Submitted' || asn.status == 'Evaluated'}">
                                <div class="submitted-info">
                                    ✓ Submitted on: <fmt:formatDate value="${asn.submissionDate}" pattern="dd-MM-yyyy HH:mm" /><br>
                                    <c:if test="${asn.status == 'Evaluated'}">
                                        <strong>Marks: ${asn.marksObtained} / ${asn.totalMarks}</strong><br>
                                        <c:if test="${not empty asn.remarks}">Remarks: ${asn.remarks}</c:if>
                                    </c:if>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/assignments" method="post" enctype="multipart/form-data" class="form-upload">
                                    <input type="hidden" name="action" value="submit">
                                    <input type="hidden" name="assignmentId" value="${asn.assignmentId}">
                                    <input type="file" name="submissionFile" accept=".pdf,.doc,.docx,.zip" required>
                                    <button type="submit" class="btn-submit-task">📤 Submit Assignment</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

</body>
</html>
