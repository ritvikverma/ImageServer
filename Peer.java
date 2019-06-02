import java.io.Serializable;

/**
 * @author ritvikverma
 * Implements the peer construct, and contains the IP and port numbers, along with constructors for initialisiation 
 */
@SuppressWarnings("serial")
public class Peer  implements Serializable{
	private String IP;
	private int port;	

	/**
	 * Constructor for peer initialisation
	 * @param iP for the IP to set
	 * @param port for the port to set
	 */
	public Peer(String iP, int port){
		IP = iP;
		this.port = port;
	}
	/**
	 * @return the iP
	 */
	public String getIP() {
		return IP;
	}
	/**
	 * @param iP the iP to set
	 */
	public void setIP(String iP) {
		IP = iP;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
}
