package dlms.model.udpobject;

import java.io.Serializable;

import shared.data.Loan;

public class UDPResponseTransferLoan implements Serializable
{
	private static final long serialVersionUID = 5819766053346981713L;

	private Loan loan;

	public UDPResponseTransferLoan(Loan loan)
	{
		this.loan = loan;
	}

	public Loan getLoan()
	{
		return loan;
	}

	public void setLoan(Loan loan)
	{
		this.loan = loan;
	}

	public boolean transferLoanApproved()
	{
		return this.loan != null;
	}
}
