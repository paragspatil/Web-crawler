package crawler;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebCrawler extends JFrame {
    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setVisible(true);
        setLayout(null);
        setTitle("WebCrawler");
        initcomponents();
    }
    private void initcomponents(){
        //text filed for inputting URl
        JTextField UrlTextField = new JTextField();
        UrlTextField.setBounds(20,20, 500,30);
        //UrlTextField.setText("HTML code?");
        UrlTextField.setName("UrlTextField");
        UrlTextField.setLayout(null);
        //UrlTextField.setEditable(false);
        UrlTextField.setVisible(true);
        //UrlTextField.disable();
        add(UrlTextField);


        // Button to get URL and download page;
        JButton RunButton = new JButton();
        RunButton.setBounds(550,20,100,30);
        RunButton.setVisible(true);
        RunButton.setText("Get text!");
        RunButton.setName("RunButton");
        add(RunButton);


        //text area to show html
        JTextArea HtmlTextArea = new JTextArea();
        HtmlTextArea.setBounds(20,80,700,550);
        HtmlTextArea.setVisible(true);
        HtmlTextArea.setName("HtmlTextArea");
        HtmlTextArea.disable();
        add(HtmlTextArea);

        // RunButton execution function
        RunButton.addActionListener( e -> {
            final String url = UrlTextField.getText();/* Get url from JTextField */;
            try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
                String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                HtmlTextArea.setText(siteText);
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

                }
        );

    }
}