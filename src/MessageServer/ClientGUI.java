/*Madhura Hegde

 * 1001733463
 * 
 */
package MessageServer;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.JRadioButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTextArea;
import javax.swing.DropMode;
public class ClientGUI {
	//declaring variables for GUI
	private static JLabel ConnectionMsg;
	private JFrame frame;
	private static JList<ArrayList<String>> ClientList;
	private static JTextField UserName;
	private static JLabel IncorrectSelectMsg;
	private static  JList<ArrayList<String>> MessageTypes;
	private static JTextArea Recipients;
	private static JTextArea InBox;
	private static JButton CheckNewMsg;
	private static JButton btnNewButton;
	static JLabel ErrorMsg;
	static JTextArea NewMsgBox;
	//declaring input output stream
	static DataInputStream dis;
	static DataOutputStream dos;
	//declaring client socket
	static Socket s=null;
	//message splitters to send identify messages
	static String   MesgSplitter="8888";
	static String MesgSplitter2="9999";
	static String MessageIdentifier="INB";
	static List <String> Users;
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//starting the GUI when the application is opened
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI window = new ClientGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		/* https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
		 * used for initialising input, output stream, client socket, sending and receiving messages to and from server
		 */
		try {
			//initailsing the socket to listen on current host at port 8080
			s=new Socket( "localhost", 8080 );
			System.out.println("sent connection request");
			//initalising input output streams
			dis=new DataInputStream(s.getInputStream());
			dos=new DataOutputStream(s.getOutputStream());
			//initalising variable to send and receive messages between client and server
			String ServerMessage;
			while(true) {
				//declare variable to hold the list of usernames in that session sent by the server
				String AllUsers[];
				//read the message from the server
				ServerMessage=dis.readUTF();
				//identify list of usernames sent by the server 
				String Message[]=ServerMessage.split(MesgSplitter);
				//display the usernames on the GUI
				ConnectionMsg.setText(Message[0]);
				//check if there are any clients connected before you and separate the names 
				if(Message.length>1)
					AllUsers=Message[1].split(",");
				else
					AllUsers=null;
				//if connection established message is received from server, then display the message types and client list to the 
				if(Message[0].contains("Connection"))
				{
					DefaultListModel DML=new DefaultListModel();
					 Users = Arrays.asList(AllUsers); 
					DML.addAll(Users);
					ClientList.setModel(DML);
					btnNewButton.setEnabled(false);
					btnNewButton.setFocusable(false);
					UserName.setFocusable(false);
					CheckNewMsg.setEnabled(true);
					DML=new DefaultListModel();
					DML.addElement("one to one");
					DML.addElement("one to many");
					DML.addElement("one to all");
					MessageTypes.setModel(DML);	
					
					break;
				}
					
			}
			while(true) {
				String SplitMessage[];
				//read for any incoming messages
				System.out.println("listening");
				ServerMessage=dis.readUTF();
				System.out.println("Inbox Message"+ServerMessage);
				
				//if any user is offline, then server sends offline message and dispplay that to user
				if(ServerMessage.contains("offline")) {
					ErrorMsg.setText(ServerMessage);
				}
				//incase if server is down, close the client connection
				else if(ServerMessage.equals("SERVER DOWN"))
				{
					ErrorMsg.setText(ServerMessage);
					JOptionPane.showMessageDialog(null, "Connection closed", "Successful", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
				/*the server sends the messages when client asks for checking new messages
				split the received messages and show on the screen
				send notification to the server that new messages are seen*/
				else if(ServerMessage.contains("CHCKNEWMSG")) {
					System.out.println("checking new message");
					SplitMessage=ServerMessage.split("CHCKNEWMSG");
					System.out.println("new message "+SplitMessage[0]);
					
					String newMsg = SplitMessage[0];
					String oldMsg = "";
					
					NewMsgBox.setText(newMsg);
					
					if(SplitMessage.length>1) {
						oldMsg = SplitMessage[1];
					}
				
					if(!oldMsg.equals("") && !oldMsg.isEmpty() && oldMsg != null) {
						InBox.setText(oldMsg);
					}
					
					sendMessage("CHCKDNEWMSG");
				}
			}
			
		}
		catch(Exception e) {
			
			//e.printStackTrace();
		}
		
		
	}
	/* function call that sends message to server*/
	static void sendMessage(String Message) {
		System.out.println("sending checknew message request");
		try {
			dos.writeUTF(Message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public ClientGUI() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		
		frame.setBounds(0, 0, 1183, 683);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Client ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setBounds(23, 11, 327, 24);
		frame.getContentPane().add(lblNewLabel);
		
		UserName = new JTextField();
		UserName.setBounds(44, 119, 384, 46);
		frame.getContentPane().add(UserName);
		UserName.setColumns(10);
		
		JLabel EnterUserName = new JLabel("Enter the user name");
		EnterUserName.setFont(new Font("Tahoma", Font.PLAIN, 20));
		EnterUserName.setBounds(44, 78, 371, 30);
		frame.getContentPane().add(EnterUserName);
		
		 btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			/*On click of login button, send the username to the server*/
			public void actionPerformed(ActionEvent e) {
				try {
					//System.out.println("Username is"+UserName.getText());
					dos.writeUTF(UserName.getText());
					System.out.println("username sent successfully");
				}
				catch(Exception e1) {
					
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnNewButton.setBounds(467, 119, 117, 35);
		frame.getContentPane().add(btnNewButton);
		
		ConnectionMsg = new JLabel("");
		ConnectionMsg.setFont(new Font("Tahoma", Font.PLAIN, 18));
		ConnectionMsg.setBounds(44, 176, 443, 35);
		frame.getContentPane().add(ConnectionMsg);
		
		JLabel MessageOptions = new JLabel("Select one of the message options");
		MessageOptions.setFont(new Font("Tahoma", Font.PLAIN, 18));
		MessageOptions.setBounds(44, 255, 271, 28);
		frame.getContentPane().add(MessageOptions);

		IncorrectSelectMsg = new JLabel("");
		IncorrectSelectMsg.setFont(new Font("Tahoma", Font.PLAIN, 18));
		IncorrectSelectMsg.setBounds(266, 370, 384, 46);
		frame.getContentPane().add(IncorrectSelectMsg);
		
		
		ClientList = new JList(ServerGUI.ActiveClients.toArray());
		ClientList.setBackground(Color.LIGHT_GRAY);
		ClientList.setBounds(372, 307, 258, 135);
		frame.getContentPane().add(ClientList);
		
		
		
		JLabel ClientListHeader = new JLabel("All users");
		ClientListHeader.setFont(new Font("Tahoma", Font.PLAIN, 18));
		ClientListHeader.setBounds(393, 258, 191, 23);
		frame.getContentPane().add(ClientListHeader);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(44, 222, 551, 23);
		frame.getContentPane().add(separator);
		
		MessageTypes = new JList<ArrayList<String>>();
		MessageTypes.setBounds(44, 307, 243, 144);
		frame.getContentPane().add(MessageTypes);
		
		JTextArea Message = new JTextArea();
		Message.setBounds(372, 521, 308, 70);
		frame.getContentPane().add(Message);
		
		JLabel EnterMessage = new JLabel("Enter Message");
		EnterMessage.setFont(new Font("Tahoma", Font.PLAIN, 16));
		EnterMessage.setBounds(372, 482, 271, 28);
		frame.getContentPane().add(EnterMessage);
		
		
		
		ErrorMsg = new JLabel("");
		ErrorMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
		ErrorMsg.setBounds(204, 595, 302, 30);
		frame.getContentPane().add(ErrorMsg);
		
		JButton Submit = new JButton("Submit");
		/* on click of submit, validate all the data provided by user and display error message to them if there are any
		 * if all data is provided correctly, form the message to be sent and send it to the server*/
		Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Message!=null) {
					try {
							String receiver=Recipients.getText();
							System.out.println("receiver"+receiver);
							//checking for multiple recipients entry for one to one messaging
							
							if(MessageTypes.getSelectedIndex()==0 && (receiver.contains(" ") || receiver.contains(",") )) {
								System.out.println("err1");
								ErrorMsg.setText("Incorrect MessageType or Recipient");
							}
							//checking if no recipient is entered for one to one and one to many
							else if ((MessageTypes.getSelectedIndex()==0 ||MessageTypes.getSelectedIndex()==1) && (receiver.equals(null)|| receiver.isEmpty())) {
								System.out.println("err2");
								
								
								ErrorMsg.setText("Incorrect MessageType or Recipient");
								
							}
							
							else {
								//check if the message enter is empty
								if(Message.getText().isEmpty()||Message.getText()==null) 
									ErrorMsg.setText("Please enter message");
								
								else {
									//check if no message type is selcted 
									if(MessageTypes.getSelectedIndex()==-1) {
										ErrorMsg.setText("Select Message Type");
									}
									//check if the recipients are connected to server atleast once
									else {
										String errorMessage="";
										if(MessageTypes.getSelectedIndex()==1 || MessageTypes.getSelectedIndex()==0) {
											String [] SendtoClients=receiver.split(",");
											for(int i=0;i<SendtoClients.length;i++) {
												SendtoClients[i] = SendtoClients[i].trim();
												if(Users.contains(SendtoClients[i])==false) {
													
													errorMessage=errorMessage+SendtoClients[i];
												}
											}
											
										}
										System.out.println("error message"+Users.size());
										if(errorMessage!="") {
											errorMessage=errorMessage+" do not exist";
											ErrorMsg.setText(errorMessage);	
										}
										else {
											//if all data entered correctly, form the output message to be sent and sent it to the server
											System.out.println("success");
											//form the message to be sent
											receiver=MessageTypes.getSelectedIndex()+MesgSplitter+receiver+MesgSplitter2+Message.getText();
											System.out.println("Final Message"+receiver);
											//send the message
											dos.writeUTF(receiver);
											//display the message sent to the user
											ErrorMsg.setText("Message Sent");
											//clear the message text area
											Message.setText("");
										}
										
									}									
								}
								
							}
						
						
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		Submit.setFont(new Font("Tahoma", Font.PLAIN, 16));
		Submit.setBounds(563, 602, 107, 23);
		frame.getContentPane().add(Submit);
		
		Recipients = new JTextArea();
		Recipients.setBounds(44, 529, 293, 59);
		frame.getContentPane().add(Recipients);
		
		JLabel RecipientsLabel = new JLabel("Enter Recipients");
		RecipientsLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		RecipientsLabel.setBounds(50, 489, 144, 14);
		frame.getContentPane().add(RecipientsLabel);
		
		 InBox = new JTextArea("");
		InBox.setBounds(776, 74, 271, 274);
		frame.getContentPane().add(InBox);
		InBox.setFocusable(false);
		
		NewMsgBox = new JTextArea();
		NewMsgBox.setBounds(778, 420, 269, 171);
		frame.getContentPane().add(NewMsgBox);
		NewMsgBox.setFocusable(false);
		JLabel InBoxLabel = new JLabel("Inbox");
		InBoxLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		InBoxLabel.setBounds(785, 28, 89, 30);
		frame.getContentPane().add(InBoxLabel);
		
		JButton ExitButton = new JButton("Exit");
		/* on click of exit button, display a dialog window to show the connection ended and end the program */
		ExitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeUTF("exit_client");
					JOptionPane.showMessageDialog(null, "Connection closed", "Successful", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		ExitButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		ExitButton.setBackground(Color.RED);
		ExitButton.setForeground(Color.BLACK);
		ExitButton.setBounds(1037, 16, 89, 23);
		frame.getContentPane().add(ExitButton);
		
		CheckNewMsg = new JButton("Check New Message");
		CheckNewMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
		CheckNewMsg.setBounds(786, 359, 182, 23);
		CheckNewMsg.setEnabled(false);
		frame.getContentPane().add(CheckNewMsg);
		CheckNewMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				sendMessage("CHCKNEWMSG");
			}
		});
		
		
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
}
