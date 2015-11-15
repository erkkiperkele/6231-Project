package dlms.model;

/**
 * @author Pascal Tozzi 27664850 Same as UDPRequestThread except it allow retry
 *         when it fail
 */
public class UDPRequestThreadWithRetry implements Runnable
{
	private Thread t = null;
	private UDPRequestThread requestThread;
	private int retryAmount = 0;

	public UDPRequestThreadWithRetry(UDPRequestThread requestThread, int retryAmount)
	{
		this.requestThread = requestThread;
		this.retryAmount = retryAmount;
	}

	public void run()
	{
		boolean canRetry = true;
		for (int i = 0; i <= retryAmount && canRetry; i++)
		{
			requestThread = new UDPRequestThread(requestThread.getRemoteHost(), requestThread.getRemotePort(), requestThread.getUsername(),
					requestThread.getCustomer(), requestThread.getLoan());
			requestThread.start();
			boolean isInterruptedException = false;
			try
			{
				requestThread.join();
			}
			catch (InterruptedException e)
			{
				isInterruptedException = true;
			}

			// if there an error and retry is allowed or an interruption
			// exception, then we can retry
			canRetry = requestThread.isAllowRetryOnError() || isInterruptedException;
		}
	}

	public void join() throws InterruptedException
	{
		if (t != null)
		{
			t.join();
		}
	}

	public void start()
	{
		t = new Thread(this);
		t.start();
	}

	/**
	 * @return the loanAmount
	 */
	public double getLoanAmount()
	{
		if (requestThread == null)
		{
			throw new RuntimeException("Thread hasn't been executed yet, data not initialized.");
		}
		else
		{
			return requestThread.getLoanAmount();
		}
	}

	/**
	 * @return the loanAmount
	 */
	public boolean isLoanTransfered()
	{
		if (requestThread == null)
		{
			throw new RuntimeException("Thread hasn't been executed yet, data not initialized.");
		}
		else
		{
			return requestThread.isLoanTransfered();
		}
	}

	/**
	 * @return the isError
	 * @throws Exception
	 */
	public boolean isError()
	{
		if (requestThread == null)
		{
			throw new RuntimeException("Thread hasn't been executed yet, data not initialized.");
		}
		else
		{
			return requestThread.isError(null);
		}
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		if (requestThread == null)
		{
			throw new RuntimeException("Thread hasn't been executed yet, data not initialized.");
		}
		else
		{
			return requestThread.getErrorMessage();
		}
	}
}
