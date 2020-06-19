package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

        // jtable
        DefaultTableModel model = new DefaultTableModel();
        JTable TitlesTable = new JTable(model);
        TitlesTable.setName("TitlesTable");
        TitlesTable.setVisible(true);
        TitlesTable.setBounds(20,100,740,550);
        TitlesTable.setPreferredScrollableViewportSize(new Dimension(740,550));
        JScrollPane scrollPane = new JScrollPane(TitlesTable);
        add(TitlesTable);
        model.addColumn("URL");
        model.addColumn("Title");
        TitlesTable.disable();

        TableColumn column1 = TitlesTable.getTableHeader().getColumnModel().getColumn(0);
        column1.setHeaderValue("URL");
        TableColumn column2 = TitlesTable.getTableHeader().getColumnModel().getColumn(1);
        column2.setHeaderValue("Title");
        //add title lable
        JLabel TitleLabel = new JLabel();
        TitleLabel.setName("TitleLabel");
        TitleLabel.setBounds(100,60,500,60);
        TitleLabel.setVisible(true);
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
       // String Titleregex = "<title>.*<\\/title>";
        String Titleregex = "<title>.*<\\/title>";
        Pattern pattern = Pattern.compile(Titleregex);

        //Regular Expression for strings
        String linkRegex = "<a href=\"[\\w\\/:\\.]*\"";
        Pattern linkpattern = Pattern.compile(linkRegex);
        Map<String,String> titlelink = new LinkedHashMap<>();


        // RunButton execution function
        RunButton.addActionListener( e -> {
            List<String> listlink = new ArrayList<>();
            List<String> finallistlink = new ArrayList<>();
                     String url = UrlTextField.getText();/* Get url from JTextField */
                    int h = model.getRowCount();
                    for (int i = 0; i < h; i++){
                        model.removeRow(i);
                    }
                    System.out.println(url + "     this is the url");
                    if(!(url.contains(".")) && !(url.contains("/"))){
                     url = "http://localhost:2555/" + url;
                    }
                    URLConnection urlConnection = null;
                    try {
                        urlConnection = new URL(url).openConnection();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        System.out.println("Exception here");
                    }

                    if (urlConnection.getContentType().startsWith("text/html")) {

                        try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
                            String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Matcher mainlinkmatcher = pattern.matcher(siteText);
                            if (mainlinkmatcher.find()) {
                                int start = mainlinkmatcher.start();
                                int end = mainlinkmatcher.end();
                                String s = siteText.substring(start + 7, end - 8);
                                //System.out.println("grchaa!!!");
                                System.out.println("mainlinktitle: " + s);
                                model.addRow(new Object[]{url, s});
                                TitleLabel.setText(s);
                            }
                            Matcher linkmatcher = linkpattern.matcher(siteText);
                            while (linkmatcher.find()) {
                                //System.out.println("....");
                                int start = linkmatcher.start();
                                int end = linkmatcher.end();
                                //System.out.println(start + "  " + end);
                                String link = siteText.substring(start + 9, end - 1);
                                listlink.add(link);
                            }

                            System.out.println(listlink.toString() + "this is list" + listlink.size());

                            boolean relativelinkflag = true;
                            for (int i = 0; i < listlink.size(); i++) {
                                System.out.println(listlink.get(i));
                                String templink = "";

                                if (listlink.get(i).substring(0, 1).equals("/")) {
                                    if (url.substring(0, 5).equals("https")) {
                                        finallistlink.add("https:" + listlink.get(i));
                                        templink = "https:" + listlink.get(i);
                                    } else {
                                        finallistlink.add("http:" + listlink.get(i));
                                        templink = "http:" + listlink.get(i);
                                    }
                                } else if (listlink.get(i).substring(0, 2).equals("ht")) {
                                    finallistlink.add(listlink.get(i));
                                    templink = listlink.get(i);
                                } else if (listlink.get(i).contains("/")) {
                                    if (url.substring(0, 5).equals("https")) {
                                        finallistlink.add("https://" + listlink.get(i));
                                        templink = "https://" + listlink.get(i);
                                    } else {
                                        finallistlink.add("http://" + listlink.get(i));
                                        templink = "http://" + listlink.get(i);
                                    }
                                } else if (listlink.get(i).contains(".")) {
                                    String temp = url;
                                    int j = temp.length() - 1;
                                    while (temp.charAt(j) != '/') {
                                        j = j - 1;
                                    }

                                    finallistlink.add(temp.substring(0, j) + listlink.get(i));
                                    templink = temp.substring(0, j) + listlink.get(i);
                                } else {
                                    System.out.println("adding localhost");
                                    if (!(finallistlink.contains("http://localhost:25555/" + listlink.get(i)))) {
                                        finallistlink.add("http://localhost:25555/" + listlink.get(i));
                                    }
                                }
                                System.out.println("size" + finallistlink.size());
                            }
                            System.out.println(finallistlink.toString() + "finallistlink" + finallistlink.size());
                            for (int k = 0; k < finallistlink.size(); k++) {
                                //String title = "";
                                System.out.println("processing link" + k);
                                URLConnection urlConnection2 = null;
                                try {
                                    urlConnection2 = new URL(finallistlink.get(k)).openConnection();
                                    System.out.println(urlConnection2.getContentType().startsWith("text/html"));
                                    if (urlConnection2.getContentType().startsWith("text/html")) {
                                        InputStream inputStream2 = new BufferedInputStream(new URL(finallistlink.get(k)).openStream());
                                        String siteText2 = new String(inputStream2.readAllBytes(), StandardCharsets.UTF_8);
                                        Matcher matcher = pattern.matcher(siteText2);
                                        // title = siteText2.substring(siteText.indexOf("title")+7, siteText.indexOf("/title")-4);
                                        if (matcher.find()) {
                                            System.out.println("found title");
                                            int start = matcher.start();
                                            int end = matcher.end();
                                            String title = siteText2.substring(start+7 , end - 8);
                                            System.out.println(k + " " + title);
                                            titlelink.put(finallistlink.get(k), title);
                                            model.addRow(new Object[]{finallistlink.get(k), title});
                                        }
                                        //System.out.println(siteText2);
                                    }
                                } catch (Exception z) {
                                    z.printStackTrace();
                                    System.out.println("Exception here!!");
                                    //System.out.println(z.getMessage());
                                }


                            }


                        } catch (MalformedURLException malformedURLException) {
                            malformedURLException.printStackTrace();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        System.out.println(titlelink + "map");

                    }
                }
        );

    }
}