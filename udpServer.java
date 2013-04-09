import java.io.*;
import java.net.*;

class udpServer{
	public static void main(String args[]) throws Exception{
		DatagramSocket serverSocket = new DatagramSocket(9876, "148.61.112.72");
		while(true){
			byte[] recvData = new byte[1024];
			byte[] sendData = new byte[1024];
			
			DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
			serverSocket.receive(recvPacket);
			
			String fileName = new String(recvPacket.getData()).trim();
			String newMessage = fileName + " - Sending shortly! \n";
			
			InetAddress IPAddress = recvPacket.getAddress();
			
			int port = recvPacket.getPort();
			
			sendData = newMessage.getBytes();
			makeCheckSum(sendData);
			
			
			
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
		}
	}
	
	public int makeCheckSum(byte[] sendData){
		byte[] data = sendData;
		int checkSum = 0;
		
		for(int i = 0; i < data.length(); i++){
			checkSum += (int)data[i];			
		}
		
		return checkSum;
	}
}
