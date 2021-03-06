package org.lordsofchaos.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCommunication {

    private static Connection conn;
    private static String dbName = "vUx0GmhOrL";
    private static String dbURL = "jdbc:mysql://remotemysql.com:3306/vUx0GmhOrL?useSSL=false&useUnicode=true&characterEncoding=UTF-8&user=vUx0GmhOrL&password=uKKJhxJLlm";

    /**
     * Converts a ResultSet to a list of LeaderBoardRows
     *
     * @param rs    the ResultSet to convert
     * @param count how many ResultSet rows to convert
     * @param all   if true, convert all ResultSets
     */
    private static List<LeaderboardRow> resultSetToRows(ResultSet rs, boolean all, int count)
        throws SQLException {
        List<LeaderboardRow> rows = new ArrayList<>();
        while (rs.next() && (count > 0 || all)) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            int waves = rs.getInt(3);
            Date date = rs.getDate(4);
            LeaderboardRow row = new LeaderboardRow(id, name, waves, date);
            rows.add(row);
            if (!all) {
                count--;
            }
        }
        return rows;
    }

    /**
     * Gets a list of LeaderBoardRows with information taken from the database
     *
     * @param count how many rows to fetch
     * @param all   if true, fetchjall rows
     */
    public static List<LeaderboardRow> getRows(int count, boolean all)
        throws SQLException, ClassNotFoundException {
        connectToDB();
        ResultSet rs = executeQuery(conn, "select * from " + dbName + ".leaderboard");
        return resultSetToRows(rs, all, count);
    }

    /**
     * Returns rows with the lowest wave number (lower wave number = higher score)
     *
     * @param count how many rows to fetch
     */
    public static List<LeaderboardRow> getHighScores(int count)
        throws SQLException, ClassNotFoundException {
        connectToDB();
        ResultSet rs = executeQuery(conn,
            "select * from " + dbName + ".leaderboard order by waves asc limit " + count);
        return resultSetToRows(rs, true, 0);
    }

    /**
     * Add a map to the maps table
     *
     * @param map map to add
     */
    public static void addMap(Map map) throws SQLException, ClassNotFoundException {
        String query;
        int userGenerated = 0;
        if (map.getUserGenerated()) {
            userGenerated = 1;
        }

        // if id is -1, let the db auto generate an id
        if (map.getID() == -1) {
            String values =
                "('" + map.getJson() + "', '" + userGenerated + "', '" + map.getMapName() + "')";
            query =
                "INSERT INTO " + dbName + ".maps (json_string, user_generated, map_name) VALUES "
                    + values;
        } else {
            String values =
                "('" + map.getID() + "', '" + map.getJson() + "', '" + userGenerated + "', '" + map
                    .getMapName() + "')";
            query = "INSERT INTO " + dbName
                + ".maps (id, json_string, user_generated, map_name) VALUES " + values;
        }
        connectToDB();
        Statement myStmt = conn.createStatement();
        myStmt.execute(query);
    }

    /**
     * Return the map with primary key 'id' from the maps table
     *
     * @param id primary key to search for
     */
    public static Map getMap(int id) throws SQLException, ClassNotFoundException {
        connectToDB();
        String query = "select * from " + dbName + ".maps " + "WHERE id = " + id;
        ResultSet rs = executeQuery(conn, query);
        rs.next();

        int foundID = rs.getInt(1);
        String foundJson = rs.getString(2);
        int foundUser_generated = rs.getInt(3);
        boolean userGenerated = false;
        if (foundUser_generated == 1) {
            userGenerated = true;
        }
        String foundMapName = rs.getString(4);
        return new Map(foundID, foundMapName, foundJson, userGenerated);
    }

    /**
     * Return a list of maps from the maps table, starting from index 'start' and ending at index
     * 'end'
     */
    public static List<Map> getMaps(int start, int end)
        throws SQLException, ClassNotFoundException {
        connectToDB();
        String query = "select * from " + dbName + ".maps "; // get all maps
        ResultSet rs = executeQuery(conn, query);
        int size = numberOfMaps();
        if (start >= size) // if the start index is bigger than the number of rows, return null
        {
            return null;
        }
        List<Map> maps = new ArrayList<>();
        int count = 0;
        while (rs.next()) {
            if (count >= start && count < end) {
                int foundID = rs.getInt(1);
                String foundJson = rs.getString(2);
                int foundUser_generated = rs.getInt(3);
                boolean userGenerated = false;
                if (foundUser_generated == 1) {
                    userGenerated = true;
                }
                String foundMapName = rs.getString(4);
                Map map = new Map(foundID, foundMapName, foundJson, userGenerated);
                maps.add(map);
            } else if (count >= end) {
                break;
            }
            count++;
        }
        return maps;
    }

    public static int numberOfMaps() throws SQLException, ClassNotFoundException {
        connectToDB();
        String query = "select * from " + dbName + ".maps "; // get all maps
        ResultSet rs = executeQuery(conn, query);
        int size = 0;
        if (rs != null) {
            rs.last();
            size = rs.getRow();
        }
        return size;
    }

    /**
     * Given a LeaderBoardRow, take the information out and add it to a new entity in the database
     *
     * @param row the row to add
     */
    public static void addRow(LeaderboardRow row) throws SQLException, ClassNotFoundException {
        String query;
        if (row.getID() == -1) { // no id specified so auto-inc
            String values =
                "('" + row.getName() + "', '" + row.getWaves() + "', '" + row.getDateTime() + "')";
            query = "INSERT INTO " + dbName + ".leaderboard (name, waves, date) VALUES " + values;
        } else {
            String values =
                "('" + row.getID() + "', '" + row.getName() + "', '" + row.getWaves() + "', '" + row
                    .getDateTime() + "')";
            query =
                "INSERT INTO " + dbName + ".leaderboard (id, name, waves, date) VALUES " + values;
        }
        connectToDB();
        Statement myStmt = conn.createStatement();
        myStmt.execute(query);
    }

    /**
     * @param id    the primary key of an entity to delete from the specified table
     * @param table the table to drop from
     */
    public static void deleteRow(int id, String table) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM " + dbName + "." + table + " WHERE id = " + id;
        connectToDB();
        Statement myStmt = conn.createStatement();
        myStmt.execute(query);
    }

    /**
     * Connect to the database defined in dbURL
     */
    private static void connectToDB() throws SQLException, ClassNotFoundException {
        if (conn == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL);
        }
    }

    /**
     * @param conn  the connection to execute this query on
     * @param query the query to execute
     */
    private static ResultSet executeQuery(Connection conn, String query) throws SQLException {
        return conn.prepareStatement(query).executeQuery();
    }

    /**
     * Debug function, prints rows to the console
     */
    private static void printOutTable() throws SQLException, ClassNotFoundException {
        Map map = getMap(3);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        printOutTable();
    }
}
