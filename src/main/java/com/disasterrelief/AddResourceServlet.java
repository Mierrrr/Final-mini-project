package com.disasterrelief;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AddResourceServlet")
public class AddResourceServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Integer addedBy = (session != null) ? (Integer) session.getAttribute("user_id") : null;

        String name = req.getParameter("name");
        String category = req.getParameter("category");
        String quantityStr = req.getParameter("quantity");
        int quantity = (quantityStr != null && !quantityStr.isEmpty()) ? Integer.parseInt(quantityStr) : 0;
        String unit = req.getParameter("unit");
        String location = req.getParameter("location");

        String sql = "INSERT INTO resources (name, category, quantity, unit, location, added_by) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setInt(3, quantity);
            ps.setString(4, unit);
            ps.setString(5, location);
            if (addedBy != null) ps.setInt(6, addedBy); else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            resp.sendRedirect("resources.html");
        } catch (SQLException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}