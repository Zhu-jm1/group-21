package ui;

import service.TAProfileService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class TAUI extends JFrame {

    private static final Font CHINESE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 18);

    private String currentUser;
    private Map<String, String> profile;

    // 个人信息标签（用于更新显示）
    private JLabel studentIdLabel;
    private JLabel majorLabel;
    private JLabel gradeLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel workHoursLabel;
    private JLabel skillsLabel;

    public TAUI(String username) {
        this.currentUser = username;
        TAProfileService.init();  // 初始化资料目录
        this.profile = TAProfileService.getProfile(username);  // 加载个人资料
        initUI();
    }

    private void initUI() {
        setTitle("TA 个人控制台 - BUPT TA 招聘系统");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 顶部标题栏
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 中间内容区
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(new Color(245, 245, 245));

        contentPanel.add(createPersonalInfoPanel());
        contentPanel.add(createApplicationStatusPanel());

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 底部：可申请职位
        JPanel bottomPanel = createAvailableJobsPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    // 顶部标题栏
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("TA 个人中心", SwingConstants.LEFT);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(0, 86, 179));

        JLabel welcome = new JLabel("欢迎，" + currentUser, SwingConstants.RIGHT);
        welcome.setFont(CHINESE_FONT);
        welcome.setForeground(Color.GRAY);

        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.setFont(CHINESE_FONT);
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(100, 30));
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要退出登录吗？", "确认退出",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginUI();
            }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(welcome);
        rightPanel.add(logoutBtn);

        panel.add(title, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    // 左侧：个人信息面板
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "个人信息",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        CHINESE_FONT,
                        new Color(0, 86, 179)
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // 检查资料是否完善
        boolean isComplete = TAProfileService.isProfileComplete(currentUser);

        if (!isComplete) {
            // 资料未完善，显示提示
            JLabel warningLabel = new JLabel("您的个人资料尚未完善", SwingConstants.CENTER);
            warningLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            warningLabel.setForeground(new Color(255, 193, 7));
            warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            warningLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
            panel.add(warningLabel);
        }

        // 个人信息行
        panel.add(createInfoRow("学号：", profile.get("studentId"), s -> studentIdLabel = s));
        panel.add(createInfoRow("专业：", profile.get("major"), s -> majorLabel = s));
        panel.add(createInfoRow("年级：", profile.get("grade"), s -> gradeLabel = s));
        panel.add(createInfoRow("邮箱：", profile.get("email"), s -> emailLabel = s));
        panel.add(createInfoRow("电话：", profile.get("phone"), s -> phoneLabel = s));
        panel.add(createInfoRow("可工作时间：", profile.get("workHours"), s -> workHoursLabel = s));
        panel.add(createInfoRow("擅长技能：", profile.get("skills"), s -> skillsLabel = s));

        panel.add(Box.createVerticalStrut(15));

        // 编辑按钮
        JButton editBtn = new JButton("编辑个人信息");
        editBtn.setFont(CHINESE_FONT);
        editBtn.setBackground(new Color(0, 86, 179));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setBorderPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.setMaximumSize(new Dimension(200, 35));
        editBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        editBtn.setMargin(new Insets(8, 20, 8, 20));
        editBtn.addActionListener(e -> showEditProfileDialog());

        panel.add(editBtn);

        return panel;
    }

    // 创建信息行
    private JPanel createInfoRow(String label, String value, java.util.function.Consumer<JLabel> labelRef) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(350, 30));

        JLabel lbl = new JLabel(label);
        lbl.setFont(CHINESE_FONT);
        lbl.setForeground(Color.GRAY);

        JLabel val = new JLabel(value.isEmpty() ? "未设置" : value);
        val.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        val.setForeground(value.isEmpty() ? Color.GRAY : new Color(0, 86, 179));

        labelRef.accept(val);

        row.add(lbl);
        row.add(val);
        return row;
    }

    // 编辑个人资料对话框
    private void showEditProfileDialog() {
        JDialog dialog = new JDialog(this, "编辑个人资料", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("填写个人信息", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(0, 86, 179));
        title.setBounds(0, 10, 370, 30);

        // 表单字段
        String[] labels = {"学号:", "专业:", "年级:", "邮箱:", "电话:", "可工作时间:", "擅长技能:"};
        String[] keys = {"studentId", "major", "grade", "email", "phone", "workHours", "skills"};
        JTextField[] fields = new JTextField[7];

        for (int i = 0; i < 7; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(CHINESE_FONT);
            lbl.setBounds(30, 55 + i * 50, 80, 25);

            fields[i] = new JTextField(profile.get(keys[i]));
            fields[i].setFont(CHINESE_FONT);
            fields[i].setBounds(120, 55 + i * 50, 260, 30);
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
            ));

            panel.add(lbl);
            panel.add(fields[i]);
        }

        JLabel msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setFont(CHINESE_FONT);
        msgLabel.setForeground(Color.RED);
        msgLabel.setBounds(0, 410, 370, 20);
        panel.add(msgLabel);

        JButton saveBtn = new JButton("保存");
        saveBtn.setFont(CHINESE_FONT);
        saveBtn.setBackground(new Color(0, 86, 179));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setBounds(120, 440, 200, 35);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        saveBtn.addActionListener(e -> {
            // 验证必填项
            if (fields[0].getText().trim().isEmpty()) {
                msgLabel.setText("学号不能为空");
                return;
            }
            if (fields[3].getText().trim().isEmpty()) {
                msgLabel.setText("邮箱不能为空");
                return;
            }

            // 保存资料
            Map<String, String> newProfile = new HashMap<>();
            newProfile.put("studentId", fields[0].getText().trim());
            newProfile.put("major", fields[1].getText().trim());
            newProfile.put("grade", fields[2].getText().trim());
            newProfile.put("email", fields[3].getText().trim());
            newProfile.put("phone", fields[4].getText().trim());
            newProfile.put("workHours", fields[5].getText().trim());
            newProfile.put("skills", fields[6].getText().trim());

            if (TAProfileService.saveProfile(currentUser, newProfile)) {
                msgLabel.setForeground(new Color(0, 128, 0));
                msgLabel.setText("✅ 保存成功！");

                // 更新当前显示
                studentIdLabel.setText(newProfile.get("studentId"));
                majorLabel.setText(newProfile.get("major"));
                gradeLabel.setText(newProfile.get("grade"));
                emailLabel.setText(newProfile.get("email"));
                phoneLabel.setText(newProfile.get("phone"));
                workHoursLabel.setText(newProfile.get("workHours"));
                skillsLabel.setText(newProfile.get("skills"));

                // 更新颜色
                for (JLabel lbl : new JLabel[]{studentIdLabel, majorLabel, gradeLabel,
                        emailLabel, phoneLabel, workHoursLabel, skillsLabel}) {
                    lbl.setForeground(new Color(0, 86, 179));
                }

                // 延迟关闭
                Timer timer = new Timer(800, ev -> dialog.dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                msgLabel.setText("保存失败");
            }
        });

        panel.add(saveBtn);
        panel.add(title);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // 右侧：申请状态面板（保持不变）
    private JPanel createApplicationStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "申请状态",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        CHINESE_FONT,
                        new Color(0, 86, 179)
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(3, 1, 0, 10));
        statsPanel.setBackground(Color.WHITE);

        String[] stats = {"已申请岗位：0 个", "面试邀请：0 个", "已录用：0 个"};
        Color[] colors = {new Color(255, 193, 7), new Color(23, 162, 184), new Color(40, 167, 69)};

        for (int i = 0; i < stats.length; i++) {
            JPanel statCard = new JPanel();
            statCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
            statCard.setBackground(colors[i]);
            statCard.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            JLabel statLabel = new JLabel(stats[i]);
            statLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            statLabel.setForeground(Color.WHITE);
            statCard.add(statLabel);
            statsPanel.add(statCard);
        }

        panel.add(statsPanel, BorderLayout.NORTH);
        return panel;
    }

    // 底部：可申请职位面板（保持不变）
    private JPanel createAvailableJobsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        "可申请职位",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        CHINESE_FONT,
                        new Color(0, 86, 179)
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        String[] columnNames = {"课程代码", "课程名称", "负责人", "招募人数", "状态", "操作"};
        String[][] data = {
                {"EBU6304", "Software Engineering", "Dr. Wang", "4", "正在招募", "申请"},
                {"EBU4201", "Database Systems", "Dr. Wu", "3", "紧急补录", "申请"}
        };

        JTable table = new JTable(data, columnNames);
        table.setFont(CHINESE_FONT);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}