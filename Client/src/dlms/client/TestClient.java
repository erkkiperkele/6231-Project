package dlms.client;

/**
 * Sample test client launcher
 * 
 * @author mat
 *
 */
public class TestClient {

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		CustomerClient cc = new CustomerClient();
		
		int accontNbr1 = cc.openAccount("rbc", "John", "Doe", "jondoe@gmail.com", "5145145145", "jondoe");
		int accontNbr2 = cc.openAccount("bmo", "Charles", "Xavier", "charlesxavier@gmail.com", "5145145145", "charlesxavier");
		int accontNbr3 = cc.openAccount("cibc", "Joey", "Vega", "joeyvega@gmail.com", "5145145155", "joeyvega");

		System.out.println("Created account " + accontNbr1);
		System.out.println("Created account " + accontNbr2);
		System.out.println("Created account " + accontNbr3);
		
		// Create a manager client and output user data
		ManagerClient mc = new ManagerClient();
		mc.printCustomerInfo("rbc");
		mc.printCustomerInfo("bmo");
		mc.printCustomerInfo("cibc");

	}
}
