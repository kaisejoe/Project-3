import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

class udpServer{
  public static void main(String args[]) throws Exception{
		DatagramSocket serverSocket = new DatagramSocket(9876);
		while(true){
			byte[] recvData = new byte[1016];
			byte[] sendData;// = new byte[1024];

			DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
			serverSocket.receive(recvPacket);

			String fileName = new String(recvPacket.getData()).trim();
			System.out.println(fileName);
			
			try{
				File file = new File(fileName);	
				sendData = Files.readAllBytes(file.toPath());

				InetAddress IPAddress = recvPacket.getAddress();

				int port = recvPacket.getPort();

			
				//int checkSum = makeCheckSum(sendData);

				byte[][] fragSendData = fragmentData(sendData);
				int totalNumberOfPackets = fragSendData.length;
				System.out.println("Num packets: " + totalNumberOfPackets);
				for(int i = 0; i < totalNumberOfPackets; i++){
					DatagramPacket sendPacket = new DatagramPacket(fragSendData[i], fragSendData[i].length, IPAddress, port);
					serverSocket.send(sendPacket);
				}	

				//receive more data to check for missing or incorrect packet transmission
				int numLeft = 1;
				while(numLeft > 0){
					System.out.println("Entered resend cycle...");
					serverSocket.receive(recvPacket);
					String missing = new String(recvPacket.getData()).trim();
				
					System.out.println(missing);

					List<String> missingList = new ArrayList<String>(Arrays.asList(missing.split(",")));
					numLeft = missingList.size();

					if(numLeft>0){
						for(int i = 0; i < numLeft; i++){
							try{
								int packetNum = Integer.parseInt(missingList.get(i));
								DatagramPacket sendPacket = new DatagramPacket(fragSendData[packetNum], fragSendData[packetNum].length, IPAddress, port);
								serverSocket.send(sendPacket);
							}catch(NumberFormatException b){
								missingList.remove(i);
								numLeft--;
							}
						}
					}
				}
				System.out.println("Left resend cycle...");
			}catch(FileNotFoundException e){
				System.out.println("Invalid file.");
			}catch(SocketTimeoutException v){
				System.out.println("Timeout.");
			}
		}
	}

	//changed the copy of the array to make sure there was no pointer issue. - brett
	//changed the checksum value. even if the client received incorrect data, it would've all
	////been counted and would've appeared to be correct.
	private static int makeCheckSum(byte[] sendData){
		long checksum = 0;
		for (int i = 0; i < sendData.length; i++) {
			checksum += (sendData[i] & 0xFF);
		}
		checksum = checksum & 0xFFFFFFFFL;
		return (int)checksum;
	}

	//Method that fragments data into appropriate sizes in an array
	//928 bits of data = 116 bytes	
	// Also adds in the extra data for the extended header
	private static byte[][] fragmentData(byte[] sendData){	    
		int pos = 0;
		int length = sendData.length;
		System.out.println(length);
    	short numOfFrags = (short)((length / 1012) + 1);
		byte[][] fragData = new byte[numOfFrags][1016];
		for(short counter = 0; counter < numOfFrags; counter++){
			int counter2 = 4;
			while(pos < length && counter2 < 1016){
			//for(int counter2 = 4; counter2 < 1016; counter2++){
				fragData[counter][counter2] = sendData[pos];
				counter2++;
				pos++;
			}
			byte[] seqNum = ByteBuffer.allocate(2).putShort(counter).array();
			byte[] totalNum = ByteBuffer.allocate(2).putShort(numOfFrags).array();
			fragData[counter][0] = seqNum[0];
			fragData[counter][1] = seqNum[1];
			fragData[counter][2] = totalNum[0];
			fragData[counter][3] = totalNum[1];
			
			/*int check = makeCheckSum(fragData[counter]);
			byte[] checkSum = ByteBuffer.allocate(4).putInt(check).array();
			
			System.out.println("Checksum: " + check);		
			
			fragData[counter][4] = checkSum[0];
			fragData[counter][5] = checkSum[1];
			fragData[counter][4] = checkSum[2];
			fragData[counter][5] = checkSum[3];*/
			System.out.println("Num frags: " + numOfFrags);
		}
		return fragData;		
	}
}
