package projekt.zespolowy.dziura.Mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Klasa sluzaca do wysylania wiadomosci email bez korzystania z aplikacji zewnetrznych.
 * W ramach jej dzialania nastepuje zalogowanie na poczte zgodnie z podanymi na stale
 * danymi uzytkownika, wyslanie maila, a nastepnie wyczyszczenie wiadomosci wyslanych oraz
 * folderu kosza na poczcie. Dziedziczy po klasie abstrakcyjnej {@link javax.mail.authenticator}, aby
 * umozliwic weryfikacje danych sluzacych do logowania na poczte.
 * <br>
 * Klasa ta wprowadzila koniecznosc dolaczenia dodatkowych plikow <i>*.jar</i>, przez co aplikacja zwiekszyla swoj rozmiar.
 */
public class MailSender extends Authenticator  {

	private final String smtpHost = "smtp.gmail.com";
	private final String username = "dziura.android@gmail.com";
	private final String password = "dziura@1";
	private final String from = "dziura.android";
	private final String to = "dziura.android@gmail.com";
	private final String gmailImap = "imap.gmail.com";
	private final String gmailTrashFolder = "[Gmail]/Kosz";
	private final int portSSL = 465;
	private final String subject = "Zg³oœ dziurê: zg³oszenie szkody";
	private final String body = "Zg³oszenie szkody z aplikacji Zg³oœ dziurê. Wszystkie" +
			" dane dotycz¹ce zg³oszenia zapisane s¹ w za³¹czniku."; 
	private Multipart multipart;
	
	private Folder trash;
	 
	 
	/**
	 * Konstruktor. Inicjalizuje obiekt  {@link javax.mail.internet.MimeMultipart}.
	 */
	public MailSender()
	{
	    multipart = new MimeMultipart(); 
	  
	    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
	    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
	    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
	    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
	    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
	    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
	    CommandMap.setDefaultCommandMap(mc); 
	}
	 
	/**
	 * Funkcja wysyla wiadomosc email.
	 * @return <li>true - w przypadku pomyslnego wyslania wiadomosci
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public boolean send() throws AddressException, MessagingException 
	{ 
		Properties props = setProperties();
		Session session = Session.getInstance(props, this); 
		MimeMessage msg = new MimeMessage(session); 
		msg.setFrom(new InternetAddress(from)); 
		InternetAddress addressTo = new InternetAddress(to); 
		msg.setRecipient(MimeMessage.RecipientType.TO, addressTo);
		msg.setSubject(subject);
		msg.setSentDate(new Date()); 
		
      // 	setup message body 
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(body); 
		multipart.addBodyPart(messageBodyPart); 
		
      // 	Put parts in message 
		msg.setContent(multipart);
		
      // 	send email 
		Transport transport = session.getTransport("smtps");
		transport.connect(smtpHost, portSSL, username, password);
		transport.sendMessage(msg,  msg.getAllRecipients());
		transport.close(); 
		
		trash();
		
		return true; 
	}
	
	/**
	 * Zalogowanie na poczte i usuniecie wszystkich wiadomosci znajdujacych sie w koszu.
	 * @throws MessagingException
	 */
	public void trash() throws MessagingException
	{
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);
		Store store = session.getStore("imaps");
		store.connect(gmailImap, username, password);

		trash = store.getFolder(gmailTrashFolder);
		trash.open(Folder.READ_WRITE);
		Message msgTrash[] = trash.getMessages();
		for (int a = 0; a < msgTrash.length; a++)
		{
			msgTrash[a].setFlag(Flags.Flag.DELETED, true);
		}	
		//trash.close(true);
	}
	 
	/**
	 * Metoda weryfikujaca prawidlowosc loginu i hasla do poczty.
	 */
	 /* (non-Javadoc)
	 * @see javax.mail.Authenticator#getPasswordAuthentication()
	 */
	@Override 
  	public PasswordAuthentication getPasswordAuthentication()
  	{ 
		return new PasswordAuthentication(username, password); 
  	} 
	 
	/**
	 * Funkcja sluzy do dodawania zalacznikow do wiadomosci.
	 * @param filepath - sciezka do pliku
	 * @param filename - nazwa pliku
	 * @throws MessagingException
	 */
	public void addAttachment(String filepath, String filename) throws MessagingException
	{ 
		BodyPart messageBodyPart = new MimeBodyPart(); 
    	DataSource source = new FileDataSource(filepath); 
    	messageBodyPart.setDataHandler(new DataHandler(source)); 
    	messageBodyPart.setFileName(filename); 
 
		multipart.addBodyPart(messageBodyPart); 
	}
	 
	private Properties setProperties()
	{ 
		Properties props = new Properties(); 
	 	props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", portSSL); 
		props.put("mail.smtp.socketFactory.port", portSSL); 
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
		props.put("mail.smtp.socketFactory.fallback", "false"); 
	 
		return props; 
	}
}