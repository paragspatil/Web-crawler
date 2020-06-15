package crawler;

import javax.swing.*;

public class WebCrawler extends JFrame {
    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setVisible(true);
        setLayout(null);
        setTitle("simple window");
        initcomponents();
    }
    private void initcomponents(){
        JTextArea TextArea = new JTextArea();
        TextArea.setBounds(50,50, 200,200);
        TextArea.setText("HTML code?");
        TextArea.setName("TextArea");
        TextArea.setLayout(null);
        //TextArea.setEditable(false);
        TextArea.setVisible(true);
        TextArea.disable();
        add(TextArea);
    }
}