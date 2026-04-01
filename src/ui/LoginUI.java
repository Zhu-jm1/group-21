package ui;
import service.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginUI extends JFrame {

    private static final Font CHINESE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 20);

    private JTextField userText;
    private JPasswordField passText;
    private JComboBox<String> roleBox;
    private JLabel msgLabel;

    private static final String[] ROLES = {"管理员 (Admin)", "教师 (MO)", "学生 (TA)"};
    private static final String[] ROLE_KEYS = {"admin", "mo", "ta"};

    public LoginUI() {
        UserService.init();  // 初始化数据文件
        initUI();
        // 关键：否则窗口不会显示出来
        setVisible(true);
    }

    private void initUI() {
        setTitle("BUPT TA 招聘系统 - 登录");
        setSize(450, 430);
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
        userText = new JTextField();
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
        passText = new JPasswordField();
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
        roleBox = new JComboBox<>(ROLES);
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
        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setFont(CHINESE_FONT);
        msgLabel.setForeground(Color.RED);
        msgLabel.setBounds(0, 315, 410, 20);

        // 悬停效果
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { loginBtn.setBackground(new Color(0, 66, 139)); }
            public void mouseExited(MouseEvent e) { loginBtn.setBackground(new Color(0, 86, 179)); }
        });
        registerBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { registerBtn.setBackground(new Color(240, 240, 255)); }
            public void mouseExited(MouseEvent e) { registerBtn.setBackground(Color.WHITE); }
        });

        // 登录逻辑
        loginBtn.addActionListener(e -> doLogin());

        // 注册逻辑：打开独立注册窗口
        registerBtn.addActionListener(e -> {
            new RegisterUI(this).showRegister();
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
    }

    // 执行登录
    private void doLogin() {
        String username = userText.getText().trim();
        String password = new String(passText.getPassword());
        String roleKey = ROLE_KEYS[roleBox.getSelectedIndex()];

        if (username.isEmpty()) {
            msgLabel.setText("请输入账号"); userText.requestFocus();
        } else if (password.isEmpty()) {
            msgLabel.setText("请输入密码"); passText.requestFocus();
        } else if (UserService.validate(username, password, roleKey)) {
            msgLabel.setForeground(new Color(0, 128, 0));
            msgLabel.setText("登录成功！欢迎 " + username);
            System.out.println("登录: " + username + " | 角色: " + roleKey);

            this.dispose();  // 关闭登录窗口

            if ("ta".equals(roleKey)) {
                new TAUI(username);  // TA 跳转到个人页面
            } else if ("admin".equals(roleKey)) {
                // 后续开发管理员页面
                JOptionPane.showMessageDialog(this, "管理员页面开发中...");
                new LoginUI();  // 暂时返回登录页
            } else {
                // MO 或其他角色
                JOptionPane.showMessageDialog(this, "该角色页面开发中...");
                new LoginUI();
            }
        } else {
            msgLabel.setForeground(Color.RED);
            msgLabel.setText("账号、密码或身份不匹配");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(LoginUI::new);
    }
}