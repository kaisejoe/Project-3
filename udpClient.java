import java.io.*;
import java.net.*;

class udpClient{

  public static void main(String args[]) throws Exception{
		DatagramSocket clientSocket = new DatagramSocket();
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Enter a message to send:");
		String message = inFromUser.readLine();
		byte[] sendData = new byte[1024];
		sendData = message.getBytes();
		
		InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		clientSocket.send(sendPacket);
		
		byte[] recvData = new byte[1024];
		DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
		clientSocket.receive(recvPacket);
		
		String newMessage = new String(recvData);
		System.out.println("Message from server:\n" + newMessage);
		clientSocket.close();
	}
}
