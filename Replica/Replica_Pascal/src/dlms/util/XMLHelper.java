package dlms.util;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;
import java.util.ArrayList;

import dlms.model.*;

/**
 * @author Pascal Tozzi 27664850 Little helper to serialize to XML and
 *         de-serialize from XML.
 */
public class XMLHelper
{
	/**
	 * writeCustomers
	 * 
	 * @param f
	 * @param filename
	 * @throws Exception
	 */
	public static void writeCustomers(ArrayList<Customer> f, String filename) throws Exception
	{
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		encoder.writeObject(f);
		encoder.close();
	}

	/**
	 * readCustomers
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Customer> readCustomers(String filename) throws Exception
	{
		ArrayList<Customer> lstCustomers = null;
		try
		{
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
			lstCustomers = (ArrayList<Customer>) decoder.readObject();
			decoder.close();
		}
		catch (Exception e)
		{
			// Should handle the error, but for simplicity we return an empty
			// list.
			lstCustomers = new ArrayList<Customer>();
		}
		return lstCustomers;
	}

	/**
	 * writeLoans
	 * 
	 * @param f
	 * @param filename
	 * @throws Exception
	 */
	public static void writeLoans(ArrayList<Loan> f, String filename) throws Exception
	{
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
		encoder.writeObject(f);
		encoder.close();
	}

	/**
	 * readLoans
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Loan> readLoans(String filename) throws Exception
	{
		ArrayList<Loan> lstLoans = null;
		try
		{
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
			lstLoans = (ArrayList<Loan>) decoder.readObject();
			decoder.close();
		}
		catch (Exception e)
		{
			// Should handle the error, but for simplicity we return an empty
			// list.
			lstLoans = new ArrayList<Loan>();
		}
		return lstLoans;
	}
}
