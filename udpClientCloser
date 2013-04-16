import java.io.*;
import java.net.*;
import java.util.*;

class udpClient{

  static String fileName;

	public static void main(String args[]) throws Exception{
		int port;
		DatagramSocket clientSocket = new DatagramSocket();
		clientSocket.setSoTimeout(2000);
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("IP Address: ");
		//String address = new String(inFromUser.readLine());
		String address = new String("148.61.112.70");
		InetAddress IPAddress = InetAddress.getByName(address);

		System.out.print("Port #: ");
		//port = Integer.parseInt(inFromUser.readLine());
		port = 9876;

		System.out.print("Filename: ");
		fileName = inFromUser.readLine();
		byte[] sendData = new byte[1016];
		//what if the filename takes more than 1024 bytes? i highly doubt this but it
		////seems like we should have to make a loop that will just copy the bytes into
		////the array into place. otherwise, the server may be waiting for bytes that it
		////won't receive if the fileName.getBytes is less than 1024
		sendData = fileName.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		clientSocket.send(sendPacket);

		byte[] recvData = new byte[1016];
		DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
		clientSocket.receive(recvPacket);
		recvData = recvPacket.getData();

		int packetCount =  (int)(analyzePacketTotal(recvData));
		ArrayList<byte[]> packetData = new ArrayList<byte[]>();
		for(int m = 0; m < packetCount; m++){
			packetData.add(null);
		}
		System.out.println("Num packets: " + packetCount + " length: " + packetData.size());

		//this should keep receiving data, then put it in the arraylist based
		//on its sequence number - Brett
		//DatagramPacket
		recvPacket = new DatagramPacket(recvData, recvData.length);
		try{
			for(int i=0; i<packetCount; i++){	
				recvData = new byte[1016];
				clientSocket.receive(recvPacket);
				recvData = recvPacket.getData();
			
			
				byte[] actualData = analyzeData(recvData);
				String temp = new String(actualData, "UTF-8");
				System.out.println("Received data: " + temp);
				//if(checkCheckSum(0, recvData))
				packetData.set((int)analyzeSeqNum(recvData), actualData);
			}
		}catch(SocketTimeoutException e){
			System.out.println("Timeout.");
		}
		System.out.println("Received all data!");

		//after we've tried every packet, we only need to retry the packets that didn't 
		////yield the correct checksum - brett
		ArrayList<Integer> missingPackets = new ArrayList<Integer>();
		missingPackets = checkData(packetData);
		int missingCount = missingPackets.size();
		String missingString;
		System.out.println("Missing count: " + missingCount);
		do{
			missingString = "";
			for(int i=0; i<missingCount; i++){
				missingString+=missingPackets.get(i)+",";
			}
			System.out.println("Missing: " + missingString);
			sendData = missingString.getBytes();

			sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);

			for(int i=0; i<missingCount; i++){	
				recvData = new byte[1016];
				clientSocket.receive(recvPacket);
				recvData = recvPacket.getData();
				System.out.println(recvData.length);

				byte[] actualData = analyzeData(recvData);

				//if(checkCheckSum(0, recvData))
					packetData.set(analyzeSeqNum(recvData), actualData);
			}

			missingPackets = checkData(packetData);
			missingCount = missingPackets.size();
			System.out.println(missingCount);
		} while(missingCount>0);


		//I don't know what this is supposed to do. the server isn't really saying anything,
		////it's receiving a file. - Brett
		//String newMessage = new String(recvData);
		//System.out.println("Server says: " + newMessage);
		
		writeToFile(packetData);
		clientSocket.close();
		
		
	}

	private static boolean checkCheckSum(int checkSum, byte[] recvData){
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
	private static byte[] analyzeData(byte[] recvData){
		byte[] data = new byte[1016];
		for(int i = 4; i < recvData.length; i++){
			data[i-4] = recvData[i];	
		}
		return data;
	}

	//Get sequence number header from packet
	private static short analyzeSeqNum(byte[] recvData){
		byte[] data = new byte[2];
		for(int i = 0; i < 2; i++){
			data[i] = recvData[i];	
		}
		//found this online for convering from byte[] to int
		//http://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java
		// - Brett
		//int index = 0;
    		short value = (short) ((data[0] << 8) | (data[1]));
    		/*int value = data[index++] << Byte.SIZE * 3;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 2;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 1;
    		value ^= (data[index++] & 0xFF);*/
			System.out.println("Seq num: " + value);
    		return value;
	}

	//Get packet total header from packet
	private static short analyzePacketTotal(byte[] recvData){
		byte[] data = new byte[2];
		System.out.println(recvData.length);
		for(int i = 2; i < 4; i++){
			data[i-2] = recvData[i];	
		}
		//found this online for convering from byte[] to int
		//http://stackoverflow.com/questions/5399798/byte-array-and-int-conversion-in-java
		// - Brett
		//int index = 0;
			short value = (short) (((data[0]) << 8) | (data[1]));
    		/*int value = data[index++] << Byte.SIZE * 3;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 2;
    		value ^= (data[index++] & 0xFF) << Byte.SIZE * 1;
    		value ^= (data[index++] & 0xFF);*/
    		return value;
	}

	//Write the values we've received to filename
	private static void writeToFile(ArrayList<byte[]> packetData){  //throws IOException? or just try/catch?
		try{
			FileOutputStream fos = new FileOutputStream(new File("temp" + fileName));
			for(byte[] b : packetData){
				fos.write(b);
			}
			fos.close();
		}catch(IOException e){System.out.println("Error writing to file " + fileName);}
	}

	private static ArrayList<Integer> checkData(ArrayList<byte[]> packetData){
		ArrayList<Integer> missing = new ArrayList<Integer>();
		int size = packetData.size();
		System.out.println(packetData.size());
		for(int i=0; i<size; i++){
			if(packetData.get(i)==null)
				missing.add(i);
		}
		return missing;
	}
}
