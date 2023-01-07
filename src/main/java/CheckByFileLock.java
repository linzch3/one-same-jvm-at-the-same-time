import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author linzecheng
 */
public class CheckByFileLock {

    public static String getCurrentJvmProcessPid() {
        // 样例：端口@IP，61244@192.168.10.58
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    public static void log(String msg) {
        System.out.printf("pid:%s, msg:%s%n", getCurrentJvmProcessPid(), msg);
    }

    public static boolean checkByFileLock(String lockFileName, byte[] lockFileContent) throws IOException {
        File lockFile = new File(lockFileName);
        try (RandomAccessFile file = new RandomAccessFile(lockFile, "rws")) {
            // 尝试获取锁
            FileLock lock = file.getChannel().tryLock();
            // 若获取失败，则说明有另外一个实例在允许
            if (lock == null) {
                log("other instance is " + new String(Files.readAllBytes(Paths.get(lockFileName))));
                return false;
            }
            // 若获取成功，那么当JVM退出时便自动删除
            lockFile.deleteOnExit();
            // 清空文件本来有的内容
            file.setLength(0);
            // 写入新的内容
            file.write(lockFileContent);
            // 加锁成功
            return true;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String lockFileName = "test_jvm.pid";
        boolean flag = checkByFileLock(lockFileName, getCurrentJvmProcessPid().getBytes(StandardCharsets.UTF_8));
        if (flag) {
            log("run instance start");
            Thread.sleep(5 * 1000L);
            log("run instance done");
        } else {
            log("cannot run for already have one instance, exit");
        }
    }

}
