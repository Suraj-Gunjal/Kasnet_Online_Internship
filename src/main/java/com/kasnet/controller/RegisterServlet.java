package com.kasnet.controller;

import com.kasnet.model.User;
import com.kasnet.service.AuthenticationService;
import com.kasnet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * Servlet handling user registration (GET + POST) at /register.
 *
 * GET  – Forwards to register.jsp (with CAPTCHA pre-generated).
 * POST – Performs full server-side validation, CAPTCHA check, then delegates
 *         to AuthenticationService.register(). On success redirects to /login
 *         with a success flash message; on failure returns to register.jsp with
 *         all previously entered values preserved.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String CAPTCHA_CHARS  = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int    CAPTCHA_LENGTH  = 5;

    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
    }

    // -------------------------------------------------------------------------
    // GET — Show registration page
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Generate and store CAPTCHA in session
        HttpSession session = request.getSession(true);
        session.setAttribute("captcha", generateCaptcha());

        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    // -------------------------------------------------------------------------
    // POST — Process registration form
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // --- Collect all form parameters ---
        String firstName    = request.getParameter("firstName");
        String lastName     = request.getParameter("lastName");
        String countryCode  = request.getParameter("countryCode");
        String mobile       = request.getParameter("mobile");
        String email        = request.getParameter("email");
        String password     = request.getParameter("password");
        String confirmPass  = request.getParameter("confirmPassword");
        String role         = request.getParameter("role");
        String stream       = request.getParameter("stream");
        String collegeName  = request.getParameter("collegeName");
        String city         = request.getParameter("city");
        String referralCode = request.getParameter("referralCode");
        String captchaInput = request.getParameter("captcha");

        // --- Retrieve existing session for CAPTCHA ---
        HttpSession session = request.getSession(false);

        // Helper: preserve form values on error so user doesn't retype
        // (bound as request attributes so JSP can repopulate inputs)
        Runnable preserveFormValues = () -> {
            request.setAttribute("f_firstName",   sanitize(firstName));
            request.setAttribute("f_lastName",    sanitize(lastName));
            request.setAttribute("f_countryCode", sanitize(countryCode));
            request.setAttribute("f_mobile",      sanitize(mobile));
            request.setAttribute("f_email",       sanitize(email));
            request.setAttribute("f_role",        sanitize(role));
            request.setAttribute("f_stream",      sanitize(stream));
            request.setAttribute("f_collegeName", sanitize(collegeName));
            request.setAttribute("f_city",        sanitize(city));
            request.setAttribute("f_referralCode",sanitize(referralCode));
        };

        // --- CAPTCHA validation ---
        if (session == null) {
            preserveFormValues.run();
            forwardWithError(request, response,
                    "Session expired. Please refresh and try again.");
            return;
        }

        String expectedCaptcha = (String) session.getAttribute("captcha");
        if (expectedCaptcha == null || captchaInput == null
                || !expectedCaptcha.equalsIgnoreCase(captchaInput.trim())) {
            session.setAttribute("captcha", generateCaptcha());
            preserveFormValues.run();
            forwardWithError(request, response,
                    "Incorrect CAPTCHA. Please try again.");
            return;
        }
        // Consume the used CAPTCHA
        session.removeAttribute("captcha");

        // --- Required field validation ---
        if (isNullOrBlank(firstName)) {
            regenAndError(request, response, session, preserveFormValues,
                    "First name is required.");
            return;
        }
        if (isNullOrBlank(lastName)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Last name is required.");
            return;
        }
        if (isNullOrBlank(email)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Email address is required.");
            return;
        }
        if (isNullOrBlank(mobile)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Mobile number is required.");
            return;
        }
        if (isNullOrBlank(password)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Password is required.");
            return;
        }
        if (isNullOrBlank(confirmPass)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Please confirm your password.");
            return;
        }
        if (isNullOrBlank(role)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Please select a role.");
            return;
        }

        // --- Email format validation ---
        if (!ValidationUtil.isValidEmail(email.trim())) {
            regenAndError(request, response, session, preserveFormValues,
                    "Please enter a valid email address.");
            return;
        }

        // --- Mobile number validation ---
        if (!ValidationUtil.isValidMobile(mobile.trim())) {
            regenAndError(request, response, session, preserveFormValues,
                    "Please enter a valid 10-digit mobile number.");
            return;
        }

        // --- Password strength validation ---
        if (!ValidationUtil.isStrongPassword(password)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Password must be at least 8 characters and include uppercase, lowercase, a digit, and a special character.");
            return;
        }

        // --- Password match validation ---
        if (!password.equals(confirmPass)) {
            regenAndError(request, response, session, preserveFormValues,
                    "Passwords do not match. Please re-enter.");
            return;
        }

        // --- Role whitelist validation ---
        if (!isValidRole(role.trim())) {
            regenAndError(request, response, session, preserveFormValues,
                    "Invalid role selected. Please choose a valid role.");
            return;
        }

        // --- Build User object ---
        User user = new User();
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setCountryCode(sanitize(countryCode));
        user.setMobile(mobile.trim());
        user.setPassword(password); // Hashing done inside AuthenticationService
        user.setRole(role.trim().toLowerCase());
        user.setStream(sanitize(stream));
        user.setCollegeName(sanitize(collegeName));
        user.setCity(sanitize(city));
        user.setReferralCode(sanitize(referralCode));

        // --- Attempt registration ---
        try {
            authService.register(user);

            // Set success flash message in session and redirect to login
            HttpSession newSession = request.getSession(true);
            newSession.setAttribute("successMessage",
                    "Registration successful! Please login to continue.");
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (Exception e) {
            // Preserve form values and show the error from the service
            session = request.getSession(true);
            session.setAttribute("captcha", generateCaptcha());
            preserveFormValues.run();
            forwardWithError(request, response, e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String generateCaptcha() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Regenerates CAPTCHA, runs form-value preservation, then forwards with error.
     */
    private void regenAndError(HttpServletRequest request,
                                HttpServletResponse response,
                                HttpSession session,
                                Runnable preserveFormValues,
                                String errorMessage)
            throws ServletException, IOException {
        session.setAttribute("captcha", generateCaptcha());
        preserveFormValues.run();
        forwardWithError(request, response, errorMessage);
    }

    private void forwardWithError(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    /**
     * Validates that the submitted role is one of the accepted values.
     */
    private boolean isValidRole(String role) {
        return "student".equalsIgnoreCase(role)
                || "admin".equalsIgnoreCase(role)
                || "mentor".equalsIgnoreCase(role);
    }

    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String sanitize(String s) {
        return s == null ? "" : s.trim();
    }
}
