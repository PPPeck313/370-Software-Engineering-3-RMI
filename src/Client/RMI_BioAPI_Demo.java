import java.io.*;
import java.net.*;
import java.rmi.*;


public class RMI_BioAPI_Demo {	
	//*** This section is for multi-threading initialization
    private final class RequestThread extends Thread {
        private String option;
        private String AsteriskJava_IP;
        private String Service_UID;
        private String remote_AsteriskSrcFilename;
        private String socket_listener_ip;
        private int socket_port;
        private String local_fileName;

        RequestThread(final String option, final String AsteriskJava_IP, final String Service_UID, final String remote_AsteriskSrcFilename, final String socket_listener_ip, final int socket_port, final String local_fileName) 
        {
            this.option = option;
            this.AsteriskJava_IP = AsteriskJava_IP;
            this.Service_UID = Service_UID;
            this.remote_AsteriskSrcFilename = remote_AsteriskSrcFilename;
            this.socket_listener_ip = socket_listener_ip;
            this.socket_port = socket_port;
            this.local_fileName = local_fileName;
            this.start();
        }

        public void run() {
            if (option.equals("socket")) {
                    try {
                            initialize_socket(socket_port, local_fileName);
                    } catch (Exception e) {
                            System.out.println("Error on initializing socket server");
                    }
            }
            if (option.equals("AsteriskJava")) {
        		new RMI_BioAPI_AsteriskJava_Client(AsteriskJava_IP, Service_UID, remote_AsteriskSrcFilename, socket_listener_ip, socket_port, local_fileName);	        		
            }
            // Runs the above operations simultaneously as multi-threads
        }

}

	private PrintWriter pw;
	private BufferedReader br;	
	private Socket socket;
	private ServerSocket serverSocket = null;
	
	public void initialize_socket_stream_buffer() {
        try{
        	socket=serverSocket.accept();//BLOCKS HERE UNTIL SERVER PERMITS
        	System.out.println("connected" + '\n');
        	br=new  BufferedReader(new InputStreamReader(socket.getInputStream()));
        	pw = new PrintWriter(socket.getOutputStream(), true);
        }catch(IOException e) {e.printStackTrace();}
	}
	
	public void socket_stream_buffer_close () {
    	pw.close();
    	try {
    		br.close();
    	} catch(IOException e) {e.printStackTrace();}
	}
    
	public void socket_listener (String local_fileName) {
		String line;
		PrintWriter printToFile = null;

		try {
			printToFile = new PrintWriter(new FileOutputStream(local_fileName));
			
			br.readLine();
			br.readLine();
			
			line = br.readLine();
			
			int counter = 0;
			
			while (!line.equals("Done")) {
				if (counter > 0) {
					printToFile.println();
				}
				
				System.out.println(line);
				printToFile.print(line);
				
				line = br.readLine();
				counter++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		printToFile.flush();
		printToFile.close();
		socket_stream_buffer_close();
	}//Prints newline in preparation for new line before it, but not before the first line
	

	public void initialize_socket(int port, String local_fileName) {
		// TODO Auto-generated method stub
		try {
            serverSocket = new ServerSocket(port);
            String addr=serverSocket.getInetAddress().toString();
            String addrr=serverSocket.getLocalSocketAddress().toString();
            System.out.println("IP address: "+addr);
            System.out.println("socket address(IP address: port): "+addrr);

            initialize_socket_stream_buffer();
            socket_listener(local_fileName);
            serverSocket.close();		
		} catch (IOException e) {
            System.err.println("Could not listen on port: "+port);
            System.exit(-1);
        }            
	}



	public RMI_BioAPI_Demo(String local_fileName, int socket_port, 
			String AsteriskJava_IP, String Service_UID,
			String remote_AsteriskSrcFilename) throws Exception {
		//CLIENT'S IP TO BE CONNECTED TO BY SERVER
	    //LEVEL 1
		//String str = "localhost";
		
	    //LEVEL 2
		String str = InetAddress.getLocalHost().getHostAddress();
		
		//LEVEL 3
	    /*URL connection = new URL("http://checkip.amazonaws.com/");
	    URLConnection con = connection.openConnection();
	    String str = null;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
	    str = reader.readLine();*/
		
		String socket_listener_ip = str;
	    
        new RequestThread("socket", "N/A", "N/A", "N/A", "N/A", socket_port, local_fileName);//SOCKET_LISTENER_IP UNUSED IN SOCKET
        new RequestThread("AsteriskJava", AsteriskJava_IP, Service_UID, remote_AsteriskSrcFilename, socket_listener_ip, socket_port, local_fileName);
	}


	public static void main(String[] args) throws Exception {
	    if (args.length != 5)
	    {
	            System.out.println
	                ("Syntax - java RMI_BioAPI_Demo <local_Filename> <host_port> <Remote_AsteriskJava_IP> <service_UID> <remote_source_Filename>");
	            System.exit(1);
	    }
			
		
        // Create an instance of our service server ...
	    
	    RMI_BioAPI_Demo demo_instance = new RMI_BioAPI_Demo(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]);

	}

}