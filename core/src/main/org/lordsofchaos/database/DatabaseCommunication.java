package org.lordsofchaos.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCommunication
{

    /*
    Database information:
    dbname= vUx0GmhOrL, username=vUx0GmhOrL, pw= uKKJhxJLlm, server= remotemysql.com, port 3306
    phpmyadmin user: vUx0GmhOrL, password: uKKJhxJLlm
    site pw = FIO)_£POMl1m
     */

    private static Connection conn;
    private static String dbName = "vUx0GmhOrL";
    private static String dbURL = "jdbc:mysql://remotemysql.com:3306/vUx0GmhOrL?useSSL=false&useUnicode=true&characterEncoding=UTF-8&user=vUx0GmhOrL&password=uKKJhxJLlm";
    
    // pass in result set- set 'all' to true if you want all rows from the result set, otherwise set to false and specify count
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
    
    // count is the number of rows you want to fetch, if all is true, all rows are returned
    public static List<LeaderboardRow> getRows(int count, boolean all) throws SQLException, ClassNotFoundException {
        connectToDB();
        ResultSet rs = executeQuery(conn, "select * from " + dbName + ".leaderboard");
        return resultSetToRows(rs, all, count);
    }
    
    // return n rows with lowest wave number
    public static List<LeaderboardRow> getHighScores(int count) throws SQLException, ClassNotFoundException {
        connectToDB();
        ResultSet rs = executeQuery(conn, "select * from " + dbName + ".leaderboard order by waves asc limit " + count);
        return resultSetToRows(rs, true, 0);
    }
    
    public static void addRow(LeaderboardRow row) throws SQLException, ClassNotFoundException {
        String query;
        if (row.getID() == -1) { // no id specified so auto-inc
            String values = "('" + row.getName() + "', " + row.getWaves() + ", '" + row.getDateTime() + "')";
            query = "INSERT INTO " + dbName + ".leaderboard (name, waves, date) VALUES " + values;
        } else {
            String values = "(" + row.getID() + ", '" + row.getName() + "', " + row.getWaves() + ", '" + row.getDateTime() + "')";
            query = "INSERT INTO " + dbName + ".leaderboard (id, name, waves, date) VALUES " + values;
        }
        connectToDB();
        Statement myStmt = conn.createStatement();
        myStmt.execute(query);
    }
    
    public static void deleteRow(int id) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM " + dbName + ".leaderboard WHERE id = " + id;
        connectToDB();
        Statement myStmt = conn.createStatement();
        myStmt.execute(query);
    }
    
    private static void connectToDB() throws SQLException, ClassNotFoundException {
        if (conn == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbURL);
        }
    }
    
    private static ResultSet executeQuery(Connection conn, String query) throws SQLException {
        return conn.prepareStatement(query).executeQuery();
    }
    
    // only used for debugging
    private static void printOutTable() throws SQLException, ClassNotFoundException {
        List<LeaderboardRow> rows = getHighScores(3);//getRows(0, true);
        for (int i = 0; i < rows.size(); i++) {
            System.out.println(rows.get(i).ToString());
        }
    }

    // only used for debugging
    //public static void main(String[] args) throws SQLException, ClassNotFoundException {
    //    printOutTable();
    //}
}
