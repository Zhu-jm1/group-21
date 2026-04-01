package service;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class TAProfileService {

    // 存储路径：data/profiles/username.txt
    private static final String PROFILE_DIR = "data/profiles/";

    // 初始化：确保目录存在
    public static void init() {
        File dir = new File(PROFILE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("[系统] 个人资料目录已创建");
        }
    }

    // 获取 TA 个人资料
    public static Map<String, String> getProfile(String username) {
        File file = new File(PROFILE_DIR + username + ".txt");
        Map<String, String> profile = new HashMap<>();

        if (!file.exists()) {
            // 返回空资料（未设置）
            profile.put("studentId", "");
            profile.put("major", "");
            profile.put("grade", "");
            profile.put("email", "");
            profile.put("phone", "");
            profile.put("workHours", "");
            profile.put("skills", "");
            profile.put("isComplete", "false");
            return profile;
        }

        try {
            for (String line : Files.readAllLines(Paths.get(file.getPath()))) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    profile.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 检查资料是否完整
        if (!profile.containsKey("isComplete")) {
            profile.put("isComplete", "false");
        }

        return profile;
    }

    // 保存 TA 个人资料
    public static boolean saveProfile(String username, Map<String, String> profile) {
        File file = new File(PROFILE_DIR + username + ".txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("studentId=" + profile.get("studentId")); bw.newLine();
            bw.write("major=" + profile.get("major")); bw.newLine();
            bw.write("grade=" + profile.get("grade")); bw.newLine();
            bw.write("email=" + profile.get("email")); bw.newLine();
            bw.write("phone=" + profile.get("phone")); bw.newLine();
            bw.write("workHours=" + profile.get("workHours")); bw.newLine();
            bw.write("skills=" + profile.get("skills")); bw.newLine();
            bw.write("isComplete=true"); bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 检查资料是否已完善
    public static boolean isProfileComplete(String username) {
        Map<String, String> profile = getProfile(username);
        return "true".equals(profile.get("isComplete"));
    }
}