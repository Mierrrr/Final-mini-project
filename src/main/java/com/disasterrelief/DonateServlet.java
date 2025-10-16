package com.disasterrelief;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DonateServlet")
public class DonateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String donorType = req.getParameter("donor_type"); // User or NGO
        String donorIdStr = req.getParameter("donor_id"); // optional if logged in
        Integer donorId = (donorIdStr != null && !donorIdStr.isEmpty()) ? Integer.parseInt(donorIdStr) : null;
        String amountStr = req.getParameter("amount");
        BigDecimal amount = (amountStr == null || amountStr.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(amountStr);
        String donationType = req.getParameter("donation_type"); // Money or Material
        String description = req.getParameter("description");

        String sql = "INSERT INTO donations (donor_type, donor_id, amount, donation_type, description, status) VALUES (?, ?, ?, ?, ?, 'Pending')";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, donorType);
            if (donorId != null) ps.setInt(2, donorId); else ps.setNull(2, Types.INTEGER);
            ps.setBigDecimal(3, amount);
            ps.setString(4, donationType);
            ps.setString(5, description);
            ps.executeUpdate();
            resp.getWriter().println("<script>alert('Donation recorded. Thank you!');location='donate.html';</script>");
        } catch (SQLException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}