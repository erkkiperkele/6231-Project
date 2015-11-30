package dlms.replica.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import dlms.replica.server.IBankServer;

/**
 * Base class for customer and manager clients 
 * 
 * @author mat
 *
 */
public class Client {

	public static final String WSDL_URL = "http://localhost:8080/ws/ReplicaManager?wsdl";
	
	protected Logger logger = null;
	protected IBankServer server = null;
	
	/**
	 * 
	 * Constructor
	 */
	public Client() {
		
		super();
		
		URL url = null;
		try {
			url = new URL(WSDL_URL);
			
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		QName qname = new QName("http://server.comp6231a3.primat.ca/", "BankReplicaManagerService");
		Service service = Service.create(url, qname);
		
		this.server = service.getPort(IBankServer.class);
	}

	
	public void setUpLogger(int id) {
	
		// Set up the logger
		String className = this.getClass().getSimpleName();
		String textId = className + "-" + id;
		this.logger = Logger.getLogger(textId);
	    FileHandler fh;  
	
	    try {
	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler(textId + "-log.txt");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.info(textId + " logger started");
	    } catch (SecurityException e) {  
	        e.printStackTrace();
	        System.exit(1);
	    } catch (IOException e) {  
	        e.printStackTrace(); 
	        System.exit(1); 
	    }
	}
		
	/**
	 * Gets the server stub for making server operations
	 * 
	 * @param serverId
	 * @return
	 */
	protected IBankServer getBankServer(String serverId) {
		
		return this.server;
	}
}
