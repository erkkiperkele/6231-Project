package shared.udp;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FailedProcessMessage implements Serializable {

	public String bankId;
	public long opSequenceNbr;
	
	public FailedProcessMessage(String bankId, long opSequenceNbr) {
		super();
		this.bankId = bankId;
		this.opSequenceNbr = opSequenceNbr;
	}

}
