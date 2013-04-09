import java.io.*;
import java.net.*;

class udpClient{

	String filename;
	ArrayList<ArrayList<Byte>> packetData;

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
	
	private boolean checkCheckSum(int checkSum, byte[] recvData){
		int recvCheckSum = checkSum;
		int ourCheckSum = 0;
		for(int i =0; i < recvData.length; i++){
			ourCheckSum += (int)recvData[i];
		}
		
		if(ourCheckSum == recvCheckSum){
			return true;
		}else{
			return false;
		}
		
	}
	//Get data from packet
	private byte[] getData(byte[] recvData){
		byte[928] data;
		for(int i = 32; i < recvData.length; i++){
			data[i] = recvData[i];	
		}
		return data;
	}
	//Get ext. header from packet
	private byte[] getExtHeader(byte[] recvData){
		byte[32] data;
		for(int i = 0; i < 32; i++){
			data[i] = recvData[i];	
		}
		return data;
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
}
