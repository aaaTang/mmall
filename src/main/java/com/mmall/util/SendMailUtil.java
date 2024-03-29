package com.mmall.util;

import com.mmall.pojo.MailBean;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SendMailUtil {

    public String toChinese(String text) {
        try {
            text = MimeUtility.encodeText(new String(text.getBytes(), "GB2312"), "GB2312", "B");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public boolean sendMail(MailBean mb) {
        String host = mb.getHost();
        final String username = mb.getUsername();
        final String password = mb.getPassword();
        String from = mb.getFrom();
        String to = mb.getTo();
        String subject = mb.getSubject();
        String content = mb.getContent();
        String fileName = mb.getFilename();
        Vector<String> file = mb.getFile();

        Properties props = System.getProperties();
        props.put("mail.smtp.host", host); // 设置SMTP的主机
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback","false");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true"); // 需要经过验证

        Session session = Session.getInstance(props, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress[] address = { new InternetAddress(to) };
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(toChinese(subject));
            msg.addRecipients(Message.RecipientType.CC,InternetAddress.parse(PropertiesUtil.getProperty("mail.form")));

            Multipart mp = new MimeMultipart();
            MimeBodyPart mbpContent = new MimeBodyPart();
            mbpContent.setText(content);
            mp.addBodyPart(mbpContent);

            /* 往邮件中添加附件 */
            if (file != null) {
                Enumeration<String> efile = file.elements();
                while (efile.hasMoreElements()) {
                    MimeBodyPart mbpFile = new MimeBodyPart();
                    fileName = efile.nextElement().toString();
                    FileDataSource fds = new FileDataSource(fileName);
                    mbpFile.setDataHandler(new DataHandler(fds));
                    mbpFile.setFileName(toChinese(fds.getName()));
                    mp.addBodyPart(mbpFile);
                }
                System.out.println("添加成功");
            }

            msg.setContent(mp);
            msg.setSentDate(new Date());
            Transport.send(msg);

        } catch (MessagingException me) {
            me.printStackTrace();
            return false;
        }
        return true;
    }

}
