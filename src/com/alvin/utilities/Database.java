package com.alvin.utilities;


import java.sql.*;

import static com.alvin.utilities.DebugManager.error;
import static com.alvin.utilities.DebugManager.message;

/**
 * 用于操作数据库的类
 *
 * @author Alvin Zhu
 * @version 1.0
 */
public class Database {
    public Connection connection;
    public Statement statement;
    public ResultSet resultSet;

    /**
     * 构造方法：加载数据库驱动并建立连接
     *
     * @param driver   数据库驱动字符串
     * @param url      数据库连接字符串
     * @param username 数据库用户名
     * @param password 数据库密码
     */
    public Database(String driver, String url, String username, String password) {
        loadDriver(driver);
        connection = getConnection(url, username, password);
    }

    /**
     * 加载数据库驱动
     *
     * @param driver 数据库驱动字符串
     */
    public static void loadDriver(String driver) {
        try {
            Class.forName(driver);
            message("数据库驱动加载完成。");
        } catch (ClassNotFoundException e) {
            error("找不到数据库驱动。");
            e.printStackTrace();
        }
    }

    /**
     * 建立数据库连接
     *
     * @param url      连接字符串
     * @param username 用户名
     * @param password 密码
     * @return 返回连接成功后的Connection对象
     */
    public static Connection getConnection(String url, String username, String password) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
            message("连接数据库成功。");
        } catch (SQLException e) {
            error("无法连接数据库。");
            e.printStackTrace();
        }
        return connection;
    }

    public static ResultSet select(Statement statement, String sql) {
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql);
            ResultSetMetaData meta_data = rs.getMetaData();//列名
            for (int i_col = 1; i_col <= meta_data.getColumnCount(); i_col++) {
                System.out.print(meta_data.getColumnLabel(i_col) + "   ");
            }
            System.out.println();
            while (rs.next()) {
                for (int i_col = 1; i_col <= meta_data.getColumnCount(); i_col++) {
                    System.out.print(rs.getString(i_col) + "  ");
                }
                System.out.println();
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("数据查询失败!");
        }
        return rs;
    }

    /**
     * @param statement
     * @param sql
     */
    public static void insert(Statement statement, String sql) {
        try {
            statement.clearBatch();
            statement.addBatch(sql);
            statement.executeBatch();
            message("数据插入成功!");
        } catch (SQLException e) {
            error("数据插入失败!");
            e.printStackTrace();
        }
    }

    /**
     * @param sql
     */
    public void insert(String sql) {
        try {
            statement.clearBatch();
            statement.addBatch(sql);
            statement.executeBatch();
            message("数据插入成功!");
        } catch (SQLException e) {
            error("数据插入失败!");
            e.printStackTrace();
        }
    }

    /**
     * @param sql
     */
    public void update(String sql) {
        try {
            statement.executeUpdate(sql);
            message("数据更新成功。");
        } catch (SQLException e) {
            error("数据更新失败。");
            e.printStackTrace();
        }
    }

    /**
     * @param statement
     * @param sql
     */
    public static void update(Statement statement, String sql) {
        try {
            statement.executeUpdate(sql);
            message("数据更新成功。");
        } catch (SQLException e) {
            error("数据更新失败。");
            e.printStackTrace();
        }
    }
}
