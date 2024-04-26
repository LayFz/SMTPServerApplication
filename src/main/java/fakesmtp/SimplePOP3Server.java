package fakesmtp;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import fakesmtp.core.ArgsHandler;
import fakesmtp.core.Configuration;
import fakesmtp.model.UIModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.System.out;

public class SimplePOP3Server {
    // windows 存储路径
    private static String filePath= ArgsHandler.INSTANCE.getOutputDirectory();

    // 用户名模拟
    private static String username;
    public static void main(String[] args) {
        if (filePath == null){
            filePath = Configuration.INSTANCE.get("emails.default.dir");
        }
        out.println(filePath);
        int port = 110; // POP3默认端口

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            out.println("POP3服务器已启动，监听端口 " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                out.println("新连接：" + clientSocket.getInetAddress().getHostAddress());

                // 处理客户端连接
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // 发送POP3服务器欢迎消息
            out.println("+OK Simple POP3 Server");

            String line;
            boolean isAuthenticated = false;

            while ((line = in.readLine()) != null) {
                System.out.println("客户端请求: " + line);

                if (line.toUpperCase().startsWith("QUIT")) {
                    // 退出命令
                    out.println("+OK Goodbye");
                    break;
                } else if (!isAuthenticated && line.toUpperCase().startsWith("USER")) {
                    // 用户名验证
                    username =line.substring(5,8);// 获取前三位
                    out.println("+OK User accepted");
                } else if (!isAuthenticated && line.toUpperCase().startsWith("PASS")) {
                    // 密码验证
                    // 生产环境上一般会与数据库连接校验，这里简单模拟已经登录
                    out.println("+OK Login successful");
                    isAuthenticated = true;
                } else if (isAuthenticated && line.toUpperCase().startsWith("RETR")) {
                    // 获取邮件
                    System.out.println(line.split(" ")[1]);
                    long messageNumber = Long.parseLong((line.split(" ")[1]));
                    if (messageNumber > 0) {
                        String emailContent = loadEmailContent(messageNumber);
                        if (emailContent != null) {
                            out.println("+OK " + emailContent.length() + " octets");
                            out.println(emailContent);
                            out.println("."); // 标志邮件结束
                        } else {
                            out.println("-ERR Message not found");
                        }
                    } else {
                        out.println("-ERR Invalid message number");
                    }
                } else if (isAuthenticated && line.toUpperCase().startsWith("LIST")){
                    // 使用文件系统或数据库查询以用户前三位字符开头的文件列表
                    File folder = new File(filePath); // 替换为存储邮件文件的实际路径
                    File[] matchingFiles = folder.listFiles((dir, name) -> name.startsWith(username) && name.endsWith(".eml"));

                    if (matchingFiles != null && matchingFiles.length > 0) {
                        StringBuilder emailList = new StringBuilder();
                        for (File file : matchingFiles) {
                            emailList.append(file.getName()).append("\n"); // 将文件名添加到邮件列表中
                        }

                        // 发送邮件列表给用户
                        out.println("+OK Here is your email list:");
                        out.println(emailList.toString());
                        out.println(".");
                    } else {
                        // 如果没有匹配的邮件文件，发送提示消息
                        out.println("+OK No emails found.");
                        out.println(".");
                    }

                }else if (isAuthenticated && line.toUpperCase().startsWith("DELE")) {
                    // 模拟获取邮件
                    long messageNumber = Long.parseLong((line.split(" ")[1]));
                    if (messageNumber > 0) {
                        Path emailFilePath = Paths.get(filePath +"\\"+ messageNumber +".eml");
                        System.out.println(emailFilePath);
                        if (Files.exists(emailFilePath)) {
                            try{
                                Files.delete(emailFilePath);
                                out.println("+OK File Delete");
                                return;
                            }catch (Exception e){
                                out.println("-ERR Unable to mark message as deleted");
                                e.printStackTrace();
                            }

                        }
                    } else {
                        out.println("-ERR Invalid message number");
                    }

                }

                else {
                    // 未识别的命令
                    out.println("-ERR Unrecognized command");
                }
            }

            // 关闭连接
            clientSocket.close();
            System.out.println("连接已关闭");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadEmailContent(long messageNumber) {
        // 从文件系统加载邮件内容
        // 假设邮件存储在类似"mail/user1/mail1.eml"的路径中

        String emailFilename = filePath + "\\"+ messageNumber + ".eml";
        try {
            Path emailFilePath = Paths.get(emailFilename);
            if (Files.exists(emailFilePath)) {
                byte[] emailBytes = Files.readAllBytes(emailFilePath);
                return new String(emailBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
