package com.example.test;
import android.content.Context;
import android.os.AsyncTask;
import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class JavaMailAPI extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String email, subject, message;
    private File fileStorageAccess;

    public JavaMailAPI(Context context, String email, String subject, String message, File fileStorageAccess) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.fileStorageAccess=fileStorageAccess;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        // Email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a Session object to authenticate the sender's email address and password
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Utils.senderEmail, Utils.senderPassword);
            }
        });
        // Create a new message
        MimeMessage emailMessage = new MimeMessage(session);
        try {

            // Set the recipient email address
            emailMessage.setFrom(new InternetAddress(Utils.senderEmail));
            emailMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            // Set the email subject
            emailMessage.setSubject(subject);

            // Create the email message body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            emailMessage.setText(message);

            // Create the email attachment
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(fileStorageAccess);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(fileStorageAccess.getName());

            // Add the message body and attachment to the email
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentBodyPart);

            // Set the email content
            emailMessage.setContent(multipart);

            // Send the email
            Transport.send(emailMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
