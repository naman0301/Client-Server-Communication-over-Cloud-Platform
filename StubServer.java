import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.*;
import java.io.File;
class cmdStr{
        String cmd;
        String description;
	public cmdStr(String com,String des){
		this.cmd = com;
		this.description = des;
	}
}

public class StubServer
{
   int clientno = 1;

  String help(String[] tokens){
	cmdStr help = new cmdStr("help","This is help section to see details about each command.");
	cmdStr count = new cmdStr("count","This is count command which is used to count number of parameters passed after command.");
	cmdStr time = new cmdStr("time","This is time command to check time.");
	cmdStr dict = new cmdStr("dict","This is dictionary command to find out meaning of a word passed as parameters.");
	cmdStr file = new cmdStr("file","This is file command to find out properties and behaviour of file.");

	cmdStr[] cmds= new cmdStr[5];
	cmds[0] = help;
	cmds[1] = count;
	cmds[2] = time;
	cmds[3] = dict;
	cmds[4] = file;

	String ret="";
	if(tokens.length>1){
		for(int x=1;x<tokens.length;x++){
			for(int y=0;y<5;y++){
			if((tokens[x].trim()).equals(cmds[y].cmd)){
				 System.out.println(cmds[y].cmd+"\n"+cmds[y].description+"\n");
				 ret+="Command Name: "+cmds[y].cmd+"\n"+"Description: "+cmds[y].description+"\n";
				}
			}
		}
	}else{
              ret+="Command Name: "+cmds[0].cmd+"\n"+"Description: "+cmds[0].description+"\n";
	}
	System.out.println("Inside help function\n");
	ret += "help command was received\n";
	return ret;
  }	
  

  String count(String[] tokens){
	int numb = tokens.length -1;
	System.out.println("Inside count function\n");
        return "count: "+numb ;	
  }

  String time(String[] tokens){
        Date date = new Date();
	String dt = date.toString();
        return dt;
  }

  String file(String[] tokens){ 
        String txt = "";
	String filename = "hello.c";
	File myfile = new File(filename);
	if(myfile.exists() == true)
	{
	  System.out.println( "File :" + myfile.getName());
	if(myfile.isDirectory()){
	txt = "It is a directory\n";
	}
	else if(myfile.isFile()){
	   txt = "It is a regular file\n";
	}
	txt += "Size is " +myfile.length() +"bytes\n";
	
	txt += "Last time modified: " +myfile.lastModified();

	txt += "\nIs file Readable : " +myfile.canRead();

	txt += "\n Is file Writable : " +myfile.canWrite();

	txt += "\n Is file Executable : " +myfile.canExecute();

  }
	else{
	  txt = "File does not exist";
	}
  	return txt;
  }

String dict(String[] tokens){
	ArrayList<String> list = new ArrayList<String>();
	try{
		for(int i=1;i<tokens.length;i++){
			Process p = Runtime.getRuntime().exec("dict "+tokens[i]);
			Scanner sc = new Scanner(p.getInputStream());
			String def="";
			while(sc.hasNextLine()){
				def+=sc.nextLine()+"\n";
			}
			list.add(def);
			sc.close();
		}
	}catch(IOException e){
		e.printStackTrace();
	}
	System.out.println("Inside dict function\n");
	String ret="";
	for(String s:list){
		ret+=s+"\n";
	}
	ret+="dict command was received\n";
        return ret;
  }
  public static void main(String[] args) { 
  int port= (args.length>0) ? Integer.parseInt(args[0]) : 40212;
  new StubServer(port); }

  public StubServer(int port) {
   ServerSocket server=null;
   int clientNo=1;
   try
    {
      //Create the Server Socket
      server=new ServerSocket(port);
      System.out.println("Starting multi threaded server on port " + server.getLocalPort());
      while(clientno<4)
      {
        //Listen for a connection request - program waits for an incoming connection
	Socket socket = server.accept();
//	Callable<void> task = new EchoTask(socket);
//	Socket socket = new Socket();
	System.out.println("After socket accept while loop");
	//Display client information
	System.out.println("Starting thread for client " + clientno + " at " + new Date());
	InetAddress inetAddress = socket.getInetAddress();
	System.out.println("Client " + clientno + "'s hostname is "
	      +  inetAddress.getHostName()+ ":" + socket.getPort()); //getPort() not in textbook
	System.out.println("Client " + clientno + "'s ipAddress is "
	      +  inetAddress.getHostAddress());

	//Start a new thread
        HandleAClient task=new HandleAClient(socket);
        task.setName("Client# " +clientno);
        task.start();

        clientno++;  //increment clientNo for next client
       }
      }
      catch(IOException ex) { ex.printStackTrace(); }
     } //end of main

     //Inner class to handle a client
     //Define the thread class for handling a new connection
     class HandleAClient extends Thread {
         private Socket socket;  //represents the connection to the client

         HandleAClient(Socket socket) {this.socket=socket; }

         public void run()
         {
          System.out.println(this.getName() + " is ready for your commands"); 

	  while(true)
	     {
		try{
//		DataInputStream inputFromClient=new DataInputStream(socket.getInputStream());
//         	DataOutputStream outputToClient=new DataOutputStream(socket.getOutputStream());
		Writer out = new OutputStreamWriter(socket.getOutputStream());
//		Reader in = new InputStreamReader(socket.getInputStream());
		InputStream in = new BufferedInputStream(socket.getInputStream());
		String line;
		String[] tokens;
		while(true){
		int nBytes;
		byte[] buffer = new byte[1000];
		out.write("\nPlease enter a command\n");
		out.flush();
//  		InputStream in = new BufferedInputStream(socket.getInputStream());
		nBytes=in.read(buffer);
//		out.flush();
		line = new String(buffer,0,nBytes);
		System.out.println(line);
//		line = scanner.nextLine();
		tokens = line.split(" ");
		System.out.println(tokens[0].trim().equals("help"));
		switch(tokens[0].trim()){
			case "help":out.write(help(tokens));
				break;
			case "count":out.write(count(tokens));
				break;
			case "time":out.write(time(tokens));
				break;
			case "file":out.write(file(tokens));
                                break;
			case "dict":out.write(dict(tokens));
                                break;
			default:out.write("Sorry, invalid command");
                                break;
		}

		System.out.flush();
		out.flush();
		}
		} catch (IOException ex) {
       			ex.printStackTrace();     
    		} finally { 
                  System.out.println("Server shut down");
//		out.write("Server shut down");
                 }
		 System.out.println("Reply sent!");
	     }
       }
  } //end of inner class HandleAClient
} //end of outer class MultiThreadServer
