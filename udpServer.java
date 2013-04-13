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
	
	//changed the copy of the array to make sure there was no pointer issue. - brett
	//changed the checksum value. even if the client received incorrect data, it would've all
	////been counted and would've appeared to be correct.
	private int makeCheckSum(byte[] sendData){
		byte[] data = (byte[])sendData.clone();
		
		int checkSum = 0;
		
		for(int i = 0; i < data.length(); i++){
			if(data[i]==1)
				checkSum += (int)data[i];			
		}
		
		return checkSum;
	}
	
	//Method that fragments data into appropriate sizes in an array
	private byte[] fragmentData(byte[] sendData){		
		
	}
}
