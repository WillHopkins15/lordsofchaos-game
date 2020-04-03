package org.lordsofchaos.database;

import com.badlogic.gdx.utils.Json;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCommunication
{
    private static Connection conn;
    private static String dbName = "vUx0GmhOrL";
    private static String dbURL = "jdbc:mysql://remotemysql.com:3306/vUx0GmhOrL?useSSL=false&useUnicode=true&characterEncoding=UTF-8&user=vUx0GmhOrL&password=uKKJhxJLlm";
    
    /**
     * Converts a ResultSet to a list of LeaderBoardRows
     *
     * @param rs the ResultSet to convert
     * @param count how many ResultSet rows to convert
     * @param all if true, convert all ResultSets
     */
    private static List<LeaderboardRow> resultSetToRows(ResultSet rs, boolean all, int count) throws SQLException {
        List<LeaderboardRow> rows = new ArrayList<>();
        while (rs.next() && (count > 0 || all)) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            int waves = rs.getInt(3);
            Date date = rs.getDate(4);
            LeaderboardRow row = new LeaderboardRow(id, name, waves, date);
            rows.add(row);
            if (!all)
                count--;
        }
        return rows;
    }
    
    /**
     * Gets a list of LeaderBoardRows with information taken from the database
     *
     * @param count how many rows to fetch
     * @param all if true, fetchjall rows
     */
    public static List<LeaderboardRow> getRows(int count, boolean all) throws SQLException, ClassNotFoundException {
        connectToDB();
        ResultSet rs = executeQuery(conn, "select * from " + dbName + ".leaderboard");
        return resultSetToRows(rs, all, count);
    }
    
    /**
     * Returns rows with the lowest wave number (lower wave number = higher score)
     *
     * @param count how many rows to fetch
     */
    public static List<LeaderboardRow> getHighScores(int count) throws SQLException, ClassNotFoundException {
        connectToDB();
        ResultSet rs = executeQuery(conn, "select * from " + dbName + ".leaderboard order by waves asc limit " + count);
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
        if (map.getUserGenerated()) userGenerated = 1;

        // if id is -1, let the db auto generate an id
        if (map.getID() == -1) {
            String values = "('" +map.getJson() + "', '" + userGenerated + "', '" + map.getMapName() + "')";
            query = "INSERT INTO " + dbName + ".maps (json_string, user_generated, map_name) VALUES " + values;
        } else {
            String values = "('" +map.getID() + "', '" +map.getJson() + "', '" + userGenerated + "', '" + map.getMapName() + "')";
            query = "INSERT INTO " + dbName + ".maps (id, json_string, user_generated, map_name) VALUES " + values;
        }
        System.out.println(query);
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
        if (foundUser_generated == 1) userGenerated = true;
        String foundMapName = rs.getString(4);
        return new Map(foundID, foundMapName, foundJson, userGenerated);
    }

    /**
     * Given a LeaderBoardRow, take the information out and add it to a new entity in the database
     *
     * @param row the row to add
     */
    public static void addRow(LeaderboardRow row) throws SQLException, ClassNotFoundException {
        String query;
        if (row.getID() == -1) { // no id specified so auto-inc
            String values = "('" + row.getName() + "', '" + row.getWaves() + "', '" + row.getDateTime() + "')";
            query = "INSERT INTO " + dbName + ".leaderboard (name, waves, date) VALUES " + values;
        } else {
            String values = "('" + row.getID() + "', '" + row.getName() + "', '" + row.getWaves() + "', '" + row.getDateTime() + "')";
            query = "INSERT INTO " + dbName + ".leaderboard (id, name, waves, date) VALUES " + values;
        }
        connectToDB();
        Statement myStmt = conn.createStatement();
        myStmt.execute(query);
    }

    /**
     * @param id the primary key of an entity to delete from the specified table
     * @param table the table to drop from
     */
    public static void deleteRow(int id, String table) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM " + dbName + "."+table+" WHERE id = " + id;
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
     * @param conn the connection to execute this query on
     * @param query the query to execute
     */
    private static ResultSet executeQuery(Connection conn, String query) throws SQLException {
        return conn.prepareStatement(query).executeQuery();
    }

    /**
     * Debug function, prints rows to the console
     */
    private static void printOutTable() throws SQLException, ClassNotFoundException {
        //Map map = new Map(10,"test_map", "testJson",false );
        //addMap(map);
        Map map = getMap(3);
        //List<LeaderboardRow> rows = getHighScores(3);//getRows(0, true);
        //for (int i = 0; i < rows.size(); i++) {
        //    System.out.println(rows.get(i).ToString());
        //}
        System.out.println(map.getMapName());
    }

    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        printOutTable();
    }
}
