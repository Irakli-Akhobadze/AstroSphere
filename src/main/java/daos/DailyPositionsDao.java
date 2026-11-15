package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import databaseConnection.databaseConnector;
import models.DailyPlanetaryPosition;

public class DailyPositionsDao {
    public static void saveForDate(List<DailyPlanetaryPosition> positions, LocalDate date) throws SQLException {
        Connection connection = databaseConnector.getConnection();
        if (connection == null) throw new SQLException("Database connection failed.");
        try (Connection con = connection) {
            con.setAutoCommit(false);
            try (PreparedStatement delete = con.prepareStatement("DELETE FROM dailyplanetarypositions WHERE datePick = ?")) {
                delete.setDate(1, java.sql.Date.valueOf(date));
                delete.executeUpdate();
            }
            String sql = "INSERT INTO dailyplanetarypositions(datePick, planet, zodiacSign, degree, isRetrograde) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insert = con.prepareStatement(sql)) {
                for (DailyPlanetaryPosition p : positions) {
                    insert.setDate(1, java.sql.Date.valueOf(p.getDate()));
                    insert.setString(2, p.getPlanet());
                    insert.setString(3, p.getZodiacSign());
                    insert.setBigDecimal(4, java.math.BigDecimal.valueOf(p.getDegree()));
                    insert.setBoolean(5, p.isRetrograde());
                    insert.addBatch();
                }
                insert.executeBatch();
            }
            con.commit();
        }
    }

    public static List<DailyPlanetaryPosition> findByDate(LocalDate date) throws SQLException {
        List<DailyPlanetaryPosition> list = new ArrayList<>();
        String sql = "SELECT planet, zodiacSign, degree, isRetrograde FROM dailyplanetarypositions WHERE datePick = ? ORDER BY FIELD(planet,'Sun','Moon','Mercury','Venus','Mars','Jupiter','Saturn','Uranus','Neptune','Pluto')";
        Connection connection = databaseConnector.getConnection();
        if (connection == null) throw new SQLException("Database connection failed.");
        try (Connection con = connection; PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String planet = rs.getString("planet");
                    String glyph = glyphFor(planet);
                    list.add(new DailyPlanetaryPosition(date, planet, glyph, rs.getString("zodiacSign"),
                            rs.getDouble("degree"), rs.getBoolean("isRetrograde")));
                }
            }
        }
        return list;
    }
    private static String glyphFor(String p) {
        return switch (p) { case "Sun" -> "☉"; case "Moon" -> "☾"; case "Mercury" -> "☿"; case "Venus" -> "♀"; case "Mars" -> "♂"; case "Jupiter" -> "♃"; case "Saturn" -> "♄"; case "Uranus" -> "♅"; case "Neptune" -> "♆"; default -> "♇"; };
    }
}
