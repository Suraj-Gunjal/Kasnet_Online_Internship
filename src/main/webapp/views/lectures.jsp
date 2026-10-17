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
    <title>Lecture Videos - My Internship Technologies Portal</title>
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
            margin-bottom: 24px;
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

        /* Lectures Grid */
        .lectures-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
            gap: 24px;
        }

        .lecture-card {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .lecture-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 24px rgba(0,0,0,0.08);
        }

        .video-placeholder {
            width: 100%;
            height: 180px;
            background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #ffffff;
            position: relative;
            cursor: pointer;
        }

        .play-btn {
            width: 54px;
            height: 54px;
            background: rgba(255,255,255,0.25);
            border: 2px solid #ffffff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            backdrop-filter: blur(4px);
            transition: transform 0.2s, background 0.2s;
        }

        .video-placeholder:hover .play-btn {
            transform: scale(1.1);
            background: rgba(239, 68, 68, 0.85);
        }

        .duration-tag {
            position: absolute;
            bottom: 12px;
            right: 12px;
            background: rgba(0,0,0,0.75);
            color: #fff;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 0.75rem;
            font-weight: 600;
        }

        .lecture-body {
            padding: 20px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .lecture-num {
            font-size: 0.75rem;
            font-weight: 700;
            color: #4f46e5;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 4px;
        }

        .lecture-title {
            font-size: 1.15rem;
            font-weight: 700;
            color: #111827;
            margin-bottom: 6px;
        }

        .lecture-subject {
            font-size: 0.85rem;
            color: #6b7280;
            margin-bottom: 10px;
        }

        .lecture-desc {
            font-size: 0.85rem;
            color: #4b5563;
            line-height: 1.5;
            margin-bottom: 16px;
        }

        .lecture-footer {
            border-top: 1px solid #f3f4f6;
            padding-top: 14px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .instructor-info {
            font-size: 0.8rem;
            color: #4b5563;
        }

        .completed-badge {
            background: #dcfce7;
            color: #15803d;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 700;
        }

        .btn-mark-complete {
            background: #48bb78;
            color: #ffffff;
            border: none;
            padding: 6px 12px;
            border-radius: 6px;
            font-size: 0.8rem;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }

        .btn-mark-complete:hover {
            background: #38a169;
        }

        /* Modal for Watching Video */
        .modal-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.7);
            z-index: 1000;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .modal-card {
            background: #ffffff;
            border-radius: 14px;
            width: 100%;
            max-width: 800px;
            overflow: hidden;
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
        }

        .modal-header {
            padding: 16px 20px;
            background: #1a237e;
            color: #ffffff;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .modal-title {
            font-size: 1.1rem;
            font-weight: 700;
        }

        .modal-close {
            background: none;
            border: none;
            color: #ffffff;
            font-size: 1.5rem;
            cursor: pointer;
        }

        .video-container {
            position: relative;
            padding-bottom: 56.25%; /* 16:9 */
            height: 0;
            background: #000;
        }

        .video-container iframe {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            border: none;
        }
    </style>
</head>
<body>

    <!-- Navbar -->
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/home" class="navbar-brand">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="My Internship Technologies Logo" style="height: 32px; width: auto; object-fit: contain; margin-right: 10px; border-radius: 4px; background: #ffffff; padding: 2px;" />
            <span>My Internship Technologies</span>
        </a>
        <div class="navbar-menu">
            <a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a>
            <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">Dashboard</a>
            <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
            <a href="${pageContext.request.contextPath}/assignments" class="nav-link">Assignments</a>
            <a href="${pageContext.request.contextPath}/lectures" class="nav-link active">Lectures</a>
            <a href="${pageContext.request.contextPath}/question-bank" class="nav-link">Question Bank</a>
            <a href="${pageContext.request.contextPath}/resume-analysis" class="nav-link">Resume Analysis</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn-logout">Logout</a>
        </div>
    </nav>

    <!-- Main Container -->
    <div class="container">
        <div class="page-header">
            <h1 class="page-title">Curated Internship Lectures</h1>
            <p class="page-subtitle">Watch domain lectures delivered by industry experts and track your completion.</p>
        </div>

        <c:if test="${param.success == '1'}">
            <div class="alert-success">
                ✓ Lecture marked as completed!
            </div>
        </c:if>

        <div class="lectures-grid">
            <c:forEach items="${lectureList}" var="lec">
                <div class="lecture-card">
                    <div class="video-placeholder" onclick="openLecture('${lec.title}', '${lec.videoUrl}')">
                        <div class="play-btn">▶</div>
                        <span class="duration-tag">${lec.durationMinutes} mins</span>
                    </div>

                    <div class="lecture-body">
                        <div>
                            <div class="lecture-num">Lecture #${lec.lectureNumber}</div>
                            <h2 class="lecture-title">${lec.title}</h2>
                            <div class="lecture-subject">${lec.subject}</div>
                            <p class="lecture-desc">${lec.description}</p>
                        </div>

                        <div class="lecture-footer">
                            <span class="instructor-info">Instructor: ${lec.instructor}</span>
                            <c:choose>
                                <c:when test="${lec.completed}">
                                    <span class="completed-badge">✓ Completed</span>
                                </c:when>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/lectures" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="markComplete">
                                        <input type="hidden" name="lectureId" value="${lec.lectureId}">
                                        <button type="submit" class="btn-mark-complete">Mark as Done</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- Video Modal Player -->
    <div id="videoModal" class="modal-overlay">
        <div class="modal-card">
            <div class="modal-header">
                <span id="modalVideoTitle" class="modal-title">Lecture Video</span>
                <button type="button" class="modal-close" onclick="closeLecture()">&times;</button>
            </div>
            <div class="video-container">
                <iframe id="videoIframe" src="" allowfullscreen allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"></iframe>
            </div>
        </div>
    </div>

    <script>
        function openLecture(title, videoUrl) {
            document.getElementById('modalVideoTitle').textContent = title;
            var iframe = document.getElementById('videoIframe');
            if (videoUrl && videoUrl.indexOf('youtube.com/watch?v=') !== -1) {
                var videoId = videoUrl.split('v=')[1];
                var ampersandPosition = videoId.indexOf('&');
                if (ampersandPosition !== -1) {
                    videoId = videoId.substring(0, ampersandPosition);
                }
                iframe.src = 'https://www.youtube.com/embed/' + videoId + '?autoplay=1';
            } else if (videoUrl) {
                iframe.src = videoUrl;
            } else {
                iframe.src = 'https://www.youtube.com/embed/dQw4w9WgXcQ?autoplay=1';
            }
            document.getElementById('videoModal').style.display = 'flex';
        }

        function closeLecture() {
            var iframe = document.getElementById('videoIframe');
            iframe.src = '';
            document.getElementById('videoModal').style.display = 'none';
        }

        window.onclick = function(event) {
            var modal = document.getElementById('videoModal');
            if (event.target == modal) {
                closeLecture();
            }
        }
    </script>

</body>
</html>
