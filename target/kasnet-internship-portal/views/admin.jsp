<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String loginSuccessParam = request.getParameter("loginSuccess");
    boolean isLoginSuccess = "1".equals(loginSuccessParam) || session.getAttribute("loginSuccessAlert") != null;
    if (session.getAttribute("loginSuccessAlert") != null) {
        session.removeAttribute("loginSuccessAlert");
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Portal - User Monitoring - Kasnet Internship</title>
    <style>
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        :root {
            --primary: #1a365d;
            --primary-dark: #0f172a;
            --accent: #2b6cb0;
            --accent-light: #ebf8ff;
            --text-dark: #1e293b;
            --text-muted: #64748b;
            --bg: #f8fafc;
            --card-bg: #ffffff;
            --border: #e2e8f0;
            --danger: #dc2626;
            --success: #16a34a;
            --radius: 8px;
            --font: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }

        body {
            font-family: var(--font);
            background-color: var(--bg);
            color: var(--text-dark);
            line-height: 1.5;
        }

        /* Top Bar */
        .topbar {
            background-color: var(--primary-dark);
            color: #ffffff;
            padding: 14px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .topbar-brand {
            display: flex;
            align-items: center;
            gap: 12px;
            text-decoration: none;
            color: #ffffff;
        }

        .brand-badge {
            width: 36px;
            height: 36px;
            background: #2563eb;
            color: #ffffff;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
        }

        .brand-text h1 {
            font-size: 1.05rem;
            font-weight: 700;
        }

        .brand-text span {
            font-size: 0.75rem;
            color: #94a3b8;
            letter-spacing: 0.5px;
            text-transform: uppercase;
        }

        .topbar-nav {
            display: flex;
            align-items: center;
            gap: 16px;
        }

        .admin-badge {
            background: rgba(255, 255, 255, 0.15);
            padding: 4px 10px;
            border-radius: 4px;
            font-size: 0.82rem;
            font-weight: 600;
            color: #e2e8f0;
        }

        .btn-logout {
            background-color: var(--danger);
            color: #ffffff;
            padding: 6px 14px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 0.85rem;
            font-weight: 600;
            transition: background-color 0.2s;
        }

        .btn-logout:hover {
            background-color: #b91c1c;
        }

        /* Main Container */
        .container {
            max-width: 1350px;
            margin: 28px auto;
            padding: 0 20px;
        }

        /* Page Header */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            flex-wrap: wrap;
            gap: 14px;
        }

        .page-header h2 {
            font-size: 1.5rem;
            font-weight: 700;
            color: var(--primary);
        }

        .page-header p {
            font-size: 0.9rem;
            color: var(--text-muted);
        }

        /* Metrics Grid */
        .metrics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 18px;
            margin-bottom: 24px;
        }

        .metric-card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            padding: 20px 22px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        .metric-label {
            font-size: 0.82rem;
            font-weight: 600;
            text-transform: uppercase;
            color: var(--text-muted);
            letter-spacing: 0.5px;
            margin-bottom: 8px;
        }

        .metric-value {
            font-size: 1.8rem;
            font-weight: 800;
            color: var(--primary);
        }

        /* Table Card */
        .card {
            background: var(--card-bg);
            border: 1px solid var(--border);
            border-radius: var(--radius);
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            overflow: hidden;
        }

        .card-header {
            padding: 16px 22px;
            border-bottom: 1px solid var(--border);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 12px;
            background-color: #ffffff;
        }

        .card-header h3 {
            font-size: 1.05rem;
            font-weight: 700;
            color: var(--primary);
        }

        .search-box {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .search-input {
            padding: 8px 14px;
            border: 1px solid var(--border);
            border-radius: 6px;
            font-size: 0.88rem;
            width: 260px;
            outline: none;
            transition: border-color 0.2s;
        }

        .search-input:focus {
            border-color: var(--accent);
        }

        /* Data Table */
        .table-responsive {
            width: 100%;
            overflow-x: auto;
        }

        .data-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.88rem;
            text-align: left;
        }

        .data-table th {
            background-color: #f1f5f9;
            color: #334155;
            font-weight: 600;
            padding: 12px 16px;
            border-bottom: 1px solid var(--border);
            white-space: nowrap;
        }

        .data-table td {
            padding: 12px 16px;
            border-bottom: 1px solid var(--border);
            color: var(--text-dark);
            white-space: nowrap;
        }

        .data-table tbody tr:hover {
            background-color: #f8fafc;
        }

        .role-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
        }

        .role-student {
            background-color: #dbeafe;
            color: #1e40af;
        }

        .role-admin {
            background-color: #fef3c7;
            color: #92400e;
        }

        .status-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 600;
            background-color: #dcfce7;
            color: #166534;
        }

        /* Modal Popup */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(15, 23, 42, 0.6);
            backdrop-filter: blur(4px);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 9999;
            padding: 16px;
        }

        .modal-card {
            background: #ffffff;
            border-radius: 10px;
            max-width: 440px;
            width: 100%;
            padding: 28px 24px;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.2);
            text-align: center;
            border-top: 4px solid var(--success);
        }

        .modal-title {
            font-size: 1.25rem;
            font-weight: 700;
            color: var(--primary);
            margin-bottom: 10px;
        }

        .modal-body {
            font-size: 0.92rem;
            color: var(--text-muted);
            margin-bottom: 22px;
        }

        .btn-confirm {
            background-color: var(--accent);
            color: #ffffff;
            border: none;
            padding: 9px 22px;
            border-radius: 6px;
            font-size: 0.9rem;
            font-weight: 600;
            cursor: pointer;
        }
    </style>
