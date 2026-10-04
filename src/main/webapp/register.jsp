<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account &mdash; Kasnet Online Internship Portal</title>
    <style>
        /* ===== RESET & BASE ===== */
        *, *::before, *::after {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        html {
            scroll-behavior: smooth;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f0f2f5;
            min-height: 100vh;
            display: flex;
            align-items: stretch;
            color: #1a1a2e;
        }

        /* ===== LAYOUT ===== */
        .page-wrapper {
            display: flex;
            width: 100%;
            min-height: 100vh;
        }

        /* ===== LEFT PANEL ===== */
        .left-panel {
            width: 38%;
            background: linear-gradient(160deg, #0d1b5e 0%, #1a237e 40%, #283593 70%, #1565c0 100%);
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: flex-start;
            padding: 60px 48px;
            position: sticky;
            top: 0;
            height: 100vh;
            overflow: hidden;
        }

        .left-panel::before {
            content: '';
            position: absolute;
            top: -80px;
            right: -80px;
            width: 320px;
            height: 320px;
            background: rgba(255,255,255,0.04);
            border-radius: 50%;
        }

        .left-panel::after {
            content: '';
            position: absolute;
            bottom: -100px;
            left: -60px;
            width: 400px;
            height: 400px;
            background: rgba(255,255,255,0.03);
            border-radius: 50%;
        }

        .brand-logo {
            display: flex;
            align-items: center;
            gap: 14px;
            margin-bottom: 48px;
            position: relative;
            z-index: 1;
        }

        .brand-logo .logo-icon {
            width: 52px;
            height: 52px;
            background: linear-gradient(135deg, #42a5f5, #1e88e5);
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 26px;
            box-shadow: 0 6px 20px rgba(66,165,245,0.4);
        }

        .brand-logo .logo-text {
            display: flex;
            flex-direction: column;
        }

        .brand-logo .logo-name {
            font-size: 1.5rem;
            font-weight: 800;
            color: #ffffff;
            letter-spacing: 0.5px;
            line-height: 1.1;
        }

        .brand-logo .logo-tagline {
            font-size: 0.72rem;
            color: rgba(255,255,255,0.6);
            letter-spacing: 1.5px;
            text-transform: uppercase;
            font-weight: 500;
        }

        .hero-headline {
            font-size: 2rem;
            font-weight: 800;
            color: #ffffff;
            line-height: 1.25;
            margin-bottom: 16px;
            position: relative;
            z-index: 1;
        }

        .hero-headline span {
            color: #64b5f6;
        }

        .hero-sub {
            font-size: 0.95rem;
            color: rgba(255,255,255,0.72);
            line-height: 1.7;
            margin-bottom: 40px;
            position: relative;
            z-index: 1;
        }

        .stats-row {
            display: flex;
            gap: 28px;
            margin-bottom: 40px;
            position: relative;
            z-index: 1;
        }

        .stat-item {
            display: flex;
            flex-direction: column;
        }

        .stat-number {
            font-size: 1.6rem;
            font-weight: 800;
            color: #64b5f6;
            line-height: 1;
        }

        .stat-label {
            font-size: 0.72rem;
            color: rgba(255,255,255,0.55);
            text-transform: uppercase;
            letter-spacing: 0.8px;
            margin-top: 3px;
        }

        .benefits-list {
            list-style: none;
            display: flex;
            flex-direction: column;
            gap: 16px;
            position: relative;
            z-index: 1;
        }

        .benefits-list li {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            color: rgba(255,255,255,0.85);
            font-size: 0.88rem;
            line-height: 1.5;
        }

        .benefit-icon {
            width: 28px;
            height: 28px;
            background: rgba(100,181,246,0.18);
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            flex-shrink: 0;
            border: 1px solid rgba(100,181,246,0.25);
        }

        .badge-trusted {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.15);
            border-radius: 50px;
            padding: 8px 16px;
            margin-top: 36px;
            position: relative;
            z-index: 1;
        }

        .badge-dot {
            width: 8px;
            height: 8px;
            background: #4caf50;
            border-radius: 50%;
            animation: pulse-dot 2s infinite;
        }

        @keyframes pulse-dot {
            0%, 100% { opacity: 1; transform: scale(1); }
            50% { opacity: 0.6; transform: scale(0.8); }
        }

        .badge-trusted span {
            font-size: 0.78rem;
            color: rgba(255,255,255,0.75);
            letter-spacing: 0.3px;
        }

        /* ===== RIGHT PANEL ===== */
        .right-panel {
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: flex-start;
            padding: 40px 32px;
            overflow-y: auto;
            background: #f0f2f5;
        }

        .form-container {
            width: 100%;
            max-width: 720px;
        }

        .form-header {
            text-align: center;
            margin-bottom: 28px;
        }

        .form-header .step-badge {
            display: inline-block;
            background: #e8eaf6;
            color: #3949ab;
            font-size: 0.72rem;
            font-weight: 700;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            padding: 5px 14px;
            border-radius: 20px;
            margin-bottom: 10px;
        }

        .form-header h1 {
            font-size: 1.85rem;
            font-weight: 800;
            color: #0d1b5e;
            line-height: 1.2;
            margin-bottom: 6px;
        }

        .form-header p {
            color: #6b7280;
            font-size: 0.9rem;
        }

        /* ===== ALERT MESSAGES ===== */
        .alert {
            border-radius: 10px;
            padding: 14px 18px;
            margin-bottom: 20px;
            font-size: 0.875rem;
            display: flex;
            align-items: flex-start;
            gap: 10px;
            border-left: 4px solid;
        }

        .alert-error {
            background: #fef2f2;
            color: #991b1b;
            border-color: #ef4444;
        }

        .alert-success {
            background: #f0fdf4;
            color: #166534;
            border-color: #22c55e;
        }

        .alert-icon {
            font-size: 1rem;
            flex-shrink: 0;
            margin-top: 1px;
        }

        /* ===== CARD ===== */
        .form-card {
            background: #ffffff;
            border-radius: 20px;
            padding: 36px 40px;
            box-shadow: 0 4px 24px rgba(26,35,126,0.08), 0 1px 4px rgba(26,35,126,0.05);
            border: 1px solid rgba(26,35,126,0.06);
        }

        /* ===== FORM GRID ===== */
        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 18px 24px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
        }

        label {
            font-size: 0.8rem;
            font-weight: 600;
            color: #374151;
            letter-spacing: 0.3px;
        }

        label .required-star {
            color: #ef4444;
            margin-left: 2px;
        }

        label .optional-tag {
            color: #9ca3af;
            font-weight: 400;
            font-size: 0.72rem;
            margin-left: 4px;
        }

        /* ===== INPUTS ===== */
        .input-wrapper {
            position: relative;
            display: flex;
            align-items: center;
        }

        .input-icon {
            position: absolute;
            left: 12px;
            font-size: 15px;
            color: #9ca3af;
            pointer-events: none;
            z-index: 1;
        }

        input[type="text"],
        input[type="email"],
        input[type="password"],
        input[type="tel"],
        select {
            width: 100%;
            padding: 11px 14px 11px 38px;
            border: 1.5px solid #e5e7eb;
            border-radius: 10px;
            font-size: 0.88rem;
            color: #1a1a2e;
            background: #fafafa;
            transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;
            outline: none;
            font-family: inherit;
            -webkit-appearance: none;
            appearance: none;
        }

        input[type="text"]:focus,
        input[type="email"]:focus,
        input[type="password"]:focus,
        input[type="tel"]:focus,
        select:focus {
            border-color: #3949ab;
            background: #ffffff;
            box-shadow: 0 0 0 3px rgba(57,73,171,0.12);
        }

        input[type="text"].input-error,
        input[type="email"].input-error,
        input[type="password"].input-error,
        input[type="tel"].input-error,
        select.input-error {
            border-color: #ef4444;
            background: #fff5f5;
        }

        input[type="text"].input-success,
        input[type="email"].input-success,
        input[type="password"].input-success,
        input[type="tel"].input-success {
            border-color: #22c55e;
        }

        /* Select arrow */
        select {
            cursor: pointer;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%239ca3af' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 12px center;
            padding-right: 32px;
        }

        /* No icon for select */
        .select-wrapper select {
            padding-left: 38px;
        }

        /* ===== COUNTRY CODE + MOBILE ROW ===== */
        .phone-group {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
        }

        .phone-group.full-width {
            grid-column: 1 / -1;
        }

        /* ===== PASSWORD TOGGLE ===== */
        .toggle-pass {
            position: absolute;
            right: 12px;
            background: none;
            border: none;
            cursor: pointer;
            font-size: 15px;
            color: #9ca3af;
            padding: 4px;
            transition: color 0.2s;
            z-index: 1;
        }

        .toggle-pass:hover {
            color: #3949ab;
        }

        /* ===== PASSWORD STRENGTH ===== */
        .password-strength {
            margin-top: 6px;
        }

        .strength-bar-track {
            height: 4px;
            background: #e5e7eb;
            border-radius: 4px;
            overflow: hidden;
        }

        .strength-bar-fill {
            height: 100%;
            border-radius: 4px;
            width: 0%;
            transition: width 0.35s ease, background-color 0.35s ease;
        }

        .strength-text {
            font-size: 0.72rem;
            font-weight: 600;
            margin-top: 4px;
            height: 16px;
            color: #9ca3af;
        }

        /* ===== MATCH INDICATOR ===== */
        .match-msg {
            font-size: 0.72rem;
            font-weight: 600;
            margin-top: 4px;
            min-height: 16px;
        }

        .match-ok { color: #22c55e; }
        .match-fail { color: #ef4444; }

        /* ===== FIELD ERROR ===== */
        .field-error {
            font-size: 0.72rem;
            color: #ef4444;
            font-weight: 500;
            min-height: 14px;
            display: none;
        }

        .field-error.visible {
            display: block;
        }

        /* ===== CAPTCHA ===== */
        .captcha-display {
            background: linear-gradient(135deg, #e8eaf6 0%, #c5cae9 100%);
            border: 2px dashed #7986cb;
            border-radius: 10px;
            padding: 14px 18px;
            text-align: center;
            position: relative;
            overflow: hidden;
            min-height: 60px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .captcha-display::before {
            content: '';
            position: absolute;
            inset: 0;
            background-image: repeating-linear-gradient(
                45deg,
                transparent,
                transparent 8px,
                rgba(121,134,203,0.12) 8px,
                rgba(121,134,203,0.12) 9px
            );
        }

        .captcha-text {
            font-size: 1.6rem;
            font-weight: 900;
            letter-spacing: 10px;
            color: #1a237e;
            font-family: 'Courier New', monospace;
            text-shadow: 1px 2px 0 rgba(255,255,255,0.6), -1px -1px 0 rgba(0,0,0,0.1);
            position: relative;
            z-index: 1;
            user-select: none;
            -webkit-user-select: none;
        }

        .captcha-refresh {
            position: absolute;
            top: 6px;
            right: 8px;
            background: none;
            border: none;
            cursor: pointer;
            font-size: 14px;
            color: #5c6bc0;
            z-index: 2;
            padding: 2px;
            transition: transform 0.3s;
        }

        .captcha-refresh:hover {
            transform: rotate(180deg);
            color: #3949ab;
        }

        /* ===== DIVIDER ===== */
        .section-divider {
            grid-column: 1 / -1;
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 4px 0;
        }

        .section-divider::before,
        .section-divider::after {
            content: '';
            flex: 1;
            height: 1px;
            background: #e5e7eb;
        }

        .section-divider span {
            font-size: 0.72rem;
            font-weight: 700;
            color: #9ca3af;
            letter-spacing: 1px;
            text-transform: uppercase;
            white-space: nowrap;
        }

        /* ===== SUBMIT BUTTON ===== */
        .btn-submit {
            grid-column: 1 / -1;
            width: 100%;
            padding: 14px 20px;
            background: linear-gradient(135deg, #1a237e 0%, #3949ab 100%);
            color: #ffffff;
            border: none;
            border-radius: 12px;
            font-size: 0.95rem;
            font-weight: 700;
            cursor: pointer;
            letter-spacing: 0.5px;
            transition: all 0.25s;
            position: relative;
            overflow: hidden;
            margin-top: 6px;
            box-shadow: 0 4px 16px rgba(26,35,126,0.35);
        }

        .btn-submit::before {
            content: '';
            position: absolute;
            top: 0; left: -100%;
            width: 100%; height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.12), transparent);
            transition: left 0.5s;
        }

        .btn-submit:hover::before {
            left: 100%;
        }

        .btn-submit:hover {
            background: linear-gradient(135deg, #0d1b5e 0%, #1a237e 100%);
            box-shadow: 0 6px 22px rgba(26,35,126,0.5);
            transform: translateY(-1px);
        }

        .btn-submit:active {
            transform: translateY(0);
        }

        .btn-submit:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .btn-submit .btn-spinner {
            display: none;
            width: 18px;
            height: 18px;
            border: 2px solid rgba(255,255,255,0.4);
            border-top-color: #fff;
            border-radius: 50%;
            animation: spin 0.7s linear infinite;
            margin-right: 8px;
            vertical-align: middle;
        }

        @keyframes spin {
            to { transform: rotate(360deg); }
        }

        /* ===== SIGN IN LINK ===== */
        .signin-link {
            grid-column: 1 / -1;
            text-align: center;
            margin-top: 4px;
            font-size: 0.875rem;
            color: #6b7280;
        }

        .signin-link a {
            color: #3949ab;
            font-weight: 700;
            text-decoration: none;
            transition: color 0.2s;
        }

        .signin-link a:hover {
            color: #1a237e;
            text-decoration: underline;
        }

        /* ===== TERMS ===== */
        .terms-note {
            grid-column: 1 / -1;
            font-size: 0.72rem;
            color: #9ca3af;
            text-align: center;
            line-height: 1.6;
        }

        .terms-note a {
            color: #3949ab;
            text-decoration: none;
        }

        .terms-note a:hover {
            text-decoration: underline;
        }

        /* ===== FOOTER ===== */
        .page-footer {
            text-align: center;
            margin-top: 20px;
            font-size: 0.75rem;
            color: #9ca3af;
        }

        /* ===== RESPONSIVE ===== */
        @media (max-width: 900px) {
            .left-panel {
                display: none;
            }

            .right-panel {
                padding: 28px 20px;
            }

            .form-card {
                padding: 28px 22px;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }

            .form-group.full-width {
                grid-column: 1;
            }

            .btn-submit {
                grid-column: 1;
            }

            .signin-link,
            .terms-note,
            .section-divider {
                grid-column: 1;
            }

            .phone-group {
                grid-column: 1;
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 480px) {
            .form-header h1 {
                font-size: 1.5rem;
            }

            .form-card {
                padding: 22px 16px;
                border-radius: 14px;
            }
        }
    </style>
</head>
<body>

<div class="page-wrapper">

    <!-- ===== LEFT BRANDING PANEL ===== -->
    <aside class="left-panel">
        <!-- Logo -->
        <div class="brand-logo">
            <div class="logo-icon">🚀</div>
            <div class="logo-text">
                <span class="logo-name">Kasnet</span>
                <span class="logo-tagline">Internship Portal</span>
            </div>
        </div>

        <!-- Headline -->
        <h2 class="hero-headline">
            Join Thousands of<br><span>Aspiring Students</span><br>Nationwide
        </h2>
        <p class="hero-sub">
            Build your career with real-world internship experience,<br>
            expert mentorship, and industry-recognized certificates.
        </p>

        <!-- Stats -->
        <div class="stats-row">
            <div class="stat-item">
                <span class="stat-number">12K+</span>
                <span class="stat-label">Students</span>
            </div>
            <div class="stat-item">
                <span class="stat-number">350+</span>
                <span class="stat-label">Companies</span>
            </div>
            <div class="stat-item">
                <span class="stat-number">98%</span>
                <span class="stat-label">Placement</span>
            </div>
        </div>

        <!-- Benefits -->
        <ul class="benefits-list">
            <li>
                <div class="benefit-icon">🎓</div>
                <div>Access curated internships across 25+ domains &amp; streams</div>
            </li>
            <li>
                <div class="benefit-icon">🏆</div>
                <div>Industry-recognized digital certificates upon completion</div>
            </li>
            <li>
                <div class="benefit-icon">👨‍💼</div>
                <div>One-on-one mentorship from seasoned professionals</div>
            </li>
            <li>
                <div class="benefit-icon">💼</div>
                <div>Pre-placement offers &amp; direct recruiter connections</div>
            </li>
        </ul>

        <div class="badge-trusted">
            <div class="badge-dot"></div>
            <span>Trusted by students from 500+ colleges across India</span>
        </div>
    </aside>

    <!-- ===== RIGHT FORM PANEL ===== -->
    <main class="right-panel">
        <div class="form-container">

            <!-- Header -->
            <div class="form-header">
                <div class="step-badge">&#128640; Free Registration</div>
                <h1>Create Your Account</h1>
                <p>Start your internship journey today &mdash; it only takes 2 minutes</p>
            </div>

            <!-- Server-side messages -->
            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-error" id="serverAlert">
                    <span class="alert-icon">&#10060;</span>
                    <div>${requestScope.error}</div>
                </div>
            </c:if>
            <c:if test="${not empty requestScope.success}">
                <div class="alert alert-success" id="serverSuccess">
                    <span class="alert-icon">&#10003;</span>
                    <div>${requestScope.success}</div>
                </div>
            </c:if>

            <!-- Client-side validation alert -->
            <div class="alert alert-error" id="clientAlert" style="display:none;">
                <span class="alert-icon">&#10060;</span>
                <div id="clientAlertMsg"></div>
            </div>

            <!-- FORM CARD -->
            <div class="form-card">
                <form id="registerForm"
                      action="${pageContext.request.contextPath}/register"
                      method="POST"
                      novalidate
                      autocomplete="off">

                    <div class="form-grid">

                        <!-- ====== PERSONAL INFO ====== -->
                        <div class="section-divider"><span>Personal Information</span></div>

                        <!-- First Name -->
                        <div class="form-group">
                            <label for="firstName">First Name <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#128100;</span>
                                <input type="text"
                                       id="firstName"
                                       name="firstName"
                                       placeholder="e.g. Rahul"
                                       value="${not empty requestScope.firstName ? requestScope.firstName : ''}"
                                       maxlength="50"
                                       required>
                            </div>
                            <span class="field-error" id="firstNameError">First name is required.</span>
                        </div>

                        <!-- Last Name -->
                        <div class="form-group">
                            <label for="lastName">Last Name <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#128100;</span>
                                <input type="text"
                                       id="lastName"
                                       name="lastName"
                                       placeholder="e.g. Sharma"
                                       value="${not empty requestScope.lastName ? requestScope.lastName : ''}"
                                       maxlength="50"
                                       required>
                            </div>
                            <span class="field-error" id="lastNameError">Last name is required.</span>
                        </div>

                        <!-- Country Code -->
                        <div class="form-group">
                            <label for="countryCode">Country Code <span class="required-star">*</span></label>
                            <div class="input-wrapper select-wrapper">
                                <span class="input-icon">&#127758;</span>
                                <select id="countryCode" name="countryCode" required>
                                    <option value="+91" ${requestScope.countryCode == '+91' || empty requestScope.countryCode ? 'selected' : ''}>&#127470;&#127475; +91 &mdash; India</option>
                                    <option value="+1"  ${requestScope.countryCode == '+1'  ? 'selected' : ''}>&#127482;&#127480; +1 &mdash; USA</option>
                                    <option value="+44" ${requestScope.countryCode == '+44' ? 'selected' : ''}>&#127468;&#127463; +44 &mdash; UK</option>
                                    <option value="+61" ${requestScope.countryCode == '+61' ? 'selected' : ''}>&#127462;&#127482; +61 &mdash; Australia</option>
                                </select>
                            </div>
                        </div>

                        <!-- Mobile Number -->
                        <div class="form-group">
                            <label for="mobile">Mobile Number <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#128222;</span>
                                <input type="tel"
                                       id="mobile"
                                       name="mobile"
                                       placeholder="10-digit number"
                                       value="${not empty requestScope.mobile ? requestScope.mobile : ''}"
                                       maxlength="10"
                                       pattern="[0-9]{10}"
                                       required>
                            </div>
                            <span class="field-error" id="mobileError">Enter a valid 10-digit mobile number.</span>
                        </div>

                        <!-- Email -->
                        <div class="form-group full-width">
                            <label for="email">Email Address <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#9993;</span>
                                <input type="email"
                                       id="email"
                                       name="email"
                                       placeholder="you@example.com"
                                       value="${not empty requestScope.email ? requestScope.email : ''}"
                                       maxlength="100"
                                       required>
                            </div>
                            <span class="field-error" id="emailError">Enter a valid email address.</span>
                        </div>

                        <!-- ====== SECURITY ====== -->
                        <div class="section-divider"><span>Security</span></div>

                        <!-- Password -->
                        <div class="form-group">
                            <label for="password">Password <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#128274;</span>
                                <input type="password"
                                       id="password"
                                       name="password"
                                       placeholder="Min 8 characters"
                                       maxlength="64"
                                       autocomplete="new-password"
                                       required>
                                <button type="button" class="toggle-pass" onclick="togglePass('password','eyePass')" title="Show/hide password">
                                    <span id="eyePass">&#128065;</span>
                                </button>
                            </div>
                            <div class="password-strength">
                                <div class="strength-bar-track">
                                    <div class="strength-bar-fill" id="strengthBar"></div>
                                </div>
                                <div class="strength-text" id="strengthText"></div>
                            </div>
                            <span class="field-error" id="passwordError">Password must be at least 8 characters.</span>
                        </div>

                        <!-- Confirm Password -->
                        <div class="form-group">
                            <label for="confirmPassword">Confirm Password <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#128275;</span>
                                <input type="password"
                                       id="confirmPassword"
                                       name="confirmPassword"
                                       placeholder="Re-enter password"
                                       maxlength="64"
                                       autocomplete="new-password"
                                       required>
                                <button type="button" class="toggle-pass" onclick="togglePass('confirmPassword','eyeConfirm')" title="Show/hide password">
                                    <span id="eyeConfirm">&#128065;</span>
                                </button>
                            </div>
                            <div class="match-msg" id="matchMsg"></div>
                            <span class="field-error" id="confirmPasswordError">Passwords do not match.</span>
                        </div>

                        <!-- ====== ACADEMIC INFO ====== -->
                        <div class="section-divider"><span>Academic Information</span></div>

                        <!-- Role -->
                        <div class="form-group">
                            <label for="role">Select Role <span class="required-star">*</span></label>
                            <div class="input-wrapper select-wrapper">
                                <span class="input-icon">&#127891;</span>
                                <select id="role" name="role" required>
                                    <option value="" disabled ${empty requestScope.role ? 'selected' : ''}>-- Select Role --</option>
                                    <option value="STUDENT" ${requestScope.role == 'STUDENT' ? 'selected' : ''}>&#128100; Student</option>
                                    <option value="ADMIN"   ${requestScope.role == 'ADMIN'   ? 'selected' : ''}>&#128737; Admin</option>
                                </select>
                            </div>
                            <span class="field-error" id="roleError">Please select a role.</span>
                        </div>

                        <!-- Stream -->
                        <div class="form-group">
                            <label for="stream">Stream <span class="required-star">*</span></label>
                            <div class="input-wrapper select-wrapper">
                                <span class="input-icon">&#128218;</span>
                                <select id="stream" name="stream" required>
                                    <option value="" disabled ${empty requestScope.stream ? 'selected' : ''}>-- Select Stream --</option>
                                    <option value="Graduation"      ${requestScope.stream == 'Graduation'      ? 'selected' : ''}>&#127979; Graduation</option>
                                    <option value="Post Graduation" ${requestScope.stream == 'Post Graduation' ? 'selected' : ''}>&#127981; Post Graduation</option>
                                    <option value="Diploma"         ${requestScope.stream == 'Diploma'         ? 'selected' : ''}>&#128196; Diploma</option>
                                </select>
                            </div>
                            <span class="field-error" id="streamError">Please select your stream.</span>
                        </div>

                        <!-- College Name -->
                        <div class="form-group">
                            <label for="collegeName">College Name <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#127963;</span>
                                <input type="text"
                                       id="collegeName"
                                       name="collegeName"
                                       placeholder="e.g. Delhi University"
                                       value="${not empty requestScope.collegeName ? requestScope.collegeName : ''}"
                                       maxlength="120"
                                       required>
                            </div>
                            <span class="field-error" id="collegeNameError">College name is required.</span>
                        </div>

                        <!-- City -->
                        <div class="form-group">
                            <label for="city">City <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#127961;</span>
                                <input type="text"
                                       id="city"
                                       name="city"
                                       placeholder="e.g. Mumbai"
                                       value="${not empty requestScope.city ? requestScope.city : ''}"
                                       maxlength="80"
                                       required>
                            </div>
                            <span class="field-error" id="cityError">City is required.</span>
                        </div>

                        <!-- ====== EXTRAS ====== -->
                        <div class="section-divider"><span>Additional Details</span></div>

                        <!-- Referral Code -->
                        <div class="form-group full-width">
                            <label for="referralCode">Referral Code <span class="optional-tag">(optional)</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#127381;</span>
                                <input type="text"
                                       id="referralCode"
                                       name="referralCode"
                                       placeholder="Enter referral code if you have one"
                                       value="${not empty requestScope.referralCode ? requestScope.referralCode : ''}"
                                       maxlength="20">
                            </div>
                        </div>

                        <!-- ====== CAPTCHA ====== -->
                        <div class="section-divider"><span>Verification</span></div>

                        <!-- CAPTCHA Display -->
                        <div class="form-group">
                            <label>CAPTCHA Code</label>
                            <div class="captcha-display" id="captchaBox" aria-label="CAPTCHA code display">
                                <%-- Show server-side captcha if available, else generate client-side --%>
                                <c:choose>
                                    <c:when test="${not empty sessionScope.regCaptcha}">
                                        <span class="captcha-text" id="captchaText">${sessionScope.regCaptcha}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="captcha-text" id="captchaText"></span>
                                    </c:otherwise>
                                </c:choose>
                                <button type="button" class="captcha-refresh" onclick="refreshCaptcha()" title="Refresh CAPTCHA">&#128260;</button>
                            </div>
                        </div>

                        <!-- CAPTCHA Input -->
                        <div class="form-group">
                            <label for="captchaInput">Enter CAPTCHA <span class="required-star">*</span></label>
                            <div class="input-wrapper">
                                <span class="input-icon">&#128737;</span>
                                <input type="text"
                                       id="captchaInput"
                                       name="captchaInput"
                                       placeholder="Type the code above"
                                       maxlength="6"
                                       autocomplete="off"
                                       required>
                            </div>
                            <span class="field-error" id="captchaError">Please enter the CAPTCHA code.</span>
                        </div>

                        <!-- ====== TERMS NOTE ====== -->
                        <p class="terms-note">
                            By creating an account, you agree to our
                            <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.
                            Your data is safe with us.
                        </p>

                        <!-- ====== SUBMIT ====== -->
                        <button type="submit" class="btn-submit" id="submitBtn">
                            <span class="btn-spinner" id="btnSpinner"></span>
                            <span id="btnText">&#128640;&nbsp; Create Account</span>
                        </button>

                        <!-- Sign In Link -->
                        <div class="signin-link">
                            Already have an account?&nbsp;
                            <a href="${pageContext.request.contextPath}/login">Sign In &rarr;</a>
                        </div>

                    </div><!-- /form-grid -->
                </form>
            </div><!-- /form-card -->

            <div class="page-footer">
                &copy; 2024 Kasnet Online Internship Portal. All rights reserved.
            </div>

        </div><!-- /form-container -->
    </main>

</div><!-- /page-wrapper -->

<!-- ===== INLINE JAVASCRIPT ===== -->
<script>
    /* =============================================
       CAPTCHA GENERATION (Client-side fallback)
       ============================================= */
    var CAPTCHA_CHARS = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    var clientCaptchaValue = '';
    var serverCaptchaPresent = false;

    (function initCaptcha() {
        var captchaTextEl = document.getElementById('captchaText');
        // If server already set the captcha text content, use server mode
        if (captchaTextEl && captchaTextEl.textContent.trim().length > 0) {
            serverCaptchaPresent = true;
            clientCaptchaValue = captchaTextEl.textContent.trim();
        } else {
            generateClientCaptcha();
        }
    })();

    function generateClientCaptcha() {
        var result = '';
        for (var i = 0; i < 6; i++) {
            result += CAPTCHA_CHARS.charAt(Math.floor(Math.random() * CAPTCHA_CHARS.length));
        }
        clientCaptchaValue = result;
        document.getElementById('captchaText').textContent = result;
    }

    function refreshCaptcha() {
        if (!serverCaptchaPresent) {
            generateClientCaptcha();
            document.getElementById('captchaInput').value = '';
            clearFieldError('captchaInput', 'captchaError');
        } else {
            // Redirect to server endpoint to regenerate captcha
            window.location.href = '${pageContext.request.contextPath}/register?refreshCaptcha=true';
        }
        // Animate icon
        var btn = document.querySelector('.captcha-refresh');
        btn.style.transform = 'rotate(360deg)';
        btn.style.transition = 'transform 0.4s ease';
        setTimeout(function() {
            btn.style.transform = '';
            btn.style.transition = '';
        }, 500);
    }

    /* =============================================
       PASSWORD VISIBILITY TOGGLE
       ============================================= */
    function togglePass(inputId, iconId) {
        var inp = document.getElementById(inputId);
        var icon = document.getElementById(iconId);
        if (inp.type === 'password') {
            inp.type = 'text';
            icon.textContent = '\uD83D\uDE48'; // 🙈
        } else {
            inp.type = 'password';
            icon.textContent = '\uD83D\uDC41'; // 👁
        }
    }

    /* =============================================
       PASSWORD STRENGTH INDICATOR
       ============================================= */
    var strengthBar = document.getElementById('strengthBar');
    var strengthText = document.getElementById('strengthText');

    document.getElementById('password').addEventListener('input', function() {
        var val = this.value;
        var score = calcPasswordStrength(val);
        var levels = [
            { label: '',       color: '#e5e7eb', width: '0%'   },
            { label: 'Weak',   color: '#ef4444', width: '25%'  },
            { label: 'Fair',   color: '#f97316', width: '50%'  },
            { label: 'Good',   color: '#eab308', width: '75%'  },
            { label: 'Strong', color: '#22c55e', width: '100%' }
        ];
        var level = levels[score];
        strengthBar.style.width = level.width;
        strengthBar.style.backgroundColor = level.color;
        strengthText.textContent = score > 0 ? level.label : '';
        strengthText.style.color = level.color;

        // Also re-validate confirm password match if it has value
        var confVal = document.getElementById('confirmPassword').value;
        if (confVal.length > 0) checkPasswordMatch();
    });

    function calcPasswordStrength(pwd) {
        if (!pwd || pwd.length === 0) return 0;
        var score = 0;
        if (pwd.length >= 8)  score++;
        if (pwd.length >= 12) score++;
        if (/[A-Z]/.test(pwd) && /[a-z]/.test(pwd)) score++;
        if (/[0-9]/.test(pwd)) score++;
        if (/[^A-Za-z0-9]/.test(pwd)) score++;
        if (score <= 1) return 1;
        if (score === 2) return 2;
        if (score === 3) return 3;
        return 4;
    }

    /* =============================================
       PASSWORD MATCH CHECK
       ============================================= */
    var matchMsg = document.getElementById('matchMsg');

    document.getElementById('confirmPassword').addEventListener('blur', checkPasswordMatch);
    document.getElementById('confirmPassword').addEventListener('input', function() {
        if (this.value.length > 0) checkPasswordMatch();
    });

    function checkPasswordMatch() {
        var pwd  = document.getElementById('password').value;
        var conf = document.getElementById('confirmPassword').value;
        var confInput = document.getElementById('confirmPassword');
        if (conf.length === 0) {
            matchMsg.textContent = '';
            matchMsg.className = 'match-msg';
            return;
        }
        if (pwd === conf) {
            matchMsg.textContent = '✓ Passwords match';
            matchMsg.className = 'match-msg match-ok';
            confInput.classList.remove('input-error');
            confInput.classList.add('input-success');
            hideFieldError('confirmPasswordError');
        } else {
            matchMsg.textContent = '✗ Passwords do not match';
            matchMsg.className = 'match-msg match-fail';
            confInput.classList.remove('input-success');
        }
    }

    /* =============================================
       MOBILE NUMBER: DIGITS ONLY, MAX 10
       ============================================= */
    document.getElementById('mobile').addEventListener('input', function() {
        this.value = this.value.replace(/\D/g, '').substring(0, 10);
    });

    document.getElementById('mobile').addEventListener('keypress', function(e) {
        if (!/[0-9]/.test(e.key)) e.preventDefault();
    });

    /* =============================================
       FIELD ERROR HELPERS
       ============================================= */
    function showFieldError(inputId, errorId, message) {
        var inp = document.getElementById(inputId);
        var err = document.getElementById(errorId);
        if (inp) inp.classList.add('input-error');
        if (err) {
            if (message) err.textContent = message;
            err.classList.add('visible');
        }
    }

    function hideFieldError(errorId) {
        var err = document.getElementById(errorId);
        if (err) err.classList.remove('visible');
    }

    function clearFieldError(inputId, errorId) {
        var inp = document.getElementById(inputId);
        var err = document.getElementById(errorId);
        if (inp) inp.classList.remove('input-error');
        if (err) err.classList.remove('visible');
    }

    // Clear errors on user input
    var fieldsToWatch = ['firstName','lastName','email','mobile','password','confirmPassword','role','stream','collegeName','city','captchaInput'];
    fieldsToWatch.forEach(function(id) {
        var el = document.getElementById(id);
        if (el) {
            el.addEventListener('input', function() {
                this.classList.remove('input-error');
                var errorEl = document.getElementById(id + 'Error');
                if (errorEl) errorEl.classList.remove('visible');
                // Hide client alert too
                document.getElementById('clientAlert').style.display = 'none';
            });
        }
    });

    /* =============================================
       CLIENT-SIDE FORM VALIDATION ON SUBMIT
       ============================================= */
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        e.preventDefault();

        var valid = true;
        var firstErrorMsg = '';

        function require(inputId, errorId, message) {
            var el = document.getElementById(inputId);
            var val = el ? el.value.trim() : '';
            if (!val) {
                showFieldError(inputId, errorId, message);
                if (valid) firstErrorMsg = message;
                valid = false;
            } else {
                clearFieldError(inputId, errorId);
            }
        }

        // First Name
        require('firstName', 'firstNameError', 'First name is required.');
        // Last Name
        require('lastName', 'lastNameError', 'Last name is required.');

        // Mobile
        var mobileVal = document.getElementById('mobile').value.trim();
        if (!mobileVal || mobileVal.length !== 10 || !/^\d{10}$/.test(mobileVal)) {
            showFieldError('mobile', 'mobileError', 'Enter a valid 10-digit mobile number.');
            if (valid) firstErrorMsg = 'Enter a valid 10-digit mobile number.';
            valid = false;
        } else {
            clearFieldError('mobile', 'mobileError');
        }

        // Email
        var emailVal = document.getElementById('email').value.trim();
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailVal) {
            showFieldError('email', 'emailError', 'Email address is required.');
            if (valid) firstErrorMsg = 'Email address is required.';
            valid = false;
        } else if (!emailRegex.test(emailVal)) {
            showFieldError('email', 'emailError', 'Enter a valid email address.');
            if (valid) firstErrorMsg = 'Enter a valid email address.';
            valid = false;
        } else {
            clearFieldError('email', 'emailError');
        }

        // Password
        var pwdVal = document.getElementById('password').value;
        if (!pwdVal || pwdVal.length < 8) {
            showFieldError('password', 'passwordError', 'Password must be at least 8 characters.');
            if (valid) firstErrorMsg = 'Password must be at least 8 characters.';
            valid = false;
        } else {
            clearFieldError('password', 'passwordError');
        }

        // Confirm Password
        var confVal = document.getElementById('confirmPassword').value;
        if (!confVal) {
            showFieldError('confirmPassword', 'confirmPasswordError', 'Please confirm your password.');
            if (valid) firstErrorMsg = 'Please confirm your password.';
            valid = false;
        } else if (pwdVal !== confVal) {
            showFieldError('confirmPassword', 'confirmPasswordError', 'Passwords do not match.');
            if (valid) firstErrorMsg = 'Passwords do not match.';
            valid = false;
        } else {
            clearFieldError('confirmPassword', 'confirmPasswordError');
        }

        // Role
        require('role', 'roleError', 'Please select a role.');
        // Stream
        require('stream', 'streamError', 'Please select your stream.');
        // College
        require('collegeName', 'collegeNameError', 'College name is required.');
        // City
        require('city', 'cityError', 'City is required.');

        // CAPTCHA
        var captchaInput = document.getElementById('captchaInput').value.trim().toUpperCase();
        if (!captchaInput) {
            showFieldError('captchaInput', 'captchaError', 'Please enter the CAPTCHA code.');
            if (valid) firstErrorMsg = 'Please enter the CAPTCHA code.';
            valid = false;
        } else if (!serverCaptchaPresent && captchaInput !== clientCaptchaValue) {
            showFieldError('captchaInput', 'captchaError', 'CAPTCHA code is incorrect. Please try again.');
            if (valid) firstErrorMsg = 'CAPTCHA code is incorrect. Please try again.';
            valid = false;
            generateClientCaptcha();
            document.getElementById('captchaInput').value = '';
        } else {
            clearFieldError('captchaInput', 'captchaError');
        }

        if (!valid) {
            // Show client alert
            var alertBox = document.getElementById('clientAlert');
            document.getElementById('clientAlertMsg').textContent = firstErrorMsg || 'Please fix the highlighted errors above.';
            alertBox.style.display = 'flex';
            // Scroll to top of form
            alertBox.scrollIntoView({ behavior: 'smooth', block: 'center' });
            return;
        }

        // All valid — show loading state and submit
        var btn = document.getElementById('submitBtn');
        var spinner = document.getElementById('btnSpinner');
        var btnText = document.getElementById('btnText');
        btn.disabled = true;
        spinner.style.display = 'inline-block';
        btnText.textContent = ' Creating Account...';
        document.getElementById('clientAlert').style.display = 'none';

        this.submit();
    });

    /* =============================================
       LIVE FIELD VALIDATION (blur events)
       ============================================= */
    document.getElementById('firstName').addEventListener('blur', function() {
        if (!this.value.trim()) showFieldError('firstName', 'firstNameError', 'First name is required.');
        else clearFieldError('firstName', 'firstNameError');
    });

    document.getElementById('lastName').addEventListener('blur', function() {
        if (!this.value.trim()) showFieldError('lastName', 'lastNameError', 'Last name is required.');
        else clearFieldError('lastName', 'lastNameError');
    });

    document.getElementById('email').addEventListener('blur', function() {
        var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!this.value.trim()) showFieldError('email', 'emailError', 'Email address is required.');
        else if (!emailRegex.test(this.value.trim())) showFieldError('email', 'emailError', 'Enter a valid email address.');
        else clearFieldError('email', 'emailError');
    });

    document.getElementById('mobile').addEventListener('blur', function() {
        if (!this.value || this.value.length !== 10 || !/^\d{10}$/.test(this.value))
            showFieldError('mobile', 'mobileError', 'Enter a valid 10-digit mobile number.');
        else clearFieldError('mobile', 'mobileError');
    });

    document.getElementById('collegeName').addEventListener('blur', function() {
        if (!this.value.trim()) showFieldError('collegeName', 'collegeNameError', 'College name is required.');
        else clearFieldError('collegeName', 'collegeNameError');
    });

    document.getElementById('city').addEventListener('blur', function() {
        if (!this.value.trim()) showFieldError('city', 'cityError', 'City is required.');
        else clearFieldError('city', 'cityError');
    });

    /* =============================================
       AUTO-DISMISS SERVER ALERT AFTER 6s
       ============================================= */
    (function() {
        var serverAlert = document.getElementById('serverAlert');
        if (serverAlert) {
            setTimeout(function() {
                serverAlert.style.transition = 'opacity 0.6s ease';
                serverAlert.style.opacity = '0';
                setTimeout(function() { serverAlert.style.display = 'none'; }, 600);
            }, 6000);
        }
    })();
</script>

</body>
</html>
