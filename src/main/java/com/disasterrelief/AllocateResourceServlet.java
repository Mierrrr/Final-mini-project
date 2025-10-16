package com.disasterrelief;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AllocateResourceServlet")
public class AllocateResourceServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resourceIdStr = req.getParameter("resource_id");
        String disasterIdStr = req.getParameter("disaster_id");
        String volunteerIdStr = req.getParameter("volunteer_id");
        String ngoIdStr = req.getParameter("ngo_id");
        String qtyStr = req.getParameter("quantity");

        if (resourceIdStr == null || qtyStr == null) {
            resp.getWriter().println("Missing parameters.");
            return;
        }
        int resourceId = Integer.parseInt(resourceIdStr);
        int quantity = Integer.parseInt(qtyStr);
        Integer disasterId = (disasterIdStr != null && !disasterIdStr.isEmpty()) ? Integer.parseInt(disasterIdStr) : null;
        Integer volunteerId = (volunteerIdStr != null && !volunteerIdStr.isEmpty()) ? Integer.parseInt(volunteerIdStr) : null;
        Integer ngoId = (ngoIdStr != null && !ngoIdStr.isEmpty()) ? Integer.parseInt(ngoIdStr) : null;

        String allocateSql = "INSERT INTO allocations (resource_id, disaster_id, volunteer_id, ngo_id, quantity_allocated) VALUES (?, ?, ?, ?, ?)";
        String updateResourceSql = "UPDATE resources SET quantity = quantity - ? WHERE resource_id = ? AND quantity >= ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement psUpdate = conn.prepareStatement(updateResourceSql);
             PreparedStatement psAllocate = conn.prepareStatement(allocateSql)) {

            conn.setAutoCommit(false);

            psUpdate.setInt(1, quantity);
            psUpdate.setInt(2, resourceId);
            psUpdate.setInt(3, quantity);
            int updated = psUpdate.executeUpdate();

            if (updated == 0) {
                conn.rollback();
                resp.getWriter().println("<script>alert('Not enough resource quantity');location='resources.html';</script>");
                return;
            }

            psAllocate.setInt(1, resourceId);
            if (disasterId != null) psAllocate.setInt(2, disasterId); else psAllocate.setNull(2, Types.INTEGER);
            if (volunteerId != null) psAllocate.setInt(3, volunteerId); else psAllocate.setNull(3, Types.INTEGER);
            if (ngoId != null) psAllocate.setInt(4, ngoId); else psAllocate.setNull(4, Types.INTEGER);
            psAllocate.setInt(5, quantity);
            psAllocate.executeUpdate();

            conn.commit();
            resp.getWriter().println("<script>alert('Resource allocated');location='resources.html';</script>");
        } catch (SQLException e) {
            e.printStackTrace(resp.getWriter());
        }
    }
}