package dlms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.*;

import shared.util.Env;

public class EnvP
{
	public static Date getNewLoanDueDate()
	{
		Date loanDueDate;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 6);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			loanDueDate = formatter.parse(formatter.format(cal.getTime()));
		}
		catch (Exception e)
		{
			Env.log(Level.SEVERE, "Date Parse Exception: " + e.getMessage());
			loanDueDate = cal.getTime();
		}
		return loanDueDate;
	}

	/**
	 * If the string is numeric
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
		try
		{
			Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public static String getServerCustomersFile(String name, String username)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		return "./" + name + "_" + lowerKey.toString() + Constant.ServerCustomersFile;
	}

	public static String getServerLoansFile(String name, String username)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		return "./" + name + "_" + lowerKey.toString() + Constant.ServerLoansFile;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromDatagram(DatagramPacket request)
	{
		T messageRequested;
		try
		{
			byte[] message = Arrays.copyOf(request.getData(), request.getLength());

			// De-serialization of object
			ByteArrayInputStream bis = new ByteArrayInputStream(message);
			ObjectInputStream in = new ObjectInputStream(bis);
			messageRequested = (T) in.readObject();
		}
		catch (Exception e)
		{
			messageRequested = null;
		}
		return messageRequested;
	}

	public static <T> byte[] getByteFromObject(T object) throws Exception
	{
		byte[] message;

		// Serialization of object
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(object);

		message = bos.toByteArray();
		return message;
	}
}
