import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // 数据文件路径（项目根目录下的 data/users.txt）
    private static final String FILE_PATH = "data/users.txt";

    // 初始化：确保文件存在
    public static void init() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
                // 创建默认管理员账号
                writeLine("admin,123456,admin");
                System.out.println("[系统] 默认管理员：admin / 123456");
            } catch (IOException e) {
                System.err.println("[错误] 无法创建数据文件");
            }
        }
    }

    // 验证登录
    public static boolean validate(String username, String password, String roleKey) {
        List<String> lines = readAllLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                if (parts[0].equals(username) && parts[1].equals(password) && parts[2].equals(roleKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 注册用户
    public static boolean register(String username, String password, String roleKey) {
        if (exists(username)) {
            return false; // 用户已存在
        }
        writeLine(username + "," + password + "," + roleKey);
        return true;
    }

    // 检查用户名是否存在
    private static boolean exists(String username) {
        for (String line : readAllLines()) {
            if (line.startsWith(username + ",")) {
                return true;
            }
        }
        return false;
    }

    // 读取所有行
    private static List<String> readAllLines() {
        try {
            return Files.readAllLines(Paths.get(FILE_PATH));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // 写入一行（追加模式）
    private static void writeLine(String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.newLine();
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}