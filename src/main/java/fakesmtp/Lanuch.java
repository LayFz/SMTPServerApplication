package fakesmtp;

import fakesmtp.client.view.LoginDialog;
import fakesmtp.core.Configuration;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Lanuch {
    public static boolean isPortOpen(String host, int port) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 1000); // 1000ms超时
            socket.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static void main(String[] args) {
        FakeSMTP.main(args);
        String host = "localhost";
        int port = Integer.parseInt(Configuration.INSTANCE.get("smtp.default.port"));
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            boolean isPortUsed = isPortOpen(host, port);
            if (isPortUsed) {
                    Thread main1 = new Thread(() -> {
                        // 执行main1的任务
                        SimplePOP3Server.main(args);
                        System.out.println("SimplePOP3Server is running");
                    });

                    Thread main2 = new Thread(() -> {
                            LoginDialog.main(args);
                            // 执行main2的任务
                            System.out.println("LoginDialog 1 is running");
                    });
                Thread main3 = new Thread(() -> {
                    LoginDialog.main(args);
                    // 执行main2的任务
                    System.out.println("LoginDialog 2 is running");
                });
                    main1.start();
                    main2.start();
                    main3.start();
                executorService.shutdownNow(); // 关闭定时任务
            } else {
                System.out.println("请启动服务");
            }
        }, 0, 5, TimeUnit.SECONDS); // 每隔一秒执行一次
    }
}
