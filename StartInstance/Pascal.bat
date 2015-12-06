cd Replica_Dominion_Pascal
start call java -cp ../../out/production/Replica_Pascal;../../out/production/Shared dlms.StartBankServer Dominion
cd ../Replica_National_Pascal
start call java -cp ../../out/production/Replica_Pascal;../../out/production/Shared dlms.StartBankServer National
cd ../Replica_Royal_Pascal
start call java -cp ../../out/production/Replica_Pascal;../../out/production/Shared dlms.StartBankServer Royal