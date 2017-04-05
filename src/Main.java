import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        mainInterface();
    }

    /**
     * 主界面入口
     */
    public static void mainInterface() {

        createIPTxt();//检查是否存在ip.txt文件
        JFrame jFrame = new JFrame("登陆");
        jFrame.setSize(500, 280);
        setCenter(jFrame);//设置窗口在屏幕中央
        jFrame.setLayout(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel centerPanel = new JPanel();
        placeCenterPanel(centerPanel);
        centerPanel.setBounds(50, 100, 400, 150);
        //添加中央面板
        jFrame.add(centerPanel);
        JPanel leftPanel = new JPanel();
        placeWestPanel(leftPanel);
        leftPanel.setBounds(50, 30, 400, 100);
        //添加左侧面板
        jFrame.add(leftPanel);
        //设置窗口可见
        jFrame.setVisible(true);
        //
        jFrame.setResizable(false);
    }

    /**
     * 设置左侧jPabel面板中的组件
     */
    public static void placeWestPanel(JPanel jPanel) {
        //布局暂设置为null，以后根据需求更改
        jPanel.setLayout(null);
        JLabel jLabelIP = new JLabel("新增IP：");
        jLabelIP.setBounds(10, 10, 180, 25);
        // 创建 JLabel
        jPanel.add(jLabelIP);
        JTextField jTextField = new JTextField();
        jTextField.setBounds(100, 10, 180, 25);
        jPanel.add(jTextField);
        JButton jButton = new JButton("提交");
        jButton.setBounds(310, 10, 80, 25);
        jPanel.add(jButton);
        JLabel jLabel = new JLabel("登录IP:");
        jLabel.setBounds(10, 40, 80, 25);
        jPanel.add(jLabel);
        JComboBox jComboBox = new JComboBox();
        readTxtFile(jComboBox); //按行读取文件ip.txt
        jComboBox.setBounds(100, 40, 180, 25);
        jPanel.add(jComboBox);
        JButton jButtonDelete = new JButton("删除");
        jButtonDelete.setBounds(310, 40, 80, 25);
        jPanel.add(jButtonDelete);

        /**
         * 为删除按钮添加监听事件
         */
        jButtonDelete.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = jComboBox.getSelectedItem().toString();
                System.out.println(message);    //获得要删除的字符串
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("ip.txt")),
                            "UTF-8"));
                    StringBuffer stringBuffer = new StringBuffer();
                    String lineTxt = null;
                    while ((lineTxt = br.readLine()) != null) {
                        //读取ip.txt中内容过滤掉要删除的选项，再重新写入到ip.txt文件中去。
                        if (lineTxt.length() != 0) {
                            if (lineTxt.equals(message)){
                                continue;
                            }else{
                                stringBuffer.append(lineTxt).append( "\r\n");
                            }
                        }
                    }
                    br.close();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ip.txt"),
                            "UTF-8"));
                    bw.write(stringBuffer.toString());
                    bw.close();
                    jComboBox.removeAllItems();     //清空下拉列表
                    readTxtFile(jComboBox);         //重新加载下拉列表
                } catch (Exception ee) {
                    ee.printStackTrace();
                }

            }
        });
        /**
         * 为提交按钮添加监听事件
         */
        jButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = jTextField.getText();
                if (message.contains(";")) {
                    //如果字符串中包含英文分号，将英文分号替换成中文分号
                    message = message.replace(";", "；");
                } else if (!message.contains("；") && !isValidIP(message)) {
                    //如果字符串不包含中文分号，并且不是有效的ip地址
                    JOptionPane.showMessageDialog(null, "消息格式为：ip地址\n或者：ip地址；ip地址说明", "警告", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String ipAddress = message.split("\\；")[0];
                //System.out.println(ipAddress);

                if (ipAddress != "" && isValidIP(ipAddress)) {
                    writeTxtFile(message);      //写入到文件中
                    jComboBox.removeAllItems();
                    readTxtFile(jComboBox);     //按行读取文件ip.txt
                    jTextField.setText("");     //输入框置空
                    JOptionPane.showMessageDialog(new JFrame().getContentPane(),
                            "添加成功!", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "IP地址格式不正确！！", "警告", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * 设置中央jPanel面板中的组件
     *
     * @param jPanel
     */
    public static void placeCenterPanel(JPanel jPanel) {

        //布局暂设置为null，以后根据需求更改
        jPanel.setLayout(null);
        // 创建 JLabel
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setBounds(10, 20, 80, 25);
        jPanel.add(userLabel);

        // 创建文本域用于用户输入
        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 180, 25);
        jPanel.add(userText);
        // 输入密码的文本域
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setBounds(10, 50, 80, 25);
        jPanel.add(passwordLabel);
        /*
         *这个类似用于输入的文本域
         * 但是输入的信息会以点号代替，用于包含密码的安全性
         */
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 180, 25);
        jPanel.add(passwordText);
        //创建重置按钮
        JButton resetButton = new JButton("重置");
        resetButton.setBounds(310, 20, 80, 25);
        jPanel.add(resetButton);
        // 创建登录按钮
        JButton loginButton = new JButton("登陆");
        loginButton.setBounds(310, 50, 80, 25);
        jPanel.add(loginButton);
        /**
         * 为重置按钮设置监听事件
         */
        resetButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userText.setText("");
                passwordText.setText("");
            }
        });
    }

    /**
     * 将jFrame组件放置于电脑屏幕的中央
     *
     * @param jFrame
     */
    public static void setCenter(JFrame jFrame) {
        int windowWidth = jFrame.getWidth(); //获得窗口宽
        int windowHeight = jFrame.getHeight(); //获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); //定义工具包
        Dimension screenSize = kit.getScreenSize(); //获取屏幕的尺寸
        int screenWidth = screenSize.width; //获取屏幕的宽
        int screenHeight = screenSize.height; //获取屏幕的高
        jFrame.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);//设置窗口居中显示
    }

    /**
     * 创建ip.txt文件
     */
    public static void createIPTxt() {
        File fileName = new File("ip.txt");
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按行读取txt文件内容
     */
    public static void readTxtFile(JComboBox jComboBox) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("ip.txt")),
                    "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                if (lineTxt.length() != 0) {
                    //非空行
                    //System.out.println(lineTxt);
                    jComboBox.addItem(lineTxt);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写ip.txt文件
     */
    public static void writeTxtFile(String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ip.txt", true),
                    "UTF-8"));
            bw.write(message);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断ip地址是否合法
     *
     * @param ipAddress
     * @return
     */
    public static boolean isValidIP(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}
