import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * @author ritvikverma
 * Represents the peer construct for each launched peer. Implements multithreading. 
 */
public class ImagePeer {
	private ArrayList<ImagePanel> actualImage = new ArrayList<ImagePanel>(); //ArrayList of image panels that does not get edited in the end
	private JFrame mainFrame; //Our main frame that holds the image panels, buttons and textArea
	private JPanel imagePanel = new JPanel(); //Panel of images	
	private User user;
	private String IP;
	private Socket socket;
	private ArrayList<Peer> peers = new ArrayList<Peer>();
	private ArrayList<Socket> clients = new ArrayList<>();
	private ArrayList<String> remaining = new ArrayList<>();
	private ServerSocket serversocket;
	public static void main(String args[])
	{
		ImagePeer peer = new ImagePeer();
		peer.exec();
	}	
	
	private void addAll() {
		remaining.clear();
		remaining.add("50");
		for(int i=51;i!=50;i=(i+1)%100)
			remaining.add(Integer.toString(i));
		
	}
	
	private String getIP() {
		return	JOptionPane.showInputDialog(
				mainFrame,	
				"Connection to server:",	
				"Input",
				JOptionPane.QUESTION_MESSAGE);	
	}
	private String getUsername() {
		return	JOptionPane.showInputDialog(
				mainFrame,	
				"Username:",	
				"Input",
				JOptionPane.QUESTION_MESSAGE);	
	}
	private String getPassword() {
		return	JOptionPane.showInputDialog(
				mainFrame,	
				"Password:",	
				"Input",
				JOptionPane.QUESTION_MESSAGE);	
	}
	
	private void loginFailed() {
		JOptionPane.showMessageDialog(
				mainFrame,	
				"Login Fail",	
				"Message",
				JOptionPane.INFORMATION_MESSAGE);	
	}

	private void exec() {
		IP = getIP();
		try {
		user = new User (getUsername(), getPassword(), true); //True to encrypt, otherwise if it's already encrypted, then let it be
		}
		catch (Exception e)
		{	
			loginFailed();//Prompts for login fail
			System.exit(1);//Exit with error	
		}
		if(IP == null || user.getUsername()==null || user.getHashedPassword()==null) {//If no IP or no username or not password
			loginFailed();
			System.exit(1);
		}
		connect();//If all good, then connect
	}
	
	@SuppressWarnings("unchecked")
	private void connect() {
		try {
			socket=new Socket(IP, 9000);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			oos.writeObject(user);			
			if(ois.readInt()==12345) {
				peers=(ArrayList<Peer>)ois.readObject();
				oos.writeInt(startServer());
				Thread t=new Thread(new acceptClients());
				t.start();
				structure();
				Thread p=new Thread(new p2p());
				p.start();
				getData(oos, ois);
				changes(oos, ois);
			}
			else {
				loginFailed();
				System.exit(1);
			}
		} catch (Exception e) {
			loginFailed();
			System.exit(1);
		}
	}

	private int startServer() {
		serversocket=null;
		int port=9000;
		while(serversocket==null) {
			try {
				serversocket = new ServerSocket(port);//Starts server on port 9000
			} catch (IOException e) {
				port++;
			}
		}
		return port;
	}

	private void changes(ObjectOutputStream oos ,ObjectInputStream ois) {
		try {

			if(ois.readBoolean()) {
				if(JOptionPane.showConfirmDialog(mainFrame, "Image on server updated. Update picture?")==JOptionPane.YES_OPTION) {
				getData(oos,ois);
				}
				else {
					ois.close();
					oos.close();
					serversocket.close();
				}
			}else {
				getBlock((ImagePanel)ois.readObject(),false);
				getBlock((ImagePanel)ois.readObject(),false);
			}
			changes(oos,ois);
		} catch (IOException | ClassNotFoundException e) {
			try {
				oos.reset();
				ois.reset();
				changes(oos,ois);
			} catch (IOException e1) {
			}
		}
	}

	private void getData(ObjectOutputStream oos, ObjectInputStream ois) {
		int start=(25*(peers.size()+2))%100;
		addAll();
		int end=start;
		try {
			oos.writeInt(start);
			oos.flush();
			getBlock((ImagePanel)ois.readObject());
		} catch (IOException | ClassNotFoundException e1) {

		}
//		if(peers.size()==clients.size())
			end=50;
		for(Socket s: clients) {
			int end1=end;
			Thread t=new Thread(new peerBlocks(s, end1, end1+25));
			t.start();
			end1+=25;
		}
		for (int i=start+1;i!=end;i=(i+1)%100) { //Puts all of the blocks in.
			try {
				oos.writeInt(i);
				oos.flush();
				getBlock((ImagePanel)ois.readObject());
			} catch (Exception e) {

			}
		}
		while(remaining.size()>0) {
			try {
				oos.writeInt(Integer.parseInt(remaining.get(0)));
				oos.flush();
				getBlock((ImagePanel)ois.readObject());
			} catch (Exception e) {

			}
		}
		try {
			oos.writeInt(-1);
			oos.flush();
		} catch (IOException e) {
		}
	}

