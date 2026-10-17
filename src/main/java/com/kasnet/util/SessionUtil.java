package com.kasnet.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

/**
 * SessionUtil - HTTP Session Management Utility
 *
 * Centralises all session read/write operations for the Kasnet Online Internship Portal.
 * Keeps session attribute key strings in one place to avoid typos across servlets/JSPs.
 *
 * Usage:
 *   SessionUtil.setUserId(request, 42);
 *   if (!SessionUtil.requireLogin(request, response)) return;
 *   int uid = SessionUtil.getUserId(request);
 */
public class SessionUtil {

    // -------------------------------------------------------------------------
    // Session attribute keys — single source of truth
    // -------------------------------------------------------------------------

    /** Session key for the logged-in user's database ID. */
    public static final String ATTR_USER_ID   = "userId";

    /** Session key for the logged-in user's display name. */
    public static final String ATTR_USER_NAME = "userName";

    /** Context-relative path of the login page (used for redirect on 401). */
    private static final String LOGIN_PATH = "/login";

    // Private constructor — utility class
    private SessionUtil() {
        throw new UnsupportedOperationException("SessionUtil is a utility class.");
    }

    // -------------------------------------------------------------------------
    // Read methods
    // -------------------------------------------------------------------------

    /**
     * Returns the logged-in user's ID from the current session.
     *
     * @param request the current HTTP request
     * @return the userId stored in session, or {@code null} if not logged in
     *         (no session exists, session expired, or attribute not set)
     */
    public static Integer getUserId(HttpServletRequest request) {
        // false → do NOT create a new session if one does not exist
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object userId = session.getAttribute(ATTR_USER_ID);
        if (userId instanceof Integer) {
            return (Integer) userId;
        }
        return null;
    }

    /**
     * Returns the logged-in user's display name from the current session.
     *
     * @param request the current HTTP request
     * @return the user's name, or {@code null} if not set / not logged in
     */
    public static String getUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object name = session.getAttribute(ATTR_USER_NAME);
        return (name instanceof String) ? (String) name : null;
    }

    /**
     * Checks whether a valid login session is present (i.e. a userId is stored).
     *
     * @param request the current HTTP request
     * @return {@code true} if the user is logged in
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getUserId(request) != null;
    }

    // -------------------------------------------------------------------------
    // Write methods
    // -------------------------------------------------------------------------

    /**
     * Stores the authenticated user's database ID in the session.
     * Creates a new session if one does not already exist.
     *
     * <p>Call this immediately after a successful password check.</p>
     *
     * @param request the current HTTP request
     * @param userId  the user's primary key from the database
     */
    public static void setUserId(HttpServletRequest request, int userId) {
        // true → create session if absent
        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_USER_ID, userId);
    }

    /**
     * Stores the authenticated user's display name in the session.
     *
     * @param request the current HTTP request
     * @param name    the user's full name or username to display in the UI
     */
    public static void setUserName(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_USER_NAME, name);
    }

    // -------------------------------------------------------------------------
    // Session lifecycle
    // -------------------------------------------------------------------------

    /**
     * Invalidates the current session, effectively logging the user out.
     *
     * <p>This removes all session attributes and releases the session object.
     * Safe to call even if no session exists.</p>
     *
     * @param request the current HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // -------------------------------------------------------------------------
    // Guard / redirect helper
    // -------------------------------------------------------------------------

    /**
     * Enforces login — redirects to the login page if the user is not authenticated.
     *
     * <p>Typical usage at the top of a servlet's {@code doGet} / {@code doPost}:</p>
     * <pre>{@code
     *   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
     *           throws ServletException, IOException {
     *       if (!SessionUtil.requireLogin(req, resp)) return;
     *       // ... protected logic here ...
     *   }
     * }</pre>
     *
     * @param request  the current HTTP request
     * @param response the current HTTP response (used to send the redirect)
     * @return {@code true} if the user IS logged in (safe to proceed);
     *         {@code false} if a redirect was issued (caller must return immediately)
     * @throws IOException if the redirect write fails
     */
    public static boolean requireLogin(HttpServletRequest request,
                                       HttpServletResponse response) throws IOException {
        if (isLoggedIn(request)) {
            return true; // user is authenticated — proceed normally
        }
        // Build the absolute redirect URL: context-path + /login
        String loginUrl = request.getContextPath() + LOGIN_PATH;
        response.sendRedirect(loginUrl);
        return false; // redirect issued — caller must stop processing
    }
}
