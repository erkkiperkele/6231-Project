cd Replica_Dominion_Pascal
start call java -cp ../../Replica/Replica_Pascal/bin;../../Shared/bin dlms.StartBankServer Dominion
cd ../Replica_National_Pascal
start call java -cp ../../Replica/Replica_Pascal/bin;../../Shared/bin dlms.StartBankServer National
cd ../Replica_Royal_Pascal
start call java -cp ../../Replica/Replica_Pascal/bin;../../Shared/bin dlms.StartBankServer Royal