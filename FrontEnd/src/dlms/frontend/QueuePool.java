package dlms.frontend;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import shared.udp.UDPMessage;

/**
 * Each client request needs a blocking queue to get the UDP messages from the
 * replica. This class represents a dictionary of all current such queues for
 * use by the replica
 * 
 * @author mprice
 *
 */
@SuppressWarnings("serial")
public class QueuePool extends HashMap<Long, BlockingQueue<UDPMessage>> {

}
