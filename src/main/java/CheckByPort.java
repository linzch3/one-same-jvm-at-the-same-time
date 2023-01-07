import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author linzecheng
 */
public class CheckByPort {

    public static String getCurrentJvmProcessPid() {
        // 样例：端口@IP，61244@192.168.10.58
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    public static void log(String msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTimeStr = simpleDateFormat.format(new Date());
        System.out.printf("pid:%s, time: %s, msg:%s%n", getCurrentJvmProcessPid(), currentTimeStr, msg);
    }

    public static boolean checkByPort(int port) {
        try {
            // 使用本地套接字ServerSocket来尝试打开端口
            ServerSocket ss = new ServerSocket(port);
            // 注册一个钩子，当JVM退出时也关闭端口
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    ss.close();
                    log("shutdown instance, close port ok");
                } catch (IOException e) {
                    log("shutdown instance, close port fail for: " + e.getMessage());
                }
            }));
            log("current instance can run");
            return true;
        } catch (IOException e) {
            log("current instance cannot run for other instance is running, err: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        boolean flag = checkByPort(8081);
        if (!flag) {
            log("exit current instance");
            return;
        } else {
            log("continue current instance");
        }
        Thread.sleep(5 * 1000L);
        log("instance run done");
    }

}
