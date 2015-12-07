
cd ../bin/

start call java -cp . dlms.replica.ReplicaLauncher Royal
start call java -cp . dlms.replica.ReplicaLauncher Dominion
start call java -cp . dlms.replica.ReplicaLauncher National

pause;
