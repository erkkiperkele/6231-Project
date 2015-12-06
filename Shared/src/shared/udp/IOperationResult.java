package shared.udp;

import java.io.Serializable;

public interface IOperationResult<T> extends Serializable {

	T getResult();
	boolean isResultEqual(IOperationResult<T> altMessage);
}
