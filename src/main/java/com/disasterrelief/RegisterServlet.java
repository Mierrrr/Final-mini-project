package com.disasterrelief;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("name");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String role = req.getParameter("role"); // admin, volunteer, victim, donor
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String hashed = PasswordUtil.sha256(password);

        String insertUserSql = "INSERT INTO users (full_name, email, phone, address, role) VALUES (?, ?, ?, ?, ?)";
        String insertLoginSql = "INSERT INTO login_info (user_id, username, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {

            psUser.setString(1, fullName);
            psUser.setString(2, email);
            psUser.setString(3, phone);
            psUser.setString(4, address);
            psUser.setString(5, role);
            int affected = psUser.executeUpdate();

            if (affected == 0) {
                resp.getWriter().println("Failed to register user.");
                return;
            }

            try (ResultSet keys = psUser.getGeneratedKeys()) {
                if (keys.next()) {
                    int userId = keys.getInt(1);
                    try (PreparedStatement psLogin = conn.prepareStatement(insertLoginSql)) {
                        psLogin.setInt(1, userId);
                        psLogin.setString(2, username);
                        psLogin.setString(3, hashed);
                        psLogin.executeUpdate();
                    }
                }
            }

            resp.sendRedirect("login.html");
        } catch (SQLException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}