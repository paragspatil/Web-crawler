package crawler;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        HtmlTextArea.setBounds(20,100,740,550);
        HtmlTextArea.setVisible(true);
        HtmlTextArea.setName("HtmlTextArea");
        HtmlTextArea.disable();
        add(HtmlTextArea);

        //add title lable
        JLabel TitleLabel = new JLabel();
        TitleLabel.setName("TitleLabel");
        TitleLabel.setBounds(100,60,500,60);
        TitleLabel.setVisible(true);
        //TitleLabel.setText("Title:");
        Font font = new Font("Courier", Font.BOLD,12);
        TitleLabel.setFont(font);
        TitleLabel.setFont(TitleLabel.getFont().deriveFont(16f));
        add(TitleLabel);


        //a lable for word "Title"
        JLabel Title = new JLabel();
        Title.setBounds(20,60,80,60);
        Title.setVisible(true);
        Title.setText("Title: ");
        Title.setFont(font);
        Title.setFont(Title.getFont().deriveFont(16f));
        add(Title);

        //Regular expression for extracting title;
        String Titleregex = "<title>.*<\\/title>";
        Pattern pattern = Pattern.compile(Titleregex);

        // RunButton execution function
        RunButton.addActionListener( e -> {
            final String url = UrlTextField.getText();/* Get url from JTextField */;
            try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
                String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                HtmlTextArea.setText(siteText);
                Matcher matcher = pattern.matcher(siteText);
                if(matcher.find()){
                    int start = matcher.start();
                    int end = matcher.end();
                    String Roughtitle = matcher.group();
                    String title = siteText.substring(start+7,end-8);
                    TitleLabel.setText(title);

                }
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

                }
        );

    }
}