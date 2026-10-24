<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (!com.kasnet.util.SessionUtil.requireLogin(request, response)) {
        return;
    }
    if (!com.kasnet.util.SessionUtil.isStudent(request)) {
        if (com.kasnet.util.SessionUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/assignments");
            return;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${assignment.title}"/> - Assignment</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: "Segoe UI", Arial, sans-serif; background: #f0f2f5; color: #1a1a2e; }
        .navbar { background: linear-gradient(135deg, #1a237e, #283593); color: #fff; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; gap: 16px; }
        .navbar a { color: #fff; text-decoration: none; }
        .brand { font-size: 1.15rem; font-weight: 700; }
        .nav-links { display: flex; flex-wrap: wrap; gap: 16px; align-items: center; }
        .container { width: min(1000px, 94%); margin: 28px auto; }
        .back-link { display: inline-block; margin-bottom: 18px; color: #1d4ed8; font-weight: 650; text-decoration: none; }
        .panel { background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; padding: 28px; box-shadow: 0 4px 16px rgba(0,0,0,.04); }
        .assignment-code { display: inline-block; padding: 5px 12px; border-radius: 20px; background: #e0e7ff; color: #3730a3; font-size: .8rem; font-weight: 700; }
        h1 { margin: 14px 0 6px; color: #1a237e; font-size: 2rem; }
        .subject { color: #6366f1; font-weight: 650; }
        .meta { display: flex; flex-wrap: wrap; gap: 22px; margin: 20px 0; padding: 14px 0; border-top: 1px solid #e5e7eb; border-bottom: 1px solid #e5e7eb; color: #4b5563; }
        .section { margin-top: 24px; }
        .section h2 { margin: 0 0 10px; color: #1e3a8a; font-size: 1.15rem; }
        .content { color: #374151; line-height: 1.7; white-space: pre-wrap; overflow-wrap: anywhere; }
        .pdf-viewer { display: block; width: 100%; height: 760px; border: 1px solid #dbeafe; border-radius: 8px; background: #f8fbff; }
        @media (max-width: 600px) {
            .navbar { align-items: flex-start; flex-direction: column; }
            .panel { padding: 20px; }
            h1 { font-size: 1.6rem; }
            .pdf-viewer { height: 560px; }
        }
    </style>
</head>
<body>
<nav class="navbar">
    <a class="brand" href="${pageContext.request.contextPath}/home">My Internship Technologies</a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/home">Home</a>
        <a href="${pageContext.request.contextPath}/assignments">Assignments</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</nav>
<main class="container">
    <a class="back-link" href="${pageContext.request.contextPath}/assignments">&larr; Back to assignments</a>
    <article class="panel">
        <span class="assignment-code"><c:out value="${assignment.assignmentNumber}"/></span>
        <h1><c:out value="${assignment.title}"/></h1>
        <div class="subject"><c:out value="${assignment.subject}"/></div>
        <div class="meta">
            <span>Due: <strong><fmt:formatDate value="${assignment.dueDate}" pattern="dd MMM yyyy"/></strong></span>
            <span>Maximum marks: <strong><c:out value="${assignment.totalMarks}"/></strong></span>
        </div>

        <section class="section">
            <h2>Description</h2>
            <div class="content"><c:out value="${assignment.description}"/></div>
        </section>

        <c:if test="${not empty assignment.instructionsText}">
            <section class="section">
                <h2>Instructions</h2>
                <div class="content"><c:out value="${assignment.instructionsText}"/></div>
            </section>
        </c:if>

        <c:if test="${not empty assignment.instructionPdfPath}">
            <section class="section">
                <h2>Attached instructions PDF</h2>
                <iframe class="pdf-viewer"
                        src="${pageContext.request.contextPath}/assignment-instructions?assignmentId=${assignment.assignmentId}#toolbar=0&navpanes=0"
                        title="Assignment instructions PDF"></iframe>
            </section>
        </c:if>
    </article>
</main>
</body>
</html>