	private class peerBlocks implements Runnable{
		private Socket s;
		private int start;
		private int end;
		/**
		 * @param s for socket initialisation
		 * @param start for starting integer value of blocks to get from the peer
		 * @param end for ending integer value of blocks to get from the peer
		 */
		public peerBlocks(Socket s, int start, int end) {
			this.s=s;
			this.start=start;
			this.end=end;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 * Executes after multithreading occurs
		 */
		@Override
		public void run() {
			try {
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				for (int i=0;i<=start-end;i=(i+1)%100) { //Puts all of the blocks in.
					try {
						oos.writeInt(i);
						oos.flush();
						ImagePanel p=(ImagePanel)ois.readObject();
						getBlock(p);
					} catch (Exception e) {
					}
				}
			} catch (IOException e) {
			}
		}
		
	}
	
	private void getBlock(ImagePanel block) {
		try {
			try {
				imagePanel.remove(block.getOriginalLocation());
				actualImage.remove(block.getOriginalLocation());
				remaining.remove(Integer.toString(block.getOriginalLocation()));
			}catch(Exception e) {} 
			actualImage.add(block);
			try {
				imagePanel.add(block.getJl(),block.getOriginalLocation());
			}catch(Exception e) {
				imagePanel.add(block.getJl());
			}
			imagePanel.repaint();
			imagePanel.revalidate();
		}
		
		catch(Exception e)
		{
			
		}
	}
	
	private void getBlock(ImagePanel block, boolean del) {
		try {
			try {
				if(del) {
					imagePanel.remove(block.getOriginalLocation());
					actualImage.remove(block.getOriginalLocation());
				}
			}catch(Exception e) {} 
			actualImage.add(block);
			try {
				imagePanel.add(block.getJl(),block.getOriginalLocation());
			}catch(Exception e) {
				imagePanel.add(block.getJl());
			}
			imagePanel.repaint();
			imagePanel.revalidate();
		}
		
		catch(Exception e)
		{

		}
	}
	
	/**
	 * Adds structure to the entire game through adding components within components
	 */
	private void structure() {
		mainFrame = new JFrame("Image Peer #"+(peers.size()+1));
		imagePanel.setPreferredSize(new Dimension(700,700)); //Set the preferred size	
		imagePanel.setLayout(new GridLayout(10,10,0,0)); //Sets the layout of the imagePanel, divides it into grids of various sizes with zero gap
		for(int i=0;i<100;i++)
			imagePanel.add(new JPanel());
		mainFrame.getContentPane().add(BorderLayout.NORTH, imagePanel);//According to the description
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Exits on close
		mainFrame.pack();//Lets its components be at least their preferred size
		mainFrame.setVisible(true);//We make the frame visible			
	}
	
	
	private class acceptClients implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 * For runnable, executes after multithreading occurs
		 */
		@Override
		public void run() {
			try {
				Socket clientSocket = serversocket.accept();//Accepts the client socket, creates a new Socket for the client that stores IP and port and others
				Thread t1=new Thread(new acceptClients());//Makes a thread for new parallel search
				t1.start();//Starts the new thread, using the acceptClients object and runs the overriden method after start is called
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream()); //Gets data from the client
				ObjectOutputStream oos=new ObjectOutputStream(clientSocket.getOutputStream());//Sends data to the client
				sendBlocks(oos,ois);
			} 
			catch(Exception e) 
			{
				
			}
			
		}
		
		private void sendBlocks(ObjectOutputStream oos, ObjectInputStream ois)
		{
			try {
				while(true)
				{
					int get=ois.readInt();//Read what the client wants
					if(get>=0&&get<=99)//If what we want is something legitimate
						oos.writeObject(actualImage.get(get));//Sends the actual ImagePanel object
				}
			} catch (IOException e) {
			}
		}
	}
	

	/**
	 * @author ritvikverma
	 * Implements the peer to peer functionality
	 */
	public class p2p implements Runnable {
	
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 * Implements multithreading and connects to other peers
		 */
		@Override
		public void run() {
			for(Peer p: peers) {
				try {

					Socket s = new Socket(p.getIP(), p.getPort());
					ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
					ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
					clients.add(s);
				}catch(Exception e) {
				}
			}
		}
		
	}

	
}
