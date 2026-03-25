package ui;
import service.UserService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterUI extends JDialog {

    private static final Font CHINESE_FONT = new Font("Microsoft YaHei", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 20);

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JComboBox<String> roleBox;
    private JLabel msgLabel;
    private JButton confirmBtn;

    private static final String[] ROLES = {"管理员 (Admin)", "教师 (MO)", "学生 (TA)"};
    private static final String[] ROLE_KEYS = {"admin", "mo", "ta"};

    public RegisterUI(Frame owner) {
        super(owner, "注册新账号", true);
        initUI();
    }

    private void initUI() {
        setSize(400, 340);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel title = new JLabel("创建新账号", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(0, 86, 179));
        title.setBounds(0, 10, 320, 30);

        // 账号
        JLabel uLabel = new JLabel("账号:");
        uLabel.setFont(CHINESE_FONT);
        uLabel.setBounds(40, 55, 50, 25);
        usernameField = new JTextField();
        usernameField.setFont(CHINESE_FONT);
        usernameField.setBounds(100, 55, 220, 30);

        // 密码
        JLabel pLabel = new JLabel("密码:");
        pLabel.setFont(CHINESE_FONT);
        pLabel.setBounds(40, 95, 50, 25);
        passwordField = new JPasswordField();
        passwordField.setFont(CHINESE_FONT);
        passwordField.setBounds(100, 95, 220, 30);

        // 确认密码
        JLabel cLabel = new JLabel("确认:");
        cLabel.setFont(CHINESE_FONT);
        cLabel.setBounds(40, 135, 50, 25);
        confirmField = new JPasswordField();
        confirmField.setFont(CHINESE_FONT);
        confirmField.setBounds(100, 135, 220, 30);

        // 身份
        JLabel rLabel = new JLabel("身份:");
        rLabel.setFont(CHINESE_FONT);
        rLabel.setBounds(40, 175, 50, 25);
        roleBox = new JComboBox<>(ROLES);
        roleBox.setFont(CHINESE_FONT);
        roleBox.setBounds(100, 175, 220, 30);

        // 提示信息
        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setFont(CHINESE_FONT);
        msgLabel.setForeground(Color.RED);
        msgLabel.setBounds(0, 210, 320, 20);

        // 确认按钮
        confirmBtn = new JButton("确认注册");
        confirmBtn.setFont(CHINESE_FONT);
        confirmBtn.setBounds(100, 240, 220, 30);
        confirmBtn.setBackground(new Color(0, 86, 179));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 按钮事件
        confirmBtn.addActionListener(e -> doRegister());

        // 添加组件
        panel.add(title);
        panel.add(uLabel); panel.add(usernameField);
        panel.add(pLabel); panel.add(passwordField);
        panel.add(cLabel); panel.add(confirmField);
        panel.add(rLabel); panel.add(roleBox);
        panel.add(msgLabel);
        panel.add(confirmBtn);

        add(panel);
    }

    // 执行注册逻辑
    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String roleKey = ROLE_KEYS[roleBox.getSelectedIndex()];

        if (username.isEmpty() || password.isEmpty()) {
            msgLabel.setText("账号和密码不能为空");
        } else if (!password.equals(confirm)) {
            msgLabel.setText("两次密码不一致");
        } else if (password.length() < 6) {
            msgLabel.setText("密码至少 6 位");
        } else if (UserService.register(username, password, roleKey)) {
            msgLabel.setForeground(new Color(0, 128, 0));
            msgLabel.setText("注册成功！");
            confirmBtn.setEnabled(false);
            // 延迟关闭，让用户看到成功提示
            Timer timer = new Timer(800, e -> dispose());
            timer.setRepeats(false);
            timer.start();
        } else {
            msgLabel.setText("账号已存在");
        }
    }

    // 外部调用：显示注册窗口
    public void showRegister() {
        // 重置状态
        usernameField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        msgLabel.setText("");
        msgLabel.setForeground(Color.RED);
        confirmBtn.setEnabled(true);
        setVisible(true);
    }
}