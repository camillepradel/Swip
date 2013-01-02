package org.swip.utils;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
    
    public static void sendEmail(String subject, String message) {
        // send me an email
        // Common variables
        String host = "localhost";
        String from = "camille@camillepradel.fr";
        String to = "pradel@irit.fr";

        // Set properties
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.debug", "true");
//        props.put("mail.smtp.port", Integer.toString(465));


        // Get session
        Session session = Session.getInstance(props);

        try {
            // Instantiate a message
            Message msg = new MimeMessage(session);

            // Set the FROM message
            msg.setFrom(new InternetAddress(from));

            // The recipients can be more than one so we use an array but you can
            // use 'new InternetAddress(to)' for only one address.
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);

            // Set the message subject and date we sent it.
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // Set message content
            msg.setText(message);

            // Send the message
            Transport.send(msg);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    
}
