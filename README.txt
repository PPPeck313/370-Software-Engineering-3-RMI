Generate Stub
rmic RMI_BioAPI_AsteriskJava_Server

Start Server
rmiregistry &
java RMI_BioAPI_AsteriskJava_Server 2099

Start Client
java RMI_BioAPI_Demo demo16.txt 1688 [insert server ip] my_transact demo.txt