import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginUI extends JFrame {

    private static final Font CHINESE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 20);

    public LoginUI() {
        // 初始化用户服务
        UserService.init();

        setTitle("BUPT TA 招聘系统 - 登录");
        setSize(450, 430);  // 高度+30，给注册按钮留空间
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("BUPT TA 招聘系统", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(0, 86, 179));
        titleLabel.setBounds(0, 20, 410, 40);

        // 副标题
        JLabel subTitleLabel = new JLabel("2026 春季学期人才招募", SwingConstants.CENTER);
        subTitleLabel.setFont(CHINESE_FONT);
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setBounds(0, 55, 410, 25);

        // 账号
        JLabel userLabel = new JLabel("账号:");
        userLabel.setFont(CHINESE_FONT);
        userLabel.setBounds(50, 100, 60, 25);
        JTextField userText = new JTextField();
        userText.setFont(CHINESE_FONT);
        userText.setBounds(120, 100, 260, 30);
        userText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        // 密码
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(CHINESE_FONT);
        passLabel.setBounds(50, 145, 60, 25);
        JPasswordField passText = new JPasswordField();
        passText.setFont(CHINESE_FONT);
        passText.setBounds(120, 145, 260, 30);
        passText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        // 身份
        JLabel roleLabel = new JLabel("身份:");
        roleLabel.setFont(CHINESE_FONT);
        roleLabel.setBounds(50, 190, 60, 25);
        String[] roles = {"管理员 (Admin)", "教师 (MO)", "学生 (TA)"};
        String[] roleKeys = {"admin", "mo", "ta"};  // 对应文件存储的 key
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setFont(CHINESE_FONT);
        roleBox.setBounds(120, 190, 260, 30);

        // 登录按钮
        JButton loginBtn = new JButton("登 录");
        loginBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        loginBtn.setBounds(120, 235, 260, 35);
        loginBtn.setBackground(new Color(0, 86, 179));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 注册按钮
        JButton registerBtn = new JButton("注 册");
        registerBtn.setFont(CHINESE_FONT);
        registerBtn.setBounds(120, 275, 260, 30);
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setForeground(new Color(0, 86, 179));
        registerBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 86, 179)));
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 提示信息
        JLabel msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setFont(CHINESE_FONT);
        msgLabel.setForeground(Color.RED);
        msgLabel.setBounds(0, 315, 410, 20);

        // 登录按钮悬停效果
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginBtn.setBackground(new Color(0, 66, 139)); }
            public void mouseExited(MouseEvent e) { loginBtn.setBackground(new Color(0, 86, 179)); }
        });

        // 注册按钮悬停效果
        registerBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { registerBtn.setBackground(new Color(240, 240, 255)); }
            public void mouseExited(MouseEvent e) { registerBtn.setBackground(Color.WHITE); }
        });

        // 登录逻辑
        loginBtn.addActionListener(e -> {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword());
            int roleIndex = roleBox.getSelectedIndex();
            String roleKey = roleKeys[roleIndex];

            if (username.isEmpty()) {
                msgLabel.setText("请输入账号"); userText.requestFocus();
            } else if (password.isEmpty()) {
                msgLabel.setText("请输入密码"); passText.requestFocus();
            } else if (UserService.validate(username, password, roleKey)) {
                msgLabel.setForeground(new Color(0, 128, 0));
                msgLabel.setText("登录成功！欢迎 " + username);
                System.out.println("登录: " + username + " | 角色: " + roleKey);
                // 后续：跳转对应页面
                // new AdminDashboard().setVisible(true); this.dispose();
            } else {
                msgLabel.setForeground(Color.RED);
                msgLabel.setText("账号、密码或身份不匹配");
            }
        });

        // 注册逻辑 - 弹出对话框
        registerBtn.addActionListener(e -> {
            showRegisterDialog();
        });

        // 添加组件
        mainPanel.add(titleLabel);
        mainPanel.add(subTitleLabel);
        mainPanel.add(userLabel);
        mainPanel.add(userText);
        mainPanel.add(passLabel);
        mainPanel.add(passText);
        mainPanel.add(roleLabel);
        mainPanel.add(roleBox);
        mainPanel.add(loginBtn);
        mainPanel.add(registerBtn);
        mainPanel.add(msgLabel);

        getRootPane().setDefaultButton(loginBtn);
        add(mainPanel);
        setVisible(true);
    }

    // 注册对话框
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "注册新账号", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("创建新账号", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(0, 86, 179));
        title.setBounds(0, 10, 320, 30);

        JLabel uLabel = new JLabel("账号:");
        uLabel.setFont(CHINESE_FONT);
        uLabel.setBounds(40, 55, 50, 25);
        JTextField uText = new JTextField();
        uText.setFont(CHINESE_FONT);
        uText.setBounds(100, 55, 220, 30);

        JLabel pLabel = new JLabel("密码:");
        pLabel.setFont(CHINESE_FONT);
        pLabel.setBounds(40, 95, 50, 25);
        JPasswordField pText = new JPasswordField();
        pText.setFont(CHINESE_FONT);
        pText.setBounds(100, 95, 220, 30);

        JLabel cLabel = new JLabel("确认:");
        cLabel.setFont(CHINESE_FONT);
        cLabel.setBounds(40, 135, 50, 25);
        JPasswordField cText = new JPasswordField();
        cText.setFont(CHINESE_FONT);
        cText.setBounds(100, 135, 220, 30);

        JLabel rLabel = new JLabel("身份:");
        rLabel.setFont(CHINESE_FONT);
        rLabel.setBounds(40, 175, 50, 25);
        String[] roles = {"管理员 (Admin)", "教师 (MO)", "学生 (TA)"};
        String[] roleKeys = {"admin", "mo", "ta"};
        JComboBox<String> rBox = new JComboBox<>(roles);
        rBox.setFont(CHINESE_FONT);
        rBox.setBounds(100, 175, 220, 30);

        JLabel msg = new JLabel("", SwingConstants.CENTER);
        msg.setFont(CHINESE_FONT);
        msg.setForeground(Color.RED);
        msg.setBounds(0, 210, 320, 20);

        JButton confirmBtn = new JButton("确认注册");
        confirmBtn.setFont(CHINESE_FONT);
        confirmBtn.setBounds(100, 240, 220, 30);
        confirmBtn.setBackground(new Color(0, 86, 179));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);

        confirmBtn.addActionListener(ev -> {
            String username = uText.getText().trim();
            String password = new String(pText.getPassword());
            String confirm = new String(cText.getPassword());
            String roleKey = roleKeys[rBox.getSelectedIndex()];

            if (username.isEmpty() || password.isEmpty()) {
                msg.setText("账号和密码不能为空");
            } else if (!password.equals(confirm)) {
                msg.setText("两次密码不一致");
            } else if (password.length() < 6) {
                msg.setText("密码至少 6 位");
            } else if (UserService.register(username, password, roleKey)) {
                msg.setForeground(new Color(0, 128, 0));
                msg.setText("注册成功！请登录");
                confirmBtn.setEnabled(false);
                dialog.dispose();
            } else {
                msg.setText("账号已存在");
            }
        });

        panel.add(title);
        panel.add(uLabel); panel.add(uText);
        panel.add(pLabel); panel.add(pText);
        panel.add(cLabel); panel.add(cText);
        panel.add(rLabel); panel.add(rBox);
        panel.add(msg);
        panel.add(confirmBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> new LoginUI());
    }
}