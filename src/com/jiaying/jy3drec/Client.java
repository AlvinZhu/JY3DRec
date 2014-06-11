package com.jiaying.jy3drec;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

import static com.alvin.utilities.DebugManager.error;
import static com.alvin.utilities.DebugManager.message;

/**
 * @author Alvin Zhu
 * @version 1.1
 */
public class Client {

    String cfg_file = "config.properties";
    public File tmp_file = null;

    String server_ip = "192.168.1.150";
    int server_port = 4000;
    String database_driver = "com.mysql.jdbc.Driver";
    String database_url = "jdbc:mysql://192.168.44.131:3306/Jiaying";
    String username = "root";
    String password = "jiaying";

    public Client() {
        Properties prop = new Properties();
        InputStream config_in;

        config_in = Client.class.getClassLoader().getResourceAsStream(cfg_file);

        try {
            //tmp_file = File.createTempFile("JY3DModel", ".tmp");
            tmp_file = new File("D:/file_3d_recv1.pdf");

            if (config_in != null) {
                prop.load(config_in);
                server_ip = prop.getProperty("server_ip");
                server_port = Integer.parseInt(prop.getProperty("server_port"));
                database_driver = prop.getProperty("database_driver");
                database_url = prop.getProperty("database_url");
                username = prop.getProperty("username");
                password = prop.getProperty("password");
            }
        } catch (IOException e) {
            error("初始化失败。");
            error(e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void recvFile() {
        Socket socket = null;
        DataInputStream s_in = null;
        DataOutputStream f_out = null;

        int file_length;
        try {
            socket = new Socket(server_ip, server_port);

            s_in = new DataInputStream(socket.getInputStream());
            f_out = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(tmp_file)));

            file_length = s_in.readInt();
            byte[] buffer = new byte[file_length];
            s_in.readFully(buffer);
            f_out.write(buffer);

            f_out.flush();
        } catch (FileNotFoundException e) {
            error("打开临时文件失败。");
            error(e);
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            error("建立连接失败。");
            error(e);
            e.printStackTrace();
            System.exit(-1);
        } finally {

            try {
                if (socket != null) socket.close();
                if (s_in != null) s_in.close();
                if (f_out != null) f_out.close();
            } catch (IOException e) {
                error("关闭连接失败。");
                error(e);
                e.printStackTrace();
                System.exit(-1);
            }

        }
    }

    public void writeDatebase(int donor_id) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            Class.forName(database_driver);
            message("数据库驱动加载完成。");
        } catch (ClassNotFoundException e) {
            error("找不到数据库驱动。");
            e.printStackTrace();
        }

        try {
            // Create connection object
            connection = DriverManager.getConnection(database_url, username, password);

            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());

            //tmp_file.renameTo(new File(tmp_file.getPath(), donor_id+".pdf"));
            BufferedInputStream f_in1 = new BufferedInputStream(new FileInputStream(tmp_file));
            BufferedInputStream f_in2 = new BufferedInputStream(new FileInputStream(tmp_file));

            // Create Statement object
            String sql;
            sql = "INSERT INTO Donor(build_time, model_3d, id) VALUES(?,?,?) ON DUPLICATE KEY UPDATE build_time = ?, model_3d = ?";
            //sql = "UPDATE Donor SET build_time=?, model_3d = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);

            statement.setTimestamp(1, ts);
            statement.setBlob(2, f_in1);
            statement.setInt(3, donor_id);
            statement.setTimestamp(4, ts);
            statement.setBlob(5, f_in2);

            // Execute the query
            statement.executeUpdate();

        } catch (SQLException e) {
            error("数据库操作出错。");
            error(e);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            error("打开临时文件失败。");
            error(e);
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                error("关闭数据库连接失败。");
                error(e);
                e.printStackTrace();
            }
        }
    }
}
