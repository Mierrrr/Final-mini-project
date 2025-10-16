package com.disasterrelief;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AddDisasterServlet")
public class AddDisasterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String type = req.getParameter("type");
        String location = req.getParameter("location");
        String date = req.getParameter("date"); // yyyy-MM-dd
        String severity = req.getParameter("severity");
        String description = req.getParameter("description");

        String sql = "INSERT INTO disasters (name, type, location, date, severity, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, location);
            ps.setDate(4, Date.valueOf(date));
            ps.setString(5, severity);
            ps.setString(6, description);
            ps.executeUpdate();
            resp.sendRedirect("dashboard.html");
        } catch (SQLException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}