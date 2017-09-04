package com.github.guhaibin.utils.mail;

import com.github.guhaibin.api.Config;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发送邮件的简单封装
 * @author bean
 * @version 1.0
 */
public class MailSender {

	private static final Logger LOG = LoggerFactory.getLogger(MailSender.class);
	
	private static final String HTML_MIME_TYPE = "text/html";
	
	private static final String FROM = Config.MailConf.from;
	private static final String USERNAME = Config.MailConf.username;
	private static final String PWD = Config.MailConf.pwd;
	
	private static final Properties prop = new Properties();
	private static final Session session;
	static {
		
		prop.setProperty("mail.smtp.auth", "true");
		prop.put("mail.smtp.starttls.enable", "false");
	    prop.put("mail.smtp.host", Config.MailConf.host);
	    prop.put("mail.smtp.port", Config.MailConf.port);
	    
	    session = Session.getInstance(prop, new Authenticator() {
	    	protected PasswordAuthentication getPasswordAuthentication() {
	               return new PasswordAuthentication(USERNAME, PWD);
		   }
		});
	    
	}
	
	public static void send(Email mail){
		
		if(mail.getTo() == null || mail.getTo().isEmpty()){
			LOG.error("to users can not be null");
			return;
		}
		if(StringUtils.isBlank(mail.getSubject())){
			LOG.error("subject can not be empty");
			return;
		}
		
		String toList = mail.getTo().stream().reduce((t1,  t2) -> t1 = t1 + "," + t2).orElse("");
		try{
			
			Message message = new MimeMessage(session);
			
			message.setSubject(mail.getSubject());
			message.setFrom(new InternetAddress(FROM));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toList));
			
			if(mail.getCc() != null && !mail.getCc().isEmpty()){
				String ccList = mail.getCc().stream().reduce((t1,  t2) -> t1 = t1 + "," + t2).orElse("");
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccList));
			}
			
			message.setContent(mail.getContent(), HTML_MIME_TYPE);
			
			Transport.send(message);
			
		}catch(Exception e){
			
			LOG.error("send mail error. mail subject is '{}', mail to -> '{}' ", mail.getSubject(), toList, e);
			
		}
		
	}
	
}
