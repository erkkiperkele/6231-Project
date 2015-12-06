package shared.udp;

import java.io.Serializable;


public interface IOperationMessage extends Serializable 
{
	OperationType getOperationType();
	String getBank();
	String getMachineName();
}
