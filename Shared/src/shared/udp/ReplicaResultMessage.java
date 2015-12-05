package shared.udp;

import java.io.Serializable;
import java.net.InetSocketAddress;

@SuppressWarnings("serial")
public class ReplicaResultMessage<T> implements Serializable {

	private long opSequenceNbr;
	private OperationType opType;
	private T opResult;
	private InetSocketAddress replicaAddr;
	private String errorMessage;
	
	public ReplicaResultMessage(long opSequenceNbr, OperationType opType, T opResult, InetSocketAddress replicaAddr, String errorMessage) {
		super();
		this.opSequenceNbr = opSequenceNbr;
		this.opType = opType;
		this.opResult = opResult;
		this.replicaAddr = replicaAddr;
		this.errorMessage = errorMessage;
	}

	//
	// Getters and setters
	//
	
	public long getOpSequenceNbr() {
		return opSequenceNbr;
	}

	public void setOpSequenceNbr(long opSequenceNbr) {
		this.opSequenceNbr = opSequenceNbr;
	}

	public OperationType getOpType() {
		return opType;
	}

	public void setOpType(OperationType opType) {
		this.opType = opType;
	}

	public T getOpResult() {
		return opResult;
	}

	public void setOpResult(T opResult) {
		this.opResult = opResult;
	}

	public InetSocketAddress getReplicaAddr() {
		return replicaAddr;
	}

	public void setReplicaAddr(InetSocketAddress replicaAddr) {
		this.replicaAddr = replicaAddr;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
