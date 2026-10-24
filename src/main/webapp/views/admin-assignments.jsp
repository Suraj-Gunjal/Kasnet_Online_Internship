<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    if (!com.kasnet.util.SessionUtil.requireAdmin(request, response)) {
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Assignment Management - Kasnet Admin</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; font-family: "Segoe UI", Arial, sans-serif; background: #f3f6fb; color: #172033; }
        nav { background: #1e3a8a; color: #fff; padding: 15px 5%; display: flex; justify-content: space-between; gap: 16px; }
        nav a { color: #fff; text-decoration: none; font-weight: 600; }
        main { width: min(1180px, 94%); margin: 28px auto; }
        h1 { margin: 0 0 8px; color: #1e3a8a; }
        .muted { color: #64748b; margin: 0 0 22px; }
        .panel { background: #fff; border: 1px solid #e2e8f0; border-radius: 12px; padding: 24px; margin-bottom: 24px; box-shadow: 0 4px 14px #0f172a0a; }
        .form-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
        label { display: block; font-size: .9rem; font-weight: 650; margin-bottom: 6px; }
        input, textarea { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 7px; font: inherit; }
        input:focus, textarea:focus { outline: 2px solid #93c5fd; border-color: #2563eb; }
        textarea { min-height: 110px; resize: vertical; }
        .full { grid-column: 1 / -1; }
        .hint { color: #64748b; font-size: .82rem; margin: 5px 0 0; }
        .check { display: flex; align-items: center; gap: 9px; }
        .check input { width: auto; }
        button, .button { display: inline-block; border: 0; border-radius: 7px; padding: 10px 15px; font-weight: 650; font-size: .9rem; cursor: pointer; text-decoration: none; }
        .primary { color: #fff; background: #1d4ed8; }
        .secondary { color: #1e3a8a; background: #e0eafe; }
        .danger { color: #991b1b; background: #fee2e2; }
        .actions { display: flex; flex-wrap: wrap; align-items: center; gap: 9px; }
        .notice { border-radius: 8px; padding: 12px 15px; margin-bottom: 16px; }
        .success { color: #065f46; background: #ecfdf5; border: 1px solid #a7f3d0; }
        .error { color: #991b1b; background: #fef2f2; border: 1px solid #fecaca; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; min-width: 760px; }
        th, td { text-align: left; padding: 12px 10px; border-bottom: 1px solid #e2e8f0; vertical-align: top; }
        th { color: #475569; font-size: .82rem; background: #f8fafc; }
        td { font-size: .9rem; }
        .status { font-weight: 700; }
        .active { color: #15803d; }
        .inactive { color: #b91c1c; }
        @media (max-width: 680px) {
            nav { flex-direction: column; }
            main { width: 94%; margin-top: 18px; }
            .panel { padding: 17px; }
            .form-grid { grid-template-columns: 1fr; }
            .full { grid-column: auto; }
        }
    </style>
</head>
<body>
<nav>
    <a href="${pageContext.request.contextPath}/admin">Kasnet Administration</a>
    <div class="actions">
        <a href="${pageContext.request.contextPath}/admin">Admin dashboard</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</nav>
<main>
    <h1>Assignment management</h1>
    <p class="muted">Create, edit, publish, deactivate, and republish student assignments.</p>

    <c:if test="${not empty successMessage}">
        <div class="notice success"><c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="notice error"><c:out value="${errorMessage}"/></div>
    </c:if>
    <c:if test="${not empty loadError}">
        <div class="notice error"><c:out value="${loadError}"/></div>
    </c:if>
    <c:if test="${flashError}">
        <div class="notice error"><c:out value="${flashMessage}"/></div>
    </c:if>

    <section class="panel">
        <h2><c:choose><c:when test="${formAssignment.assignmentId > 0}">Edit assignment</c:when><c:otherwise>Create assignment</c:otherwise></c:choose></h2>
        <form action="${pageContext.request.contextPath}/admin/assignments" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
            <input type="hidden" name="action" value="save">
            <input type="hidden" name="assignmentId" value="<c:out value='${formAssignment.assignmentId}'/>">
            <div class="form-grid">
                <div>
                    <label for="title">Assignment title *</label>
                    <input id="title" name="title" maxlength="200" required value="<c:out value='${formAssignment.title}'/>">
                </div>
                <div>
                    <label for="assignmentNumber">Assignment number/code *</label>
                    <input id="assignmentNumber" name="assignmentNumber" maxlength="50" required pattern="[A-Za-z0-9]+(-[A-Za-z0-9]+)*" placeholder="ASN-003" value="<c:out value='${formAssignment.assignmentNumber}'/>">
                </div>
                <div>
                    <label for="subject">Subject/course *</label>
                    <input id="subject" name="subject" maxlength="200" required value="<c:out value='${formAssignment.subject}'/>">
                </div>
                <div>
                    <label for="dueDate">Due date *</label>
                    <input id="dueDate" type="date" name="dueDate" required value="<fmt:formatDate value='${formAssignment.dueDate}' pattern='yyyy-MM-dd'/>">
                </div>
                <div class="full">
                    <label for="description">Assignment description *</label>
                    <textarea id="description" name="description" maxlength="5000" required><c:out value="${formAssignment.description}"/></textarea>
                </div>
                <div class="full">
                    <label for="instructionsText">Detailed text instructions</label>
                    <textarea id="instructionsText" name="instructionsText" maxlength="16000"><c:out value="${formAssignment.instructionsText}"/></textarea>
                    <p class="hint">Text instructions may be used alone or together with a PDF.</p>
                </div>
                <div>
                    <label for="instructionPdf">Instruction PDF (optional, maximum 5 MB)</label>
                    <input id="instructionPdf" type="file" name="instructionPdf" accept=".pdf,application/pdf">
                    <c:if test="${not empty formAssignment.instructionPdfPath}">
                        <p class="hint">A PDF is already attached. <a href="${pageContext.request.contextPath}/assignment-instructions?assignmentId=${formAssignment.assignmentId}" target="_blank" rel="noopener">Open current PDF</a>. Leave this field empty to keep it.</p>
                        <label class="check"><input type="checkbox" name="removePdf" value="true"> Remove current PDF</label>
                    </c:if>
                </div>
                <div>
                    <label for="totalMarks">Maximum marks *</label>
                    <input id="totalMarks" type="number" name="totalMarks" min="1" max="100000" step="1" required value="<c:out value='${formTotalMarks != null ? formTotalMarks : formAssignment.totalMarks}'/>">
                </div>
                <div class="full check">
                    <input id="isActive" type="checkbox" name="isActive" value="true" <c:if test="${formAssignment.active}">checked</c:if>>
                    <label for="isActive" style="margin:0">Publish assignment immediately (active)</label>
                </div>
            </div>
            <p class="hint">Required: title, code, subject, description, due date, marks, and either text instructions or a PDF.</p>
            <div class="actions" style="margin-top:16px">
                <button class="primary" type="submit"><c:choose><c:when test="${formAssignment.assignmentId > 0}">Save changes</c:when><c:otherwise>Create assignment</c:otherwise></c:choose></button>
                <c:if test="${formAssignment.assignmentId > 0}">
                    <a class="button secondary" href="${pageContext.request.contextPath}/admin/assignments">Cancel edit</a>
                </c:if>
            </div>
        </form>
    </section>

    <section class="panel">
        <h2>Existing assignments</h2>
        <c:choose>
            <c:when test="${empty assignmentList}">
                <p class="muted" style="margin:12px 0 0">No assignments have been created yet.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>Code</th><th>Assignment</th><th>Due date</th><th>Marks</th><th>Status</th><th>Actions</th></tr></thead>
                        <tbody>
                        <c:forEach items="${assignmentList}" var="asn">
                            <tr>
                                <td><c:out value="${asn.assignmentNumber}"/></td>
                                <td><strong><c:out value="${asn.title}"/></strong><br><span class="muted"><c:out value="${asn.subject}"/></span></td>
                                <td><fmt:formatDate value="${asn.dueDate}" pattern="dd MMM yyyy"/></td>
                                <td><c:out value="${asn.totalMarks}"/></td>
                                <td class="status ${asn.active ? 'active' : 'inactive'}"><c:out value="${asn.active ? 'Published' : 'Inactive'}"/></td>
                                <td>
                                    <div class="actions">
                                        <a class="button secondary" href="${pageContext.request.contextPath}/admin/assignments?edit=${asn.assignmentId}">Edit</a>
                                        <form action="${pageContext.request.contextPath}/admin/assignments" method="post">
                                            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                                            <input type="hidden" name="action" value="toggle">
                                            <input type="hidden" name="assignmentId" value="${asn.assignmentId}">
                                            <input type="hidden" name="active" value="${!asn.active}">
                                            <button class="${asn.active ? 'danger' : 'primary'}" type="submit"><c:out value="${asn.active ? 'Deactivate' : 'Republish'}"/></button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>
