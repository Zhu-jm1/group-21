import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginUI extends JFrame {

    // 定义中文字体
    private static final Font CHINESE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 20);

    public LoginUI() {
        setTitle("BUPT TA 招聘系统 - 登录");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 主面板 - 白色背景
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("BUPT TA 招聘系统", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(0, 86, 179));
        titleLabel.setBounds(0, 30, 410, 40);

        // 副标题
        JLabel subTitleLabel = new JLabel("2026 春季学期人才招募", SwingConstants.CENTER);
        subTitleLabel.setFont(CHINESE_FONT);
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setBounds(0, 70, 410, 25);

        // 账号标签
        JLabel userLabel = new JLabel("账号:");
        userLabel.setFont(CHINESE_FONT);
        userLabel.setBounds(50, 120, 60, 25);

        // 账号输入框
        JTextField userText = new JTextField();
        userText.setFont(CHINESE_FONT);
        userText.setBounds(120, 120, 260, 30);
        userText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        // 密码标签
        JLabel passLabel = new JLabel("密码:");
        passLabel.setFont(CHINESE_FONT);
        passLabel.setBounds(50, 170, 60, 25);

        // 密码输入框
        JPasswordField passText = new JPasswordField();
        passText.setFont(CHINESE_FONT);
        passText.setBounds(120, 170, 260, 30);
        passText.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        // 身份选择标签
        JLabel roleLabel = new JLabel("身份:");
        roleLabel.setFont(CHINESE_FONT);
        roleLabel.setBounds(50, 220, 60, 25);

        // 身份下拉框
        String[] roles = {"管理员 (Admin)", "教师 (MO)", "学生 (TA)"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setFont(CHINESE_FONT);
        roleBox.setBounds(120, 220, 260, 30);
        roleBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 登录按钮
        JButton loginBtn = new JButton("登 录");
        loginBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        loginBtn.setBounds(120, 270, 260, 40);
        loginBtn.setBackground(new Color(0, 86, 179));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 鼠标悬停效果
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginBtn.setBackground(new Color(0, 66, 139));
            }
            public void mouseExited(MouseEvent evt) {
                loginBtn.setBackground(new Color(0, 86, 179));
            }
        });

        // 提示信息标签
        JLabel msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setFont(CHINESE_FONT);
        msgLabel.setForeground(Color.RED);
        msgLabel.setBounds(0, 320, 410, 20);

        // 添加所有组件到面板
        mainPanel.add(titleLabel);
        mainPanel.add(subTitleLabel);
        mainPanel.add(userLabel);
        mainPanel.add(userText);
        mainPanel.add(passLabel);
        mainPanel.add(passText);
        mainPanel.add(roleLabel);
        mainPanel.add(roleBox);
        mainPanel.add(loginBtn);
        mainPanel.add(msgLabel);

        // 登录按钮事件
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText().trim();
                String password = new String(passText.getPassword());
                String role = (String) roleBox.getSelectedItem();

                if (username.isEmpty()) {
                    msgLabel.setText("请输入账号");
                    userText.requestFocus();
                } else if (password.isEmpty()) {
                    msgLabel.setText("请输入密码");
                    passText.requestFocus();
                } else {
                    // 这里后续会调用 UserService 验证
                    msgLabel.setForeground(new Color(0, 128, 0));
                    msgLabel.setText("登录成功！欢迎 " + username);
                    System.out.println("登录信息 - 账号：" + username + ", 身份：" + role);
                }
            }
        });

        // 回车键支持
        getRootPane().setDefaultButton(loginBtn);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 在事件调度线程中创建并显示 UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginUI();
            }
        });
    }
}