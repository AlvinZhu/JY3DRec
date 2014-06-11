package com.jiaying.jy3drec;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Photo {
    public Boolean result = false;
    public int length = 0;
    public File file = null;
}

class ServeThread implements Runnable {
    byte p_id[] = null;
    int num_pic = 0;
    Socket socket = null;
    int file_length = 0;
    int ret, count;
    Boolean result = false;

    public ServeThread(Socket client) {
        socket = client;
        new Thread(this).start();
    }

    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());

            p_id = new byte[18];
            input.readFully(p_id, 0, 18);

            num_pic = input.readInt();
            // num_pic = LittletoBig(input);
            Photo photo[] = new Photo[num_pic];
            System.out.println("照片数量：" + num_pic);
            for (int i = 0; i < num_pic; i++) {
                photo[i] = new Photo();
                photo[i].result = input.readBoolean();
                result |= photo[i].result;
                System.out.println(photo[i].result);
                photo[i].length = input.readInt();
                // photo[i].length = LittletoBig(input);
                System.out.println(photo[i].length);
                byte buffer[] = new byte[photo[i].length];
                input.readFully(buffer);
                String path = new String("D:/photo_" + i + ".jpg");
                System.out.println(path);
                photo[i].file = new File(path);
                DataOutputStream f_out = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(
                                photo[i].file)));
                f_out.write(buffer);
                f_out.flush();

                f_out.close();
            }

            if (result) {
                DataOutputStream output = new DataOutputStream(
                        socket.getOutputStream());
                File file = new File("D:/file_3d_recv");
                DataInputStream f_in = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(file)));
                file_length = (int) file.length();
//				byte buf[] = toLH(file_length);
                output.writeInt(file_length);
//				output.write(buf);
                System.out.println("3D文件大小：" + file_length);
                byte[] buffer = new byte[file_length];
                f_in.readFully(buffer);

                output.write(buffer);

                output.flush();
                System.out.println("发送3D文件完成");

                Boolean dResult = input.readBoolean();
                System.out.println(dResult);

                f_in.close();
                output.close();
            }
            input.close();

        } catch (IOException e) {
            System.out.println("服务器 run 异常: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    socket = null;
                    System.out.println("服务端 finally 异常:" + e.getMessage());
                }
            }
        }
    }
}

/**
 * Created by Alvin on 2014/6/4.
 */
public class Server {
    ServerSocket serverSocket = null;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket client = serverSocket.accept();
                new ServeThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }
}
