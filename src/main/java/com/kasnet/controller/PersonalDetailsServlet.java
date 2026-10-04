package com.kasnet.controller;

import com.kasnet.model.StudentProfile;
import com.kasnet.service.ProfileService;
import com.kasnet.util.SessionUtil;
import com.kasnet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Servlet handling personal detail updates at /personal-details.
 *
 * GET  – Redirects to /profile (no standalone page for this action).
 * POST – Reads userId exclusively from the session (never the request), validates
 *         all submitted personal detail fields, updates both the user record and the
 *         student_profiles record via ProfileService, recalculates profile completion,
 *         then redirects back to /profile?tab=personal with a success or error parameter.
 */
@WebServlet("/personal-details")
public class PersonalDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProfileService profileService;

    @Override
    public void init() throws ServletException {
        profileService = new ProfileService();
    }

    // -------------------------------------------------------------------------
    // GET — redirect to profile
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    // -------------------------------------------------------------------------
    // POST — process personal detail form submission
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // --- Authentication guard (userId from SESSION only) ---
        if (!SessionUtil.isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession(false);
        int userId = (int) session.getAttribute("userId");

        // --- Collect form parameters ---
        String firstName = request.getParameter("firstName");
        String lastName  = request.getParameter("lastName");
        String gender    = request.getParameter("gender");
        String dobStr    = request.getParameter("dob");
        String mobile    = request.getParameter("mobile");
        String address   = request.getParameter("address");
        String city      = request.getParameter("city");
        String state     = request.getParameter("state");

        // --- Server-side validation ---
        String validationError = validatePersonalDetails(
                firstName, lastName, gender, dobStr, mobile, city, state);

        if (validationError != null) {
            redirectWithError(request, response, validationError);
            return;
        }

        // --- Parse date of birth ---
        LocalDate dob;
        try {
            dob = LocalDate.parse(dobStr.trim());
        } catch (DateTimeParseException e) {
            redirectWithError(request, response,
                    "Invalid date of birth format. Please use YYYY-MM-DD.");
            return;
        }

        // Ensure date of birth is not in the future
        if (dob.isAfter(LocalDate.now())) {
            redirectWithError(request, response,
                    "Date of birth cannot be a future date.");
            return;
        }

        // Ensure user is at least 10 years old (basic sanity check)
        if (dob.isAfter(LocalDate.now().minusYears(10))) {
            redirectWithError(request, response,
                    "Please enter a valid date of birth.");
            return;
        }

        // --- Build StudentProfile object (userId comes from session, never the form) ---
        StudentProfile profile = new StudentProfile();
        profile.setUserId(userId);                          // ALWAYS from session
        profile.setFirstName(firstName.trim());
        profile.setLastName(lastName.trim());
        profile.setGender(gender.trim());
        profile.setDob(dob);
        profile.setMobile(mobile.trim());
        profile.setAddress(address != null ? address.trim() : "");
        profile.setCity(city.trim());
        profile.setState(state.trim());

        // --- Update via service (updates user table + student_profiles table + recalcs) ---
        try {
            boolean updated = profileService.updatePersonalDetails(profile);

            if (updated) {
                // Update the display name in session if name changed
                session.setAttribute("userName",
                        profile.getFirstName() + " " + profile.getLastName());

                // Redirect with success signal
                response.sendRedirect(request.getContextPath()
                        + "/profile?tab=personal&success=1");
            } else {
                redirectWithError(request, response,
                        "No changes were saved. Please modify a field before saving.");
            }

        } catch (IllegalArgumentException e) {
            // Validation exception from service layer
            redirectWithError(request, response, e.getMessage());
        } catch (Exception e) {
            System.err.println("[PersonalDetailsServlet] Error updating personal details for userId="
                    + userId + ": " + e.getMessage());
            redirectWithError(request, response,
                    "An error occurred while saving your details. Please try again.");
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Validates all personal detail fields.
     *
     * @return A descriptive error message string if invalid, or {@code null} if all valid.
     */
    private String validatePersonalDetails(String firstName, String lastName,
                                            String gender,    String dobStr,
                                            String mobile,    String city,
                                            String state) {
        if (isNullOrBlank(firstName)) {
            return "First name is required.";
        }
        if (firstName.trim().length() > 50) {
            return "First name must not exceed 50 characters.";
        }
        if (isNullOrBlank(lastName)) {
            return "Last name is required.";
        }
        if (lastName.trim().length() > 50) {
            return "Last name must not exceed 50 characters.";
        }
        if (isNullOrBlank(gender)) {
            return "Please select a gender.";
        }
        if (!isValidGender(gender.trim())) {
            return "Invalid gender selected.";
        }
        if (isNullOrBlank(dobStr)) {
            return "Date of birth is required.";
        }
        if (isNullOrBlank(mobile)) {
            return "Mobile number is required.";
        }
        if (!ValidationUtil.isValidMobile(mobile.trim())) {
            return "Please enter a valid 10-digit mobile number.";
        }
        if (isNullOrBlank(city)) {
            return "City is required.";
        }
        if (city.trim().length() > 100) {
            return "City name must not exceed 100 characters.";
        }
        if (isNullOrBlank(state)) {
            return "State is required.";
        }
        if (state.trim().length() > 100) {
            return "State name must not exceed 100 characters.";
        }
        return null; // All valid
    }

    /**
     * Whitelists accepted gender values.
     */
    private boolean isValidGender(String gender) {
        return "male".equalsIgnoreCase(gender)
                || "female".equalsIgnoreCase(gender)
                || "other".equalsIgnoreCase(gender)
                || "prefer not to say".equalsIgnoreCase(gender);
    }

    /**
     * URL-encodes the error message and redirects to /profile?tab=personal&error=...
     */
    private void redirectWithError(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String errorMessage)
            throws IOException {
        String encodedError;
        try {
            encodedError = URLEncoder.encode(errorMessage, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedError = "An+error+occurred.";
        }
        response.sendRedirect(request.getContextPath()
                + "/profile?tab=personal&error=" + encodedError);
    }

    private boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
