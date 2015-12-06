package dlms;

import dlms.model.UDPServerThread;
import shared.util.Constant;
import shared.util.Env;

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
			Env.setMachineName(Constant.MACHINE_NAME_SEQUENCER);
			UDPServerThread server = new UDPServerThread(Env.getSequencerServerInfo().getServerName(), Env.getSequencerServerInfo().getPort());
			server.start();
			server.executeTestMessage();
			server.join();
		}
		catch (Exception e)
		{
			System.out.println("Sequencer Exception: " + e.getMessage());
		}
	}
}
