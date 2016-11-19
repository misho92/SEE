package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Email {

	//send email notification
		public boolean sendEmail(String username, String pass){
			String to = null;
			try{
				URL url = new URL("http://messir.uni.lu:8085/jira/rest/api/2/user?username=" + username);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				String auth = username + ":" + pass;
				byte[] credentials = auth.getBytes(StandardCharsets.UTF_8);
				String encoded = Base64.getEncoder().encodeToString(credentials);
				conn.setRequestProperty("Authorization", "Basic " + encoded);
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String response = br.readLine();
				while (response != null) {
					to += response;
					response = br.readLine();
				}
				conn.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			to = to.split("emailAddress")[1].substring(3).split("\"")[0];
			System.out.println(to);
			boolean isSend = false;
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication("user","pass");
					}
				});
			session.setDebug(true);
			try {
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("matachev92@gmail.com"));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
				message.setSubject("Subject");
				message.setText("Message");
				Transport.send(message);
				System.out.println("Email sent!");
				isSend = true;
		      }catch (MessagingException mex) {
		    	  System.out.println("Email was not sent!");
		    	  Alert alert = new Alert(AlertType.ERROR);
	          	  alert.setTitle("Error Dialog");
	          	  alert.setHeaderText("Email notification not sent");
	          	  alert.setContentText("Email notification not sent");
	          	  alert.showAndWait();
		    	  mex.printStackTrace();
		      }
			return isSend;
		}
	
}
