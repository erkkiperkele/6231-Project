package shared.data;

import shared.util.Constant;
import shared.util.Env;

public class ServerPorts {
    
    /**
     * maps each bank to a local port.
     * @return
     */
    public static String getFrontEndIP() 
    {
    	return "127.0.0.1";
    }
    
    /**
     * maps each bank to a local port.
     * @return
     */
    public static int getFrontEndPort() 
    {
    	return 4245;
    }
    
    /**
     * maps each bank to a local port.
     * @return
     */
    public static String getSequencerIP() 
    {
    	return "127.0.0.1";
    }
    
    /**
     * maps each bank to a local port.
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
//    public static int getUDPPort(Bank bank) {
//        switch (bank) {
//            case Royal:
//                return 42201;
//
//            case National:
//                return 42202;
//
//            case Dominion:
//                return 42203;
//
//            default:
//                return 0;
//        }
//    }

        public static int getUDPPort(Bank bank) {
            return Env.getReplicaIntranetServerInfo(Env.getMachineName(), bank).getPort();
        }

    /**
     * maps each bank to another local port for its UDP server.
     * @param bank
     * @return
     */
//    public static int getUDPPortIntranet(Bank bank) {
//        switch (bank) {
//            case Royal:
//                return 4204;
//
//            case National:
//                return 4205;
//
//            case Dominion:
//                return 4206;
//
//            default:
//                return 0;
//        }
//    }

    /**
     * maps each bank to another local port for its UDP server.
     * @param bank
     * @return
     */
//    public static int getUDPPortReplicaManager(Bank bank) {
//        switch (bank) {
//            case Royal:
//                return 4207;
//
//            case National:
//                return 4208;
//
//            case Dominion:
//                return 4209;
//
//            default:
//                return 0;
//        }
//    }
}
