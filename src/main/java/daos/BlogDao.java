package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import databaseConnection.databaseConnector;
import models.Blog;

public class BlogDao {
    public static List<Blog> findByUser(int userId) throws SQLException {
        if (userId <= 0) throw new IllegalArgumentException("No active user.");
        List<Blog> blogs = new ArrayList<>();
        String sql = "SELECT blogId, blogTitle, blogContent, createdAt FROM blogs WHERE userId = ? ORDER BY createdAt DESC";
        Connection connection = databaseConnector.getConnection();
        if (connection == null) throw new SQLException("Database connection failed.");
        try (Connection con = connection; PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    java.sql.Timestamp timestamp = rs.getTimestamp("createdAt");
                    blogs.add(new Blog(rs.getInt("blogId"), rs.getString("blogTitle"), rs.getString("blogContent"),
                            timestamp == null ? null : timestamp.toLocalDateTime()));
                }
            }
        }
        return blogs;
    }

    public static void create(int userId, String title, String content) throws SQLException {
        if (userId <= 0) throw new IllegalArgumentException("No active user.");
        String sql = "INSERT INTO blogs(blogTitle, blogContent, userId) VALUES (?, ?, ?)";
        Connection connection = databaseConnector.getConnection();
        if (connection == null) throw new SQLException("Database connection failed.");
        try (Connection con = connection; PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, title.trim()); statement.setString(2, content.trim()); statement.setInt(3, userId);
            statement.executeUpdate();
        }
    }

    public static void update(int blogId, int userId, String title, String content) throws SQLException {
        String sql = "UPDATE blogs SET blogTitle = ?, blogContent = ? WHERE blogId = ? AND userId = ?";
        Connection connection = databaseConnector.getConnection();
        if (connection == null) throw new SQLException("Database connection failed.");
        try (Connection con = connection; PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, title.trim()); statement.setString(2, content.trim());
            statement.setInt(3, blogId); statement.setInt(4, userId);
            if (statement.executeUpdate() == 0) throw new SQLException("Blog not found or access denied.");
        }
    }

    public static void delete(int blogId, int userId) throws SQLException {
        Connection connection = databaseConnector.getConnection();
        if (connection == null) throw new SQLException("Database connection failed.");
        try (Connection con = connection) {
            con.setAutoCommit(false);
            try (PreparedStatement check = con.prepareStatement("SELECT blogId FROM blogs WHERE blogId = ? AND userId = ?")) {
                check.setInt(1, blogId); check.setInt(2, userId);
                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Blog not found or access denied.");
                }
            }
            String[] cleanup = {
                "DELETE FROM notifications WHERE blogId = ?",
                "DELETE FROM savedblogs WHERE blogId = ?",
                "DELETE FROM bloglikes WHERE blogId = ?",
                "DELETE FROM blogcomments WHERE blogId = ?",
                "DELETE FROM blogs WHERE blogId = ? AND userId = ?"
            };
            for (int i = 0; i < cleanup.length; i++) {
                try (PreparedStatement statement = con.prepareStatement(cleanup[i])) {
                    statement.setInt(1, blogId);
                    if (i == cleanup.length - 1) statement.setInt(2, userId);
                    statement.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException ex) {
            throw ex;
        }
    }
}
