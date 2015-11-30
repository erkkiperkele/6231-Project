package dlms;

import dlms.model.UDPServerThread;

/**
 * @author Pascal Tozzi 27664850 Entry application with console interface to
 *         start the Sequencer service.
 */
public class StartSequencer
{
	/**
	 * Initialize the bank server and it services
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			UDPServerThread server = new UDPServerThread("Sequencer", 5000);
			server.join();
		}
		catch (Exception e)
		{
			System.out.println("Sequencer Exception: " + e.getMessage());
		}
	}
}
