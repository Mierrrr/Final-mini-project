package com.disasterrelief;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String hashed = PasswordUtil.sha256(password);

        String sql = "SELECT l.login_id, u.user_id, u.full_name, u.role " +
                     "FROM login_info l JOIN users u ON l.user_id = u.user_id " +
                     "WHERE l.username = ? AND l.password = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashed);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession session = req.getSession();
                    session.setAttribute("user_id", rs.getInt("user_id"));
                    session.setAttribute("full_name", rs.getString("full_name"));
                    session.setAttribute("role", rs.getString("role"));

                    String role = rs.getString("role");
                    if ("admin".equals(role)) resp.sendRedirect("dashboard.html");
                    else if ("volunteer".equals(role)) resp.sendRedirect("resources.html");
                    else resp.sendRedirect("request_help.html");
                } else {
                    resp.getWriter().println("<script>alert('Invalid credentials');location='login.html';</script>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}