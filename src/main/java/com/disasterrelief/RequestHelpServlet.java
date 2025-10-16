package com.disasterrelief;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RequestHelpServlet")
public class RequestHelpServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Integer userId = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        // if user not logged in, you may allow guest requests by passing name/email in form
        if (userId == null) {
            resp.getWriter().println("<script>alert('Please log in to request help');location='login.html';</script>");
            return;
        }

        String disasterIdStr = req.getParameter("disaster_id");
        Integer disasterId = (disasterIdStr != null && !disasterIdStr.isEmpty()) ? Integer.parseInt(disasterIdStr) : null;
        String location = req.getParameter("location");
        String helpType = req.getParameter("help_type");
        String description = req.getParameter("description");

        String sql = "INSERT INTO help_requests (user_id, disaster_id, location, help_type, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (disasterId != null) ps.setInt(2, disasterId); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, location);
            ps.setString(4, helpType);
            ps.setString(5, description);
            ps.executeUpdate();
            resp.getWriter().println("<script>alert('Help request submitted');location='request_help.html';</script>");
        } catch (SQLException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}