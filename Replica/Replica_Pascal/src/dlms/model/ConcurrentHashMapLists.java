package dlms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import dlms.util.XMLHelper;
import shared.data.Customer;
import shared.data.Loan;

/**
 * @author Pascal Tozzi 27664850 Protect against most concurrency issue
 */
public class ConcurrentHashMapLists<T>
{
	HashMap<Character, ArrayList<T>> map = new HashMap<Character, ArrayList<T>>();

	public boolean put(String username, T value)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<T> list;
		synchronized (map)
		{
			if (!map.containsKey(lowerKey))
			{
				map.put(lowerKey, new ArrayList<T>());
			}
			list = map.get(lowerKey);
		}

		boolean isAdded = false;
		synchronized (list)
		{
			if (!list.stream().anyMatch(x -> username.equalsIgnoreCase(((KeyUserNameInterface) x).getUserName())))
			{
				isAdded = list.add(value);
			}
		}
		return isAdded;
	}

	public T get(String username)
	{
		T value = null;
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<T> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				Optional<T> optionalValue = list.stream().filter(x -> username.equalsIgnoreCase(((KeyUserNameInterface) x).getUserName())).findFirst();
				if (optionalValue.isPresent())
				{
					value = optionalValue.get();
				}
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public void commit(String filename, String username, boolean isCustomer) throws Exception
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<T> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				if (isCustomer)
				{
					XMLHelper.writeCustomers((ArrayList<Customer>) list, filename);
				}
				else
				{
					XMLHelper.writeLoans((ArrayList<Loan>) list, filename);
				}
			}
		}
	}

	public Loan getLoan(int loanID)
	{
		synchronized (map)
		{
			for (ArrayList<T> listElement : map.values())
			{
				for (T element : listElement)
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

	@SuppressWarnings("unchecked")
	public ArrayList<Customer> getAllCustomers()
	{
		ArrayList<Customer> list = new ArrayList<Customer>();
		synchronized (map)
		{
			for (ArrayList<T> listElement : map.values())
			{
				list.addAll((ArrayList<Customer>) listElement);
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Loan> getAllLoans()
	{
		ArrayList<Loan> list = new ArrayList<Loan>();
		synchronized (map)
		{
			for (ArrayList<T> listElement : map.values())
			{
				list.addAll((ArrayList<Loan>) listElement);
			}
		}
		return list;
	}

	public ArrayList<T> getList(String username)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<T> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		return list;
	}

	public boolean remove(String username)
	{
		boolean isRemoved = false;
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<T> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				Optional<T> optionalValue = list.stream().filter(x -> username.equalsIgnoreCase(((KeyUserNameInterface) x).getUserName())).findFirst();
				if (optionalValue.isPresent())
				{
					isRemoved = list.remove(optionalValue.get());
				}
			}
		}
		return isRemoved;
	}

	public Customer getCustomer(int accountNumber)
	{
		synchronized (map)
		{
			for (ArrayList<T> listElement : map.values())
			{
				for (T element : listElement)
				{
					if (((Customer) element).getAccountNumber() == accountNumber)
					{
						return (Customer) element;
					}
				}
			}
		}
		return null;
	}
}
