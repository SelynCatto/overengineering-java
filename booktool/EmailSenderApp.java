import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class EmailSenderApp {
    private static final String USER_DATA_FILE = "userdata.properties";
    private static final String EMAIL_CONFIG_FILE = "config.properties";

    private static String username = "";
    private static String password = "";
    private static String recipientEmail = "";

    public static void main(String[] args) {
        loadConfigSettings();
        final JFrame frame = new JFrame("Taxi Verktyget");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JTextField fromTextField = new JTextField(20);
        JTextField toTextField = new JTextField(20);
        JTextField dateTextField = new JTextField(20);
        JTextField timeTextField = new JTextField(20);
        JCheckBox returnCheckBox = new JCheckBox("Ja/Nej");
        JTextField returnTime = new JTextField(20);
        JTextField passengersTextField = new JTextField(20);
        JTextField nameTextField = new JTextField(20);
        JTextField ssnTextField = new JTextField(20);
        JTextField phoneTextField = new JTextField(20);
        JCheckBox beNiceBox = new JCheckBox("Ja/Nej");

        loadUserData(nameTextField, ssnTextField, phoneTextField, fromTextField, toTextField);

        final String[] finalReturnAnswer = {""};

        JButton sendButton = new JButton("Skicka bokning");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String to = toTextField.getText();
                String from = fromTextField.getText();
                String subject = "Bokningsförfrågan";

                if (returnCheckBox.isSelected()) {
                    finalReturnAnswer[0] = "Ja";
                } else {
                    finalReturnAnswer[0] = "Nej";
                }

                if(beNiceBox.isSelected()) {
                    String message = "Hej, jag skulle vilja göra en bokning!\n\n\nTyp: När/Anropsstyrdtrafik\n"
                            + "Från: " + from + "\n"
                            + "Till: " + to + "\n"
                            + "Datum: " + dateTextField.getText() + "\n"
                            + "Tid: " + timeTextField.getText() + "\n"
                            + "Returresa: " + finalReturnAnswer[0] + "\n"
                            + "Returtid: " + returnTime.getText() + "\n"
                            + "Passagerare: " + passengersTextField.getText() + "\n"
                            + "Namn: " + nameTextField.getText() + "\n"
                            + "Kund nr: " + ssnTextField.getText() + "\n"
                            + "Telefon nr: " + phoneTextField.getText() + "\n\n\nMvh " + nameTextField.getText();

                    sendEmail(username, password, recipientEmail, to, subject, message);

                    saveUserData(nameTextField.getText(), ssnTextField.getText(), phoneTextField.getText(), from, to);
                } else {
                    String message = "Typ: När/Anropsstyrdtrafik\n"
                            + "Från: " + from + "\n"
                            + "Till: " + to + "\n"
                            + "Datum: " + dateTextField.getText() + "\n"
                            + "Tid: " + timeTextField.getText() + "\n"
                            + "Returresa: " + finalReturnAnswer[0] + "\n"
                            + "Returtid: " + returnTime.getText() + "\n"
                            + "Passagerare: " + passengersTextField.getText() + "\n"
                            + "Namn: " + nameTextField.getText() + "\n"
                            + "Kund nr: " + ssnTextField.getText() + "\n"
                            + "Telefon nr: " + phoneTextField.getText();

                    sendEmail(username, password, recipientEmail, to, subject, message);

                    saveUserData(nameTextField.getText(), ssnTextField.getText(), phoneTextField.getText(), from, to);
                }
            }
        });

        JButton openBrowserButton = new JButton("Se tider");
        openBrowserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.skanetrafiken.se/sok-resa/"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton configButton = new JButton("Konfigurera");
        configButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configureEmailSettings();
            }
        });

        JButton exitButton = new JButton("Stäng");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveUserData(nameTextField.getText(), ssnTextField.getText(), phoneTextField.getText(), fromTextField.getText(), toTextField.getText());
                System.exit(0);
            }
        });

        Container container = frame.getContentPane();
        container.setLayout(new GridLayout(16, 2));
        container.add(new JLabel("Från:"));
        container.add(fromTextField);
        container.add(new JLabel("Till:"));
        container.add(toTextField);
        container.add(new JLabel("Datum:"));
        container.add(dateTextField);
        container.add(new JLabel("Tid:"));
        container.add(timeTextField);
        container.add(new JLabel("Returresa:"));
        container.add(returnCheckBox);
        container.add(new JLabel("Returtid:"));
        container.add(returnTime);
        container.add(new JLabel("Passagerare:"));
        container.add(passengersTextField);
        container.add(new JLabel("Namn:"));
        container.add(nameTextField);
        container.add(new JLabel("Kund nr:"));
        container.add(ssnTextField);
        container.add(new JLabel("Telefon nr:"));
        container.add(phoneTextField);
        container.add(new JLabel("Trevlig:"));
        container.add(beNiceBox);
        container.add(sendButton);
        container.add(openBrowserButton);
        container.add(configButton);
        container.add(exitButton);

        frame.setVisible(true);
    }

    private static void sendEmail(String username, String password, String to, String from, String subject, String message) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(username));
            emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            Transport.send(emailMessage);
            System.out.println("Email sent successfully!");
            JOptionPane.showMessageDialog(null, "Bokning skickad", "Bekräftelse", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveUserData(String name, String ssn, String phone, String from, String to) {
        try (FileWriter writer = new FileWriter(USER_DATA_FILE)) {
            Properties properties = new Properties();
            properties.setProperty("name", name);
            properties.setProperty("ssn", ssn);
            properties.setProperty("phone", phone);
            properties.setProperty("from", from);
            properties.setProperty("to", to);
            properties.store(writer, "User Data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadUserData(JTextField nameTextField, JTextField ssnTextField, JTextField phoneTextField, JTextField fromTextField, JTextField toTextField) {
        try (FileReader reader = new FileReader(USER_DATA_FILE)) {
            Properties properties = new Properties();
            properties.load(reader);
            nameTextField.setText(properties.getProperty("name"));
            ssnTextField.setText(properties.getProperty("ssn"));
            phoneTextField.setText(properties.getProperty("phone"));
            fromTextField.setText(properties.getProperty("from"));
            toTextField.setText(properties.getProperty("to"));
        } catch (IOException e) {
            System.out.println("Well, something went wrong with loading user data.");
        }
    }

    private static void configureEmailSettings() {
        JFrame configFrame = new JFrame("Konfigurera E-post");
        configFrame.setSize(300, 200);
        configFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField emailField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField(password);
        JTextField recipientField = new JTextField(recipientEmail);
        JButton saveButton = new JButton("Spara");

        panel.add(new JLabel("E-post användarnamn:"));
        panel.add(emailField);
        panel.add(new JLabel("E-post lösenord:"));
        panel.add(passwordField);
        panel.add(new JLabel("Mottagarens E-post:"));
        panel.add(recipientField);
        panel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                username = emailField.getText();
                password = new String(passwordField.getPassword());
                recipientEmail = recipientField.getText();

                saveConfigSettings(username, password, recipientEmail);

                configFrame.dispose();
            }
        });

        configFrame.add(panel);
        configFrame.setVisible(true);
    }

    private static void saveConfigSettings(String username, String password, String recipientEmail) {
        try (FileWriter writer = new FileWriter(EMAIL_CONFIG_FILE)) {
            Properties properties = new Properties();
            properties.setProperty("username", username);
            properties.setProperty("password", password);
            properties.setProperty("recipientEmail", recipientEmail);
            properties.store(writer, "Email Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfigSettings() {
        try (FileReader reader = new FileReader(EMAIL_CONFIG_FILE)) {
            Properties properties = new Properties();
            properties.load(reader);
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            recipientEmail = properties.getProperty("recipientEmail");
        } catch (IOException e) {
            System.out.println("Well, something went wrong with loading configuration settings.");
        }
    }
}
