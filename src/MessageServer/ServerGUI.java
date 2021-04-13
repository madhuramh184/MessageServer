/**Madhura Hegde

 * 1001733463
 * 
 */
package MessageServer;
//importing required packages
import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.Font;
import javax.swing.JLabel;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Color;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class ServerGUI {
	//declaring variables for GUI
	private JFrame ServerWindow;
	private static JLabel ClientName;
	private static JList CurrentClients;
	private static JButton ExitServer;
	private static JList AllUserLists;
	private JLabel CurrentClientsLabel;
	static DefaultListModel DML0;
	static String dirPath="C:\\Users\\Public\\MessageServer\\";
	
	//declare list to store connected clients data(input output stream and username)
	static Map<String, ArrayList<Object>> ConnectedClients = new HashMap<String, ArrayList<Object>>();
	//declare list to maintain all usernames in that session
	static ArrayList<String> AllUserNames = new ArrayList<String>();
	//declare list of current clients username
	static ArrayList<String> ActiveClients = new ArrayList<String>();
	// declare Socket that listens to client continously
	private static ServerSocket listener = null;
	

	public static void main(String[] args) throws Exception {
		//start the window as soon as the program is run
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.ServerWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		String username = null;
/* https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/ 
 * used for input, output stream and socket initialisation. to sending message to client and receiving the messages*/
		try {
			System.out.println("Starting Server on port 9999!");
			//initialise socket to listen on port 8080
			listener = new ServerSocket(8080);

			while (true) {
				//server continously listens to the client
				Socket client = listener.accept();
				System.out.println("Client is" + client);
				//declaring server input output streams for each client
				DataInputStream dis = new DataInputStream(client.getInputStream());
				DataOutputStream dos = new DataOutputStream(client.getOutputStream());
				//do the below until valid username is read from client
				while (true) {
					//reads the username from client through input stream
					username = dis.readUTF();
					//if user has sent request to disconnect, remove him from the current clients list
					if (username.equals("exit_client")) {
						removeClient(username);

					} 
					// else verify the username and create thread 
					else {
						System.out.println("read user name successfully" + username);
						//displaying current incoming client
						ClientName.setText(username);
						//if the username is not present in the active clients list, then add the user.
						if (!ActiveClients.contains(username)) {
							ActiveClients.add(username);
							//if username is new, update the allusernames list as well
							if (!AllUserNames.contains(username)) {
								AllUserNames.add(username);
								
							}
							//AddUserNamesToFile(username);
								
							//System.out.println("client is connected");
							//System.out.println("Assigning new thread to client");
							//storing the client information like (username,input output stream)
							ArrayList<Object> ClientData = new ArrayList<Object>(3);
							ClientData.add(dis);
							ClientData.add(dos);
							ClientData.add(client);
							ConnectedClients.put(username, ClientData);
							//System.out.println("ActiveClients" + ActiveClients);
							//if successfull connection established, stop listening to usernames
							break;
						}//if username entered by client is already in use, notify the user
						else {
							System.out.println("username already exists");
							dos.writeUTF("Username already exists.Please enter new username");
						}
					}

				}
				/* https://www.programcreek.com/java-api-examples/?class=javax.swing.JList&method=setModel 
				 * used for setting up the jlist using setmodel */
				//setting the GUI to show active and all usernames in that session
				DML0 = new DefaultListModel();
				DML0.addAll(ActiveClients);
				CurrentClients.setModel(DML0);
				DML0 = new DefaultListModel();
				DML0.addAll(AllUserNames);
				AllUserLists.setModel(DML0);

				// create a new thread object;
				Thread t = new ClientHandler(client, dis, dos, username);
				t.start();

			}
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			// JOptionPane.showMessageDialog(null, "Connection closed", "Successful",
			// JOptionPane.INFORMATION_MESSAGE);
		}

	}
	


	/**
	 * Create the application.
	 */
	//setting up the GUI
	public ServerGUI() {
		
		initialize();

	}
	/* https://www.javatpoint.com/java-joptionpane
	 used for popping the dialog box */
	//showing closing server message on click of window close button and shutting the program
	private void terminateWindow() {
		JOptionPane.showMessageDialog(null, "Shut down server", "Successful", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
	/*when the clients informs server about disconnection, remove that client from active list and 
	 * show up the updated active client on GUI
	
	*/
	static void removeClient(String username) {
		if (ConnectedClients.containsKey(username)) {
			ConnectedClients.remove(username);
			ActiveClients.remove(username);
			System.out.println("Active clients after removal"+ActiveClients);
			DML0=new DefaultListModel();
			CurrentClients.setModel(DML0);
			DML0.addAll(ActiveClients);
			CurrentClients.setModel(DML0);
		}
	}
	/*When server is being closed, send a broadcast message to all connected clients and close the clients*/
	private void closeServer() {
		if (listener != null && !listener.isClosed()) {
			try {
				for (String name : ConnectedClients.keySet()) {
					System.out.println("key: " + name);
					ArrayList<Object> ClientData = new ArrayList<Object>();
					ClientData = ConnectedClients.get(name);
					DataOutputStream send = (DataOutputStream) ClientData.get(1);
					send.writeUTF("SERVER DOWN");

				}
				//close the socket 
				listener.close();

			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		ServerWindow = new JFrame();
		ServerWindow.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 30));
		ServerWindow.getContentPane().setLayout(null);
		ServerWindow.setBounds(100, 100, 798, 439);
		ServerWindow.getContentPane().setLayout(null);
		ServerWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JLabel IncomingClient = new JLabel("IncomingClient");
		IncomingClient.setFont(new Font("Tahoma", Font.PLAIN, 16));
		IncomingClient.setBounds(51, 75, 184, 40);
		ServerWindow.getContentPane().add(IncomingClient);

		ClientName = new JLabel("ClientName");
		ClientName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		ClientName.setBounds(51, 140, 125, 35);
		ServerWindow.getContentPane().add(ClientName);

		CurrentClients = new JList();
		CurrentClients.setEnabled(false);
		CurrentClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		CurrentClients.setFont(new Font("Tahoma", Font.PLAIN, 16));
		CurrentClients.setBounds(447, 151, 279, 205);
		ServerWindow.getContentPane().add(CurrentClients);
		
		JLabel AllUsers = new JLabel("All Users");
		AllUsers.setFont(new Font("Tahoma", Font.PLAIN, 18));
		AllUsers.setBounds(186, 73, 233, 49);
		ServerWindow.getContentPane().add(AllUsers);
		
		AllUserLists = new JList();
		AllUserLists.setBounds(186, 140, 233, 215);
		ServerWindow.getContentPane().add(AllUserLists);
		
		CurrentClientsLabel = new JLabel("Current Clients");
		CurrentClientsLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		CurrentClientsLabel.setBounds(447, 73, 279, 49);
		ServerWindow.getContentPane().add(CurrentClientsLabel);
		
		JButton ExitButton = new JButton("Exit");
		ExitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeServer();
				terminateWindow();
			}
		});
		ExitButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		ExitButton.setBounds(614, 33, 89, 23);
		ServerWindow.getContentPane().add(ExitButton);
		
		/*On click of window close button, shut down the server*/
		ServerWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
				
			}
		});

	}
}
