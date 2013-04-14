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
			
			
			byte[][] fragSendData = fragmentData(sendData);
			int totalNumberOfPackets = fragSendData.length;
			
			for(int i = 0; i < totalNumberOfPackets; i++){
				DatagramPacket sendPacket = new DatagramPacket(fragSendData[i], fragSendData[i].length, IPAddress, port);
				serverSocket.send(sendPacket);
			}		
			
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
	//928 bits of data = 116 bytes	
	// Also adds in the extra data for the extended header
	private static byte[][] fragmentData(byte[] sendData){	    
		int length = sendData.length;
    		int numOfFrags = (length / 116) + 1;
		byte[][] fragData = new byte[numOfFrags][120];
		for(int counter = 0; counter < length; counter++){
			byte[] seqNum = ByteBuffer.allocate(2).putInt(counter).array();
			byte[] totalNum = ByteBuffer.allocate(2).putInt(numOfFrags).array();
			fragData[counter][0] = seqNum[0];
			fragData[counter][1] = seqNum[1];
			fragData[counter][2] = totalNum[0];
			fragData[counter][3] = totalNum[1];
			for(int counter2 = 4; counter2 < 120; counter2++){
				fragData[counter][counter2] = sendData[counter];
			}
		}
		return fragData;		
	}
}
