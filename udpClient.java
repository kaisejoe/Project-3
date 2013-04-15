import java.io.*;
import java.net.*;

class udpClient{

	String filename;
	ArrayList<Byte[]> packetData;

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
		fileName = inFromUser.readLine();
		byte[] sendData = new byte[1024];
		//what if the filename takes more than 1024 bytes? i highly doubt this but it
		////seems like we should have to make a loop that will just copy the bytes into
		////the array into place. otherwise, the server may be waiting for bytes that it
		////won't receive if the fileName.getBytes is less than 1024
		sendData = fileName.getBytes();
	
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		clientSocket.send(sendPacket);
		
		byte[] recvData = new byte[1024];
		DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
		clientSocket.receive(recvPacket);
		recvData = recvPacket.getData();
		
		int packetCount = analyzePacketTotal(recvData);
		
		//this should keep receiving data, then put it in the arraylist based
		//on its sequence number - Brett
		DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
		for(int i=0; i<packetCount; i++){	
			byte[] recvData = new byte[1024];
			clientSocket.receive(recvPacket);
			recvData = recvPacket.getData();
			
			byte[] actualData = analyzeData(recvData);
			
			if(checkCheckSum)
				packetData.add(analyzeSeqNum(recvData), actualData);
		}
		
		//after we've tried every packet, we only need to retry the packets that didn't 
		////yield the correct checksum - brett
		ArrayList<Integer> missingPackets = ArrayList<Integer>();
		missingPackets = checkData(packetData);
		int missingCount = missingPackets.size();
		
		do{
			String missingString = "";
			for(int i=0; i<missingCount; i++){
				missingString+=missingPackets.get(i)+",";
			}
			sendData = missingString.getbytes();
			
			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);
		
			for(int i=0; i<missingCount; i++){	
				byte[] recvData = new byte[1024];
				clientSocket.receive(recvPacket);
				recvData = recvPacket.getData();
			
				byte[] actualData = analyzeData(recvData);
			
				if(checkCheckSum)
					packetData.add(analyzeSeqNum(recvData), actualData);
			}
		
			missingPackets = checkData(packetData);
			int missingCount = missingPackets.size();
		} while(missingCount>0);
		
		
		//I don't know what this is supposed to do. the server isn't really saying anything,
		////it's receiving a file. - Brett
		String newMessage = new String(recvData);
		System.out.println("Server says: " + newMessage);
		
		clientSocket.close();
	}
	
	private boolean checkCheckSum(int checkSum, byte[] recvData){
		int recvCheckSum = checkSum;
		int ourCheckSum = 0;
		for(int i =0; i < recvData.length; i++){
			if(recvData[i]==1)
			ourCheckSum += (int)recvData[i];
		}
		
		if(ourCheckSum == recvCheckSum){
			return true;
		}else{
			return false;
		}	
	}
	
	//Get data from packet
	private byte[] analyzeData(byte[] recvData){
		byte[928] data;
		for(int i = 32; i < recvData.length; i++){
			data[i] = recvData[i];	
		}
		return data;
	}
	
	//Get sequence number header from packet
	private int analyzeSeqNum(byte[] recvData){
		byte[16] data;
		for(int i = 0; i < 16; i++){
			data[i] = recvData[i];	
		}
		//found this online for convering from byte[] to int
		//http://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java
		// - Brett
		int index = 0;
    		int value = data[index++] << Byte.SIZE * 3;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 2;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 1;
    		value ^= (data[index++] & 0xFF);
    		return value;
	}
	
	//Get packet total header from packet
	private int analyzePacketTotal(byte[] recvData){
		byte[16] data;
		for(int i = 16; i < 32; i++){
			data[i] = recvData[i];	
		}
		//found this online for convering from byte[] to int
		//http://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java
		// - Brett
		int index = 0;
    		int value = data[index++] << Byte.SIZE * 3;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 2;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 1;
    		value ^= (data[index++] & 0xFF);
    		return value;
	}
	
	//Write the values we've received to filename
	private void writeToFile(){  //throws IOException? or just try/catch?
		try{
		FileOutputStream fos = new FileOutputStream(new File(filename));
		for(ArrayList<Byte> b : packetData){
			fos.write(b.toArray());
		}
		}catch(IOException e){System.out.println("Error writing to file "+filename);}
	}
	
	private ArrayList<Integer> checkData(ArrayList<Byte[]> packetData){
		ArrayList<Integer> missing = new ArrayList<Integer>();
		int size = packetData.size();
		for(int i=0; i<size; i++){
			if(packetData.get(i)==null)
				missing.add(i);
		}
		return missing;
	}
}
