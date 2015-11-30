package shared.data;

public class ServerPorts {

    /**
     * maps each bank to a local port.
     * @param bank
     * @return
     */
    public static int getRMIPort(Bank bank) {
        switch (bank) {
            case Royal:
                return 4242;

            case National:
                return 4243;

            case Dominion:
                return 4244;

            default:
                return 0;
        }
    }
    
    /**
     * maps each bank to a local port.
     * @param bank
     * @return
     */
    public static String getFrontEndIP() 
    {
    	return "127.0.0.1";
    }
    
    /**
     * maps each bank to a local port.
     * @param bank
     * @return
     */
    public static int getFrontEndPort() 
    {
    	return 4245;
    }
    
    /**
     * maps each bank to a local port.
     * @param bank
     * @return
     */
    public static String getSequencerIP() 
    {
    	return "127.0.0.1";
    }
    
    /**
     * maps each bank to a local port.
     * @param bank
     * @return
     */
    public static int getSequencerPort() 
    {
    	return 4246;
    }

    /**
     * maps each bank to another local port for its UDP server.
     * @param bank
     * @return
     */
    public static int getUDPPort(Bank bank) {
        switch (bank) {
            case Royal:
                return 4245;

            case National:
                return 4246;

            case Dominion:
                return 4247;

            default:
                return 0;
        }
    }
}
