import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

/**
 * This class represents OSC connection
 * @author mohamed.hamzeen
 */
public class OSCInterface {
	
	/**
	 * IP address of the OSC receiver
	 */
	private InetAddress inetAdress;
	
	/**
	 * Port on which the OSC receiver listens to 
	 */
	private int port = 3333;
	
	/**
	 * The OSC out port for this OSC Interface
	 */
	private OSCPortOut sender;
	
	/**
	 * Is the IP address reachable?
	 */
	private boolean reachable = true;
	private boolean checkingStatus=false;
	
	/**
	 * Constructor
	 */
	public OSCInterface(String host, int port){
				
		try {
			this.inetAdress = InetAddress.getByName(host);
			if (port>1023) this.port = port;			
			System.out.println("*********OSC Interface connected to: " + this.port + "  " + this.inetAdress);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (this.inetAdress==null) {
			try { this.inetAdress = InetAddress.getLocalHost(); }
			catch (Exception e) {}			
		}
		
		checkStatus();
		try {
			sender = new OSCPortOut(inetAdress, port);
		} catch (Exception e) {}
	}
	
	/**
	 * Sends list of OSC messages as OSC bundles
	 * @param oscMessagesList List of OSC messages
	 */
	public void sendMessage(List <OSCMessage> oscMessagesList){

       OSCBundle bundle = new OSCBundle(listToArray(oscMessagesList));

       try {
       	 if (reachable) sender.send(bundle);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a single OSC message 
	 * @param oscMessage
	 */
	public void sendSingleMessage(OSCMessage oscMessage){

		try {
			if (reachable) sender.send(oscMessage); 
			//printOSCData(oscMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
	
	/**
	 * Sends OSC bundle
	 * @param bundle
	 */
	public void sendOSCBundle (OSCBundle bundle){
          try {
	    sender.send(bundle);
	  } catch (IOException e) {
		e.printStackTrace();
	  }
	}

	/**
	 * Prints out OSC data 
	 * @param oscMessage
	 */
	public void printOSCData(OSCMessage oscMessage){
		Object [] args = oscMessage.getArguments();
		for (int i=0; i<args.length; i++){
			System.out.print(args[i] + " / ");
		}
		System.out.println();
	}

	/**
	 * Converts List of OSC Message into an array of OSC Messages
	 * @param oscMessagesList
	 * @return 
	 */
	public OSCMessage[] listToArray (List <OSCMessage> oscMessagesList){
		
		OSCMessage [] oscMessagesArray = new OSCMessage [oscMessagesList.size()];
		
		for (int i=0; i<oscMessagesList.size(); i++){
			oscMessagesArray[i]=oscMessagesList.get(i);
		}
		
		return oscMessagesArray;

	}

	public String getInetAdress() {
		return inetAdress.getHostAddress();
	}


	public int getPort() {
		return port;
	}
	
	
	public void closeInteface (){
		this.sender.close();
	}

	public boolean isReachable() {
	 return reachable;
	}
	
	public synchronized void checkStatus() {
		if (checkingStatus) return;
		checkingStatus=true;
		
		new Thread(new Runnable() {
		    public void run() {
		    	try {  
		    		long startTime = System.currentTimeMillis();
		    		reachable = inetAdress.isReachable(1000);
		    		long sleepTime = 1000 - (System.currentTimeMillis() - startTime);
		    		if (sleepTime>0) Thread.sleep(sleepTime);
		    	} catch (Exception e) { reachable = false; };
		    	checkingStatus=false;
		    }
		  }).start();
	}	
}
