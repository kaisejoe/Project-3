import java.io.*;
import java.net.*;

class udpServer{
  public static void main(String args[]) throws Exception{
		DatagramSocket serverSocket = new DatagramSocket(9876);
		while(true){
			byte[] recvData = new byte[1024];
			byte[] sendData = new byte[1024];
			DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
			serverSocket.receive(recvPacket);
			String message = new String(recvPacket.getData()).trim();
			String newMessage = "Server received: " + message + ". Thank you, come again!\n";
			InetAddress IPAddress = recvPacket.getAddress();
			int port = recvPacket.getPort();
			sendData = newMessage.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
		}
	}
}
