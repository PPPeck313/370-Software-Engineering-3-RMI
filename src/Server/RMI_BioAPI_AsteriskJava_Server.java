import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.UnknownHostException;
import java.net.*;


/**
 * 
 */

/**
 * @author bon
 *
 */
public class RMI_BioAPI_AsteriskJava_Server extends UnicastRemoteObject
implements RMI_BioAPI_AsteriskJava_Interface {

    public RMI_BioAPI_AsteriskJava_Server(int port) throws RemoteException
    {
        super(port);
    }

	
	/* (non-Javadoc)
	 * @see RMI_BioAPI_AsteriskJava_Interface#RPC_FileRead(java.lang.String, java.lang.String)
	 */
	@Override
	public void RPC_FileRead(String Service_UID, String srcFileName, String socket_ip, int socket_port, String remote_fileName)
		throws RemoteException {
			// TODO Auto-generated method stub
			Socket soc;
			PrintWriter pw = null;
			BufferedReader brf = null;
			
			File fileName = new File(srcFileName);

			try {
				brf = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileName.getAbsolutePath())));
			}
			
			catch ( IOException ioe ) {throw new RuntimeException(ioe);}
			
			try {
				if ( brf == null )
					throw new RuntimeException("Cannot read from closed file "
											+ fileName.getAbsolutePath() + ".");
				
				try {
						System.out.println("Server address connected to is "+socket_ip+" and port is "+socket_port);
						soc = new Socket(socket_ip, socket_port);//CONNECTS TO CLIENT
						pw=new PrintWriter(soc.getOutputStream(), true);

						pw.println("StartXfer"); //Signaling message to start xfer to the remote socket server     		
						pw.println(remote_fileName); //Signaling message about remote file name

						String line = brf.readLine();
						int counter=0;

						while ( line != null){
							System.out.println(line);
							pw.println(line);
							counter++;
							line = brf.readLine();
						}
						System.out.println();

						pw.println("Done"); //Signaling message to terminate the remote socket server

						brf.close();
						soc.close();
				} 
				
				catch (UnknownHostException e) {
					System.err.println("Don't know about host.");
					System.exit(1);
						e.printStackTrace();
				} 
					
				catch (IOException e) {
					System.err.println("Couldn't get I/O for the connection to server.");
					System.exit(1);
					e.printStackTrace();
				}    		
			}
			
		catch (Exception e) {throw new RuntimeException(e);}
	}  // method RPC_FileRead()


	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	    if (args.length != 1)
	    {
	            System.out.println
	                ("Syntax - java RMI_BioAPI_AsteriskJava_Server_Package/RMI_BioAPI_AsteriskJava_Server_Impl host_port");
	            System.exit(1);
	    }
			
        // Create an instance of our service server ...
        /*System.setProperty("java.security.policy","file:///Users/Ptotheumpteenth/Desktop/P3/Server/test.policy.txt");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }*/
	    
	    RMI_BioAPI_AsteriskJava_Server svr = new RMI_BioAPI_AsteriskJava_Server(Integer.parseInt(args[0]));
	    //EXPORTS BY DEFAULT BECAUSE EXTENDS UNICASTREMOTEOBJECT
	    Registry registry = LocateRegistry.getRegistry(1099);//MANUAL BINDING VS NAMING
        
		System.out.println("RmiRegistry listens at port 1099 ");
		System.out.println("AsteriskJava BSP Server is ready to listen on " + args[0] + '\n');
		
	    URL connection = new URL("http://checkip.amazonaws.com/");
	    URLConnection con = connection.openConnection();
	    String str = null;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    str = reader.readLine();
	    
	    String service = "RMI_BioAPI_AsteriskJava";
	    registry.rebind(service, svr);
	    
	    System.out.println("Local IP: " + InetAddress.getLocalHost().getHostAddress());
	    System.out.println("External IP: " + str);
 		System.out.println("Server located at: rmi://" + str + ":" + 1099 + "/" + service);
		System.out.println("BioAPI AsteriskJava RMI server starts ... " + '\n');
	}

}