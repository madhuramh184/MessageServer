package MessageServer;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.io.*;
public class Client {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Scanner scn=new Scanner(System.in);
			//getting localhost ip
			InetAddress ip=InetAddress.getByName("localhost");
			//establish connection with server
			Socket s=new Socket(ip,9090);
			//obtaining input output streams
			ObjectInputStream dis=new ObjectInputStream(s.getInputStream());
			ObjectOutputStream dos=new ObjectOutputStream(s.getOutputStream());
			//following loop performs exchange of information between client and clienthandler
			while(true)
			{
				System.out.println(dis.readUTF());
				String username=scn.nextLine();
				dos.writeUTF(username);
				String ServerMessage=dis.readUTF();
				System.out.println(ServerMessage);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
