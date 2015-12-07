package dlms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import dlms.util.XMLHelper;
import shared.data.Loan;

/**
 * @author Pascal Tozzi 27664850 Protect against most concurrency issue
 */
public class LoanConcurrentHashMapLists
{
	HashMap<Character, ArrayList<Loan>> map = new HashMap<Character, ArrayList<Loan>>();

	public boolean put(String username, Loan value)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<Loan> list;
		synchronized (map)
		{
			if (!map.containsKey(lowerKey))
			{
				map.put(lowerKey, new ArrayList<Loan>());
			}
			list = map.get(lowerKey);
		}

		boolean isAdded = false;
		synchronized (list)
		{
			if (!list.stream().anyMatch(x -> value.getCustomerAccountNumber() == x.getCustomerAccountNumber()))
			{
				isAdded = list.add(value);
			}
		}
		return isAdded;
	}

	public Loan get(String username, int accountID)
	{
		Loan value = null;
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<Loan> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				Optional<Loan> optionalValue = list.stream().filter(x -> accountID == x.getCustomerAccountNumber()).findFirst();
				if (optionalValue.isPresent())
				{
					value = optionalValue.get();
				}
			}
		}
		return value;
	}

	public void commit(String filename, String username) throws Exception
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<Loan> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				XMLHelper.writeLoans((ArrayList<Loan>) list, filename);
			}
		}
	}

	public Loan getLoan(int loanID)
	{
		synchronized (map)
		{
			for (ArrayList<Loan> listElement : map.values())
			{
				for (Loan element : listElement)
				{
					if (((Loan) element).getLoanNumber() == loanID)
					{
						return (Loan) element;
					}
				}
			}
		}
		return null;
	}

	public ArrayList<Loan> getAllLoans()
	{
		ArrayList<Loan> list = new ArrayList<Loan>();
		synchronized (map)
		{
			for (ArrayList<Loan> listElement : map.values())
			{
				list.addAll((ArrayList<Loan>) listElement);
			}
		}
		return list;
	}

	public ArrayList<Loan> getList(String username)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<Loan> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		return list;
	}

	public boolean remove(String username, int accountID)
	{
		boolean isRemoved = false;
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<Loan> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				Optional<Loan> optionalValue = list.stream().filter(x -> accountID == x.getCustomerAccountNumber()).findFirst();
				if (optionalValue.isPresent())
				{
					isRemoved = list.remove(optionalValue.get());
				}
			}
		}
		return isRemoved;
	}
}
