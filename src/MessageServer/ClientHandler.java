/*Madhura Hegde

 * 1001733463
 * 
 */
package MessageServer;
//importing all packages
import java.io.*;
import java.util.*;
import java.net.*;
import java.security.Timestamp;
import java.text.SimpleDateFormat;

public class ClientHandler extends Thread {
	//initailse data input and output stream
	final DataInputStream dis;
	final DataOutputStream dos;
	//initailse username and client 
	final String username;
	final Socket client;
	//declare message splitters to identify the message types
	static String MesgSplitter = "8888";
	static String MesgSplitter2 = "9999";
	static String MessageIdentifier = "INB";
	boolean isloggedin;
	static String dirPath="C:\\Users\\Public\\MessageServer\\";
/*https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/ 
 * used for intialising constructor
 */
	// constructor
	public ClientHandler(Socket client, DataInputStream dis, DataOutputStream dos, String username) {
		this.client = client;
		this.dis = dis;
		this.dos = dos;
		this.username = username;
		this.isloggedin = true;
	}

	@Override
	public void run() {
		String listString;
		String SpiltString[];
		String SpiltString2[];
		List<String> SendtoClients = null;
		//continously do this action until connection closer is requested for that client
		while (true) {
			//send the connection established message along with the list of usernames currently connected
			listString = "Connection established" + MesgSplitter;

			for (String s : ServerGUI.AllUserNames) {
				listString += s + ",";
			}
			try {
				dos.writeUTF(listString);
				System.out.println("sent" + listString);
				//if clients requests for connection closure, remove that client from the active client list
				listString = dis.readUTF();
				
				if (listString.equals("exit_client")) {
					ServerGUI.removeClient(username);
				}
				else if(listString.contains("CHCKNEWMSG")) {
					File newDir = new File(dirPath);
					String data="",filepath="";
					 if(newDir.isDirectory()==false) {
					 		createDirectory();
					 	}
						if(newDir.isDirectory()) {
							File f = new File(dirPath+username+".txt"); 
							File f2=new File(dirPath+username+"_INB.txt"); 
							filepath=dirPath+username+".txt";
							if(!f.exists()) {
								createFile(filepath);
							}
							if(f.exists()) {
								
								 data=ReadFromFile(filepath);
							}
							if(data.equals("") || data.isEmpty() || data == null)
								data="no new messages"+"CHCKNEWMSG";
								
							else 
								data=data+"CHCKNEWMSG";
							filepath=dirPath+username+"_INB.txt";
							if(!f2.exists()) {
								
								createFile(filepath);
							}
							
							String data2=ReadFromFile(filepath);
							data=data+data2;
							sendMessage(data);
							System.out.println("checking new message");
					
						}
				}
				else if(listString.contains("CHCKDNEWMSG")) {
					ClearFile(username);
				}
				else {
					/* https://www.geeksforgeeks.org/split-string-java-examples/ 
					 * referenced to split messages
					 */
					System.out.println("received" + listString);
					SpiltString = listString.split(MesgSplitter);
					System.out.println("split option " + SpiltString[0]);
					
					//if(SpiltString[1].contains(MesgSplitter2))
					SpiltString2 = SpiltString[1].split(MesgSplitter2);
					String MessageToSend = SpiltString2[1];
					/* create directory if not exists and create file*/
					
					/*if option 1 is chosen, formulate the message and the list of clients 
					 * for whoom it should be sent  and call multicast method*/
					if (SpiltString[0].equals("1")) {
						System.out.println("option1 ");
						SendtoClients = Arrays.asList(SpiltString2[0].split(","));
						System.out.println(SendtoClients.get(0));
						multiCast(SendtoClients, MessageToSend);
					} 
					/* if option 0 is selected, then formualate the message to be sent and the user and call unicast method*/
					else if (SpiltString[0].equals("0")) {
						System.out.println("option0 ");
						SendtoClients = Arrays.asList(SpiltString2[0].replaceAll("\\s", ""));
						System.out.println(SendtoClients.get(0));
						uniCast(SendtoClients.get(0), MessageToSend);
					} 
					/* if option 2 is selected then formulate the message and call broadcast
					 * */
					else if (SpiltString[0].equals("2")) {
						System.out.println("option2 ");
						broadCast(MessageToSend);
					}

				}
					
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}

	}
	/*
	 * this method reads the data from a file and returns the data that is being read
	 */
	String ReadFromFile(String filepath) {
		String data="";
		try {
		      File myObj = new File(filepath);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		         data = data+myReader.nextLine()+'\n';
		         
		      }
		      myReader.close();
		      
		      
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		return data;
	}
	/*
	 * this method sends messages to the clients
	 */
	void sendMessage(String data) {
		
		try {
			System.out.println("sending data"+data);
			dos.writeUTF(data);
		}
		catch(Exception e) {
			
		}
	}
	/*
	 * clears all the data from file. This clears the file which was used to store the new messages 
	 */
	void ClearFile(String username) {
		File newDir = new File(dirPath);
		if(newDir.isDirectory()) {
				File f = new File(dirPath+username+".txt"); 
				
				if(f.exists()) {
					try {
					      FileWriter myWriter = new FileWriter(f);
					      
					      myWriter.write("");
					      myWriter.close();
					      System.out.println("Successfully wrote to the file.");
					    } catch (IOException e) {
					    	
					      System.out.println("An error occurred.");
					      e.printStackTrace();
					    }
				}
		}
	}
/*
 * Write message into files of all users who are atleast once connected to the server */
	 void broadCast(String Message) throws IOException {
		 for (int i = 0; i < ServerGUI.AllUserNames.size(); i++) {
			 	
				commonFileStorage(Message,ServerGUI.AllUserNames.get(i),username);
			}
	}

	
/* write message into file of that particular recipient
 * 
 *  */
	 void uniCast(String sendMessageTo, String Message) throws IOException {

		
		if (ServerGUI.AllUserNames.contains(sendMessageTo)) {
			System.out.println("unicast");
			commonFileStorage(Message,sendMessageTo,username);
			
			//send.writeUTF(MessageIdentifier + MesgSplitter + username + MesgSplitter + Message);
		}
	}
/* 
 * write messages into files of all recipeints */
	 void multiCast(List<String> SendtoClients, String MessageToSend) throws IOException {
		
		for (int i = 0; i < SendtoClients.size(); i++) {
			if (ServerGUI.AllUserNames.contains(SendtoClients.get(i))) {
				System.out.println("multi cast clients "+SendtoClients.get(i));
				commonFileStorage(MessageToSend,SendtoClients.get(i),username);
			}

		}
		
	}
	 /*
	  * This method creates a directory if it does not exist
	  */
	 static void  createDirectory() {
		 boolean isCreated = false;

		// create File object
		File newDir = new File(dirPath);

		try {
			isCreated = newDir.mkdir();
		} catch (SecurityException Se) {
		System.out.println("Error while creating directory in Java:" + Se);
		}

		
	}
	 /*
	  * This method creates a file if it does not exist in the given directory
	  * https://www.w3schools.com/java/java_files_create.asp
	  */
	 static void createFile(String filePath) {
		 boolean flag = false;
		 
		 File textFile=new File(filePath);
		 try {
			    flag = textFile.createNewFile();
			} catch (IOException ioe) {
			     System.out.println("Error while Creating File in Java" + ioe);
			}
	 }
	 /*
	  * This method writes data to a file which exists in the given directory
	  * https://mkyong.com/java/how-to-get-current-timestamps-in-java/
	  */
	 boolean writeToFile(File textFile,String name,String fileData, String username) {
		 	boolean isWriteToFile=false;
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		      //Timestamp timestamp = new Timestamp();
			 long timestamp = System.currentTimeMillis();
			 try {
			      FileWriter myWriter = new FileWriter(textFile,true);
			      
			      myWriter.write(username+":"+fileData+":"+sdf.format(timestamp)+'\n');
			      myWriter.close();
			      System.out.println("Successfully wrote to the file.");
			    } catch (IOException e) {
			    	isWriteToFile=false;
			      System.out.println("An error occurred.");
			      e.printStackTrace();
			    }
			 return isWriteToFile;
		 }
		
	 
	 //https://javarevisited.blogspot.com/2011/12/create-file-directory-java-example.html
	 /*The method below creates directory, files and call write to file method
	  * 
	  * https://www.techiedelight.com/check-if-a-directory-exists-in-java/
	  */
	 void commonFileStorage(String fileData,String sendMessageTo,String username) {
		 File newDir = new File(dirPath);
		 boolean isWrittenToFile=false;
		 	if(newDir.isDirectory()==false) {
		 		createDirectory();
		 	}
			if(newDir.isDirectory()) {
				File f = new File(dirPath+sendMessageTo+".txt"); 
				File f2=new File(dirPath+sendMessageTo+"_INB.txt"); 
				
				if(!f.exists()) {
					String filepath=dirPath+sendMessageTo+".txt";
					createFile(filepath);
				}
				if(!f2.exists()) {
					String filepath=dirPath+sendMessageTo+"_INB.txt";
					createFile(filepath);
				}
				if(f.exists() && f2.exists()) {
					isWrittenToFile=writeToFile(f,sendMessageTo,fileData,username);
					boolean isWrittenToFile2=writeToFile(f2,sendMessageTo,fileData,username);
					if(isWrittenToFile==true && isWrittenToFile2==true) {
						System.out.println("write to file success");
					}
				}
				
				
			}
	}
	 
}
