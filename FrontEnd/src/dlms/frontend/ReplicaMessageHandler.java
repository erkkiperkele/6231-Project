package dlms.frontend;

import shared.udp.OperationType;

public class ReplicaMessageHandler extends Thread {

	private int sequenceNbr;
	private OperationType opType;
	
	/**
	 * Constructor
	 */
	public ReplicaMessageHandler(int sequenceNbr, OperationType opType) {
		
		super();
		this.sequenceNbr = sequenceNbr;
		this.opType = opType;
	}

	@Override
	public void run() {
		
		
		
		
		
		
		
	}
}
