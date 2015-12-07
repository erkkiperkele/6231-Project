package dlms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import dlms.util.XMLHelper;
import shared.data.Customer;

/**
 * @author Pascal Tozzi 27664850 Protect against most concurrency issue
 */
public class CustomerConcurrentHashMapLists
{
	HashMap<Character, ArrayList<Customer>> map = new HashMap<Character, ArrayList<Customer>>();

	public boolean put(Customer c)
	{
		Character lowerKey = Character.toLowerCase(c.getUserName().charAt(0));
		ArrayList<Customer> list;
		synchronized (map)
		{
			if (!map.containsKey(lowerKey))
			{
				map.put(lowerKey, new ArrayList<Customer>());
			}
			list = map.get(lowerKey);
		}

		boolean isAdded = false;
		synchronized (list)
		{
			if (!list.stream().anyMatch(x -> c.getUserName() == x.getUserName()))
			{
				isAdded = list.add(c);
			}
		}
		return isAdded;
	}

	public Customer get(String username)
	{
		Customer value = null;
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<Customer> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				Optional<Customer> optionalValue = list.stream().filter(x -> username == x.getUserName()).findFirst();
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
		ArrayList<Customer> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				XMLHelper.writeCustomers((ArrayList<Customer>) list, filename);
			}
		}
	}

	public ArrayList<Customer> getAllCustomers()
	{
		ArrayList<Customer> list = new ArrayList<Customer>();
		synchronized (map)
		{
			for (ArrayList<Customer> listElement : map.values())
			{
				list.addAll((ArrayList<Customer>) listElement);
			}
		}
		return list;
	}

	public ArrayList<Customer> getList(String username)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		ArrayList<Customer> list;
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
		ArrayList<Customer> list;
		synchronized (map)
		{
			list = map.get(lowerKey);
		}
		if (list != null)
		{
			synchronized (list)
			{
				Optional<Customer> optionalValue = list.stream().filter(x -> username.toLowerCase() == x.getUserName()).findFirst();
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
			for (ArrayList<Customer> listElement : map.values())
			{
				for (Customer element : listElement)
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