</head>
<body>

    <!-- Top Navigation -->
    <header class="topbar">
        <a href="${pageContext.request.contextPath}/admin" class="topbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Kasnet Logo" style="height: 36px; width: auto; object-fit: contain; border-radius: 6px; background: #ffffff; padding: 2px;" />
            <div class="brand-text">
                <h1>Kasnet Internship Administration</h1>
                <span>Monitoring Console</span>
            </div>
        </a>

        <div class="topbar-nav">
            <span class="admin-badge">Admin: ${sessionScope.userName}</span>
            <a href="${pageContext.request.contextPath}/" class="admin-badge" style="text-decoration:none;">Public Site</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </header>

    <!-- Main Content -->
    <main class="container">
        <div class="page-header">
            <div>
                <h2>User &amp; Registration Monitoring</h2>
                <p>View registered student accounts and registration timestamps.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin" style="font-size:0.88rem; font-weight:600; color:var(--accent);">↻ Refresh Table</a>
            </div>
        </div>

        <!-- Metric Summary Cards -->
        <div class="metrics-grid">
            <div class="metric-card">
                <div class="metric-label">Total Accounts</div>
                <div class="metric-value">${totalUsers}</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">Registered Students</div>
                <div class="metric-value">${studentCount}</div>
            </div>
            <div class="metric-card">
                <div class="metric-label">System Administrators</div>
                <div class="metric-value">${adminCount}</div>
            </div>
        </div>

        <!-- Registered Users Table -->
        <div class="card">
            <div class="card-header">
                <h3>Registered User Directory</h3>
                <div class="search-box">
                    <input type="text" id="userSearch" class="search-input" placeholder="Search by name, email, college..." onkeyup="filterUsers()">
                </div>
            </div>

            <div class="table-responsive">
                <table class="data-table" id="usersTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Full Name</th>
                            <th>Email Address</th>
                            <th>Mobile</th>
                            <th>Role</th>
                            <th>Stream</th>
                            <th>College Name</th>
                            <th>City</th>
                            <th>Registration Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty userList}">
                                <c:forEach items="${userList}" var="u">
                                    <tr>
                                        <td><strong>#${u.userId}</strong></td>
                                        <td>${u.firstName} ${u.lastName}</td>
                                        <td>${u.email}</td>
                                        <td>${u.countryCode} ${u.mobile}</td>
                                        <td>
                                            <span class="role-badge ${'ADMIN' == u.role ? 'role-admin' : 'role-student'}">
                                                ${u.role}
                                            </span>
                                        </td>
                                        <td>${u.stream != null ? u.stream : '-'}</td>
                                        <td>${u.collegeName != null && not empty u.collegeName ? u.collegeName : '-'}</td>
                                        <td>${u.city != null && not empty u.city ? u.city : '-'}</td>
                                        <td><fmt:formatDate value="${u.createdAt}" pattern="yyyy-MM-dd HH:mm" /></td>
                                        <td><span class="status-badge">Active</span></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="10" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                        No registered users found in the database.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <!-- Login Success Popup Modal -->
    <% if (isLoginSuccess) { %>
    <div class="modal-overlay" id="loginSuccessModal">
        <div class="modal-card">
            <h3 class="modal-title">Login successful</h3>
            <p class="modal-body">Welcome back to the Admin Console, <%= session.getAttribute("userName") != null ? session.getAttribute("userName") : "Administrator" %>.</p>
            <button type="button" class="btn-confirm" onclick="closeLoginModal()">Continue</button>
        </div>
    </div>
    <script>
        function closeLoginModal() {
            var m = document.getElementById('loginSuccessModal');
            if (m) m.style.display = 'none';
            if (window.history && window.history.replaceState) {
                var cleanUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
                window.history.replaceState({}, document.title, cleanUrl);
            }
        }
    </script>
    <% } %>

    <script>
        function filterUsers() {
            var input = document.getElementById("userSearch");
            var filter = input.value.toLowerCase();
            var table = document.getElementById("usersTable");
            var trs = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

            for (var i = 0; i < trs.length; i++) {
                var rowText = trs[i].textContent || trs[i].innerText;
                if (rowText.toLowerCase().indexOf(filter) > -1) {
                    trs[i].style.display = "";
                } else {
                    trs[i].style.display = "none";
                }
            }
        }
    </script>

</body>
</html>
