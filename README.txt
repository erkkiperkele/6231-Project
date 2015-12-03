
STARTING THE APPLICATION
--------------------------------------------

** Step 1 - Start the ORB daemon

The ORB daemon is used as the naming service for the CORBA front end. 
Open a console and type the following command:

WINDOWS: start orbd -ORBInitialPort 1050
UNIX: orbd -ORBInitialPort 1050&


** Step 2 - Start the front end

The front end will register itself with the ORB daemon so it needs to 
be started after it. The front end main method is in
FrontEnd/src/dlms/frontend/FrontEnd.java. Start the process in whichever 
way you are comfortable with.


** Step 3 (temporary/optional) - Start the sample test client

The test client shows how to instantiate the customer and manager clients 
and execute commands.
The front end main method is in
Client/src/dlms/client/TestClient.java. Start the process in whichever 
way you are comfortable with.