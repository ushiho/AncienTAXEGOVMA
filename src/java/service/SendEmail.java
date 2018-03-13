package service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail extends Object {

    private static final SecureRandom random = new SecureRandom();
    private final String emailDGI = "xxxxxx@gmail.com";
    private final String passDGi = "xxxxxx";

    public String generatePassword(int len, String dic) {
        String result = "";
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(dic.length());
            result += dic.charAt(index);
        }
        return result;
    }

//    public void orginal() {
//
//        try {
//
//            Properties props = new Properties();
//            props.put("mail.smtp.host", "smtp.gmail.com"); // for gmail use smtp.gmail.com
//            props.put("mail.smtp.auth", "true");
//            props.put("mail.debug", "true");
//            props.put("mail.smtp.starttls.enable", "true");
//            props.put("mail.smtp.port", "465");
//            props.put("mail.smtp.socketFactory.port", "465");
//            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//            props.put("mail.smtp.socketFactory.fallback", "false");
//
//            Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
//
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication("hlotfi.hamza.lotfi@gmail.com", "motdepasse2020");
//                }
//            });
//
//            mailSession.setDebug(true); // Enable the debug mode
//
//            Message msg = new MimeMessage(mailSession);
//
//            //--[ Set the FROM, TO, DATE and SUBJECT fields
//            msg.setFrom(new InternetAddress("hlotfi.hamza.lotfi@gmail.com"));
//            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("lootfi.hamza@gmail.com"));
//            msg.setSentDate(new Date());
//            msg.setSubject("Hello ");
//
//            //--[ Create the body of the mail
//            msg.setText("Hello from my first e-mail sent with JavaMail");
//
//            //--[ Ask the Transport class to send our mail message
//            Transport.send(msg);
//
//            System.out.println(generatePassword(12, NUMERIC));
//        } catch (Exception E) {
//            System.out.println("Oops something has gone pearshaped!");
//            System.out.println(E);
//        }
//    }
    public int sendEmail(String receEmail, String subject, String contenu) {

        try {

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); // for gmail use smtp.gmail.com
            props.put("mail.smtp.auth", "true");
            props.put("mail.debug", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailDGI, passDGi);
                }
            });

            mailSession.setDebug(true); // Enable the debug mode

            Message msg = new MimeMessage(mailSession);

            //--[ Set the FROM, TO, DATE and SUBJECT fields
            msg.setFrom(new InternetAddress(emailDGI));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receEmail));
            msg.setSentDate(new Date());
            msg.setSubject(subject);

            //--[ Create the body of the mail
            msg.setText(contenu);

            //--[ Ask the Transport class to send our mail message
            Transport.send(msg);
            return 1;

        } catch (Exception E) {
            System.out.println(E);
            return -1;
        }
    }

}
