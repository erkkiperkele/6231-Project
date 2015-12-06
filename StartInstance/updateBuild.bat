cd ..\out\production\Client
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\Client\bin .\
cd ..\FrontEnd
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\FrontEnd\bin .\
cd ..\Replica_Aymeric
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\Replica\Replica_Aymeric\bin .\
cd ..\Replica_Mathieu
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\Replica\Replica_Mathieu\bin .\
cd ..\Replica_Pascal
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\Replica\Replica_Pascal\bin .\
cd ..\Replica_Richard
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\Replica\Replica_Richard\bin .\
cd ..\ReplicaManager
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\ReplicaManager\bin .\
cd ..\Shared
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\Shared\bin .\
cd ..\Sequencer
FOR /D %%p IN (".\*.*") DO rmdir "%%p" /s /q
del *.* /F /S /Q
xcopy /s .\..\..\..\Sequencer\bin .\

