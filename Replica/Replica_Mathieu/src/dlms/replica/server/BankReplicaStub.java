package dlms.replica.server;

import java.net.InetSocketAddress;

/**
 * A stub, representing the base data of a bank and what it needed to instantiate a bank replica
 * 
 * @author mat
 *
 */
public class BankReplicaStub {
	
	public String id = "";
	public InetSocketAddress addr;
	
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param addr
	 */
	public BankReplicaStub(String id, InetSocketAddress addr) {
		
		super();
		this.id = id;
		this.addr = addr;
	}
	
	/**
	 * Test if another stub is equal to this one
	 * 
	 * @param id
	 * @param addr
	 * @return
	 */
	public boolean equals(BankReplicaStub stub) {

		return stub.id == this.id && stub.addr.equals(this.addr);
	}
	
	/**
	 * 
	 */
	public String toString() {
		return "bankid: " + this.id + "host: " + this.addr.getHostString() + "port: " + this.addr.getPort(); 
	}
	
}
