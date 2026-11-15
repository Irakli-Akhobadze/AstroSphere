package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import databaseConnection.databaseConnector;
import models.BirthChart;

public class BirthChartDao {

    public static models.BirthChartDetails loadLatest(int userId) throws SQLException {
        String sql = "SELECT chartName, dateOfBirth, timeOfBirth, birthPlace FROM birthchart WHERE userId = ? ORDER BY createdAt DESC LIMIT 1";
        Connection connection = databaseConnector.getConnection();
        if (connection == null) throw new SQLException("Could not connect to the AstroSphere database.");
        try (Connection con = connection; PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (java.sql.ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return null;
                String name = rs.getString("chartName");
                if (name != null && name.endsWith(" - Natal Chart")) name = name.substring(0, name.length() - 14);
                return new models.BirthChartDetails(name,
                        rs.getDate("dateOfBirth").toLocalDate(),
                        rs.getTime("timeOfBirth").toLocalTime(),
                        rs.getString("birthPlace"));
            }
        }
    }


    public static void save(BirthChart chart, int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("There is no active user session.");
        }

        String query = "INSERT INTO birthchart "
                + "(userId, chartName, dateOfBirth, timeOfBirth, birthPlace, chartData, latitude, longitude, timezone) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = databaseConnector.getConnection();
        if (connection == null) {
            throw new SQLException("Could not connect to the AstroSphere database.");
        }

        try (Connection con = connection;
             PreparedStatement statement = con.prepareStatement(query)) {

            statement.setInt(1, userId);
            statement.setString(2, chart.getFullName() + " - Natal Chart");
            statement.setDate(3, java.sql.Date.valueOf(chart.getDateOfBirth()));
            statement.setTime(4, java.sql.Time.valueOf(chart.getTimeOfBirth()));
            statement.setString(5, chart.getBirthPlace());
            statement.setString(6, chart.getChartJson());
            statement.setDouble(7, chart.getLatitude());
            statement.setDouble(8, chart.getLongitude());
            statement.setString(9, chart.getTimezone());

            statement.executeUpdate();
        }
    }
}
