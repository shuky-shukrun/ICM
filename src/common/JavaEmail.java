package common;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaEmail
{
    Session mailSession;


    /**
     * Sets the mail server properties
     */
    public void setMailServerProperties()
    {
        Properties emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        mailSession = Session.getDefaultInstance(emailProperties, null);
    }
    /**
     * Prepare the email message to send
     * @param toEmails-who to send the mail
     * @param emailSubject-the subject of the mail
     * @param emailBody-the body of the mail
     * @return email message
     * @throws AddressException
     * @throws MessagingException
     */
    private MimeMessage draftEmailMessage( String toEmails,String emailSubject,String emailBody) throws AddressException, MessagingException
    {

        MimeMessage emailMessage = new MimeMessage(mailSession);
        /**
         * Set the mail recipients
         * */

        emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails));

        emailMessage.setSubject(emailSubject);
        /**
         * If sending HTML mail
         * */
        emailMessage.setContent(emailBody, "text/plain");
        /**
         * If sending only text mail
         * */
        //emailMessage.setText(emailBody);// for a text email
        return emailMessage;
    }
    /**
     * Send mail
     * @param toemails-who get the mail
     * @param emailSubject-the subject of the mail
     * @param emailBody-the body of the mail
     * @throws AddressException
     * @throws MessagingException
     */
    public void sendEmail(String toemails,String emailSubject,String emailBody) throws AddressException, MessagingException
    {
        /**
         * Sender's credentials
         * */
        String fromUser = "trsmaicm@gmail.com";
        String fromUserEmailPassword = "SMARTProject1!";

        String emailHost = "smtp.gmail.com";
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromUser, fromUserEmailPassword);
        /**
         * Draft the message
         * */
        MimeMessage emailMessage = draftEmailMessage(toemails,emailSubject,emailBody);
         /**
         * Send the mail
         * */
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        System.out.println("Email sent successfully.");
    }

}