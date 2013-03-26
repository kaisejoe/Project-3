import java.io.*;
import java.net.*;

class udpClient{

	public static void main(String args[]) throws Exception{
		int port;
		DatagramSocket clientSocket = new DatagramSocket();
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("IP Address: ");
		String address = new String(inFromUser.readLine());
		InetAddress IPAddress = InetAddress.getByName(address);
		
		System.out.print("Port #: ");
		port = Integer.parseInt(inFromUser.readLine());
		
		System.out.print("Filename: ");
		String fileName = inFromUser.readLine();
		byte[] sendData = new byte[1024];
		sendData = fileName.getBytes();
	
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		clientSocket.send(sendPacket);
		
		byte[] recvData = new byte[1024];
		DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
		clientSocket.receive(recvPacket);
		
		String newMessage = new String(recvData);
		System.out.println("Server says: " + newMessage);
		clientSocket.close();
	}
}
