package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private JTextArea htmlTextArea;
    private JTextField urlTextField;
    private JButton runButton;
    private JLabel titleLabel;
    private JTable titlesTable;
    private JTextField ExportUrlTextField;
    final private String[] titlesTableHeader = new String[] {"URL", "Title"};
    Map<String,String> LinkTitle = new LinkedHashMap<>();

    public WebCrawler() {
        super("Web Crawler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel contents = new JPanel();
        JPanel panelTop = new JPanel();
        JPanel panelLabel = new JPanel();
        JPanel panelContents = new JPanel();
        JPanel panelbottom = new JPanel();
        JButton exportButton = new JButton();
        JLabel exportlabel = new JLabel();
        urlTextField = getUrlTextField();
        htmlTextArea = getHtmlTextArea();
        runButton = getRunButton();
        titleLabel = getTitleLabel();
        exportButton = getExportButton();
        ExportUrlTextField = ExportUrlTextField();
        exportlabel = exportLabel();
        panelTop.add(urlTextField);
        panelTop.add(runButton);
        titlesTable = getTitlesTable();
        titlesTable.setEnabled(false);
        panelLabel.add(titleLabel);
        htmlTextArea.setVisible(false);
        panelContents.add(htmlTextArea);
        panelContents.add(new JScrollPane(titlesTable), BorderLayout.WEST);
        contents.setLayout(new BoxLayout(contents, BoxLayout.Y_AXIS));
        contents.add(panelTop);
        contents.add(panelLabel);
        contents.add(panelContents);
        contents.add(exportButton);
        //contents.add(exportlabel);
        //contents.add(ExportUrlTextField);
        panelbottom.add(exportlabel);
        contents.add(ExportUrlTextField);
        contents.add(panelbottom);
        setContentPane(contents);

        setSize(560, 700);
        setVisible(true);
    }

    private JLabel getTitleLabel() {
        JLabel titleLabel = new JLabel("Title: ");
        titleLabel.setName("TitleLabel");
        return titleLabel;
    }

    private JTable getTitlesTable() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("URL");
        model.addColumn("Title");
        JTable titlesTable = new JTable(model);
        titlesTable.setName("TitlesTable");
        titlesTable.setSize(540, 600);
        return titlesTable;
    }

    private JTextArea getHtmlTextArea() {
        JTextArea textArea = new JTextArea("HTML code?", 38, 47);
        textArea.setName("HtmlTextArea");
        textArea.setVisible(true);
        textArea.setEnabled(false);
        textArea.setLineWrap(true);
        return textArea;
    }

    private JTextField getUrlTextField() {
        JTextField textField = new JTextField(40);
        textField.setName("UrlTextField");
        return textField;
    }

    private JButton getRunButton() {
        JButton button = new JButton("Get text!");
        button.setSize( 100, 25);
        button.setName("RunButton");
        button.addActionListener(e -> {
            getLinks();
        });
        return button;
    }
    private  JButton getExportButton(){
        JButton ExportButton = new JButton("Save");
        ExportButton.setName("ExportButton");
        ExportButton.setLocation(400,550);
        ExportButton.setSize(100,25);
        ExportButton.addActionListener(e ->{
            String filename = ExportUrlTextField.getText();
            File file = new File(filename);

            try (PrintWriter printWriter = new PrintWriter(file)) {
                for(var entry:LinkTitle.entrySet()){
                    printWriter.println(entry.getKey());
                    printWriter.println(entry.getValue());
                    //printWriter.println("...........");
                }
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        return ExportButton;
    }
    private  JTextField ExportUrlTextField(){
        JTextField ExportUrlTextField = new JTextField();
        ExportUrlTextField.setName("ExportUrlTextField");
        ExportUrlTextField.setVisible(true);
        ExportUrlTextField.setLocation(100,500);
        ExportUrlTextField.setSize(300,25);

        return ExportUrlTextField;
    }

    private  JLabel exportLabel(){
        JLabel exportLabel = new JLabel("Export");
        exportLabel.setVisible(true);
        exportLabel.setLocation(10,500);
        exportLabel.setSize(80,25);
        exportLabel.setHorizontalAlignment(JLabel.LEFT);
        return  exportLabel;
    }


    private void parseHtml() {
        try {
            final String url = urlTextField.getText();
            final InputStream inputStream = new URL(url).openStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final StringBuilder stringBuilder = new StringBuilder();
            final String LINE_SEPARATOR = System.getProperty("line.separator");

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                stringBuilder.append(nextLine);
                stringBuilder.append(LINE_SEPARATOR);
            }
            final String siteText = stringBuilder.toString();
            Matcher matcher = Pattern.compile("(<title[\\w=\\-\"]*>)([\\w\\s\\-\"]*)(</title>)").matcher(siteText);
            if (matcher.find()) {
                titleLabel.setText(matcher.group(2));
            }
            htmlTextArea.setText(siteText);
        } catch (Exception exception) {
            htmlTextArea.setText(exception.getMessage());
        }
    }

    private void getLinks() {

        try {
            DefaultTableModel dtm = (DefaultTableModel) titlesTable.getModel();
            /*for(int i = 0;i < dtm.getRowCount();i++){
                dtm.removeRow(i);
            }*/
            parseHtml();
            Pattern patternTag = Pattern.compile("(<a.*?href=[\"'])(.*?)([\"'].*?>)(.*?)(</a>)");
            Matcher matcherTag = patternTag.matcher(htmlTextArea.getText());
            Pattern patternBaseUrl = Pattern.compile("(https?://)([\\w.-]+)(.*?)(/?)([^/]*)");
            Pattern patternNormalUrl = Pattern.compile("https?://.*?");
            Pattern patternRelativeUrl = Pattern.compile("(/?)([\\w.%/-]+)");
            URL url;
            URLConnection connection;
            Matcher matcherUrl;
            String baseUrl;
            String currentUrl;
            String urlString;
            Map<String, String> mapData = new TreeMap<>();
            Matcher matcherBaseUrl = patternBaseUrl.matcher(urlTextField.getText());
            if (matcherBaseUrl.matches()) {
                baseUrl = matcherBaseUrl.group(1) + matcherBaseUrl.group(2);
                currentUrl = matcherBaseUrl.group(1) + matcherBaseUrl.group(2) + matcherBaseUrl.group(3) + matcherBaseUrl.group(4);
            } else {
                baseUrl = "http://localhost";
                currentUrl = "http://localhost/";
            }
            mapData.put(urlTextField.getText(), titleLabel.getText());
            while (matcherTag.find()) {
                if (patternNormalUrl.matcher(matcherTag.group(2)).matches()) {
                    url = new URL(matcherTag.group(2));
                    connection = url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
                    if (connection.getContentType() != null) {
                        if (connection.getContentType().equals("text/html")) {
                            mapData.put(matcherTag.group(2), findTitleInUrl(connection));
                        }
                    }
                } else {
                    matcherUrl = patternRelativeUrl.matcher(matcherTag.group(2));
                    if (matcherUrl.matches()) {
                        if (matcherUrl.group(1).length() == 1) {
                            urlString = baseUrl + matcherUrl.group(0);
                        } else {
                            urlString = currentUrl + matcherUrl.group(0);
                        }
                        url = new URL(urlString);
                        connection = url.openConnection();
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
                        if (connection.getContentType() != null) {
                            if (connection.getContentType().equals("text/html")) {
                                mapData.put(urlString, findTitleInUrl(connection));
                            }
                        }
                    }
                }
            }
            for(var entry:LinkTitle.entrySet()){
                LinkTitle.remove(entry.getKey(),entry.getValue());
            }
            String[][] tableData = new String[mapData.size()][];
            int i = 0;
            for (Map.Entry<String, String> entry : mapData.entrySet()) {
                tableData[i] = new String[] { entry.getKey(), entry.getValue() };
                LinkTitle.put(entry.getKey(),entry.getValue());
                i++;
            }
           // System.out.println(LinkTitle);
            if (i > 0) {
                titlesTable.setModel(new DefaultTableModel(tableData, titlesTableHeader));
                titlesTable.setEnabled(true);
            }
        } catch (Exception exception) {
            htmlTextArea.setText(exception.getMessage());
        }
    }

    private String findTitleInUrl(URLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        String nextLine;
        StringBuilder stringBuilder = new StringBuilder();
        while ((nextLine = reader.readLine()) != null) {
            stringBuilder.append(nextLine);
            stringBuilder.append(LINE_SEPARATOR);
        }
        Pattern patternTitle = Pattern.compile("(<title[\\w=\\-\"]*>)(.*?)(</title>)");
        Matcher matcherTitle = patternTitle.matcher(stringBuilder);
        if (matcherTitle.find()) {
            return matcherTitle.group(2);
        }
        return "No title";
    }
}