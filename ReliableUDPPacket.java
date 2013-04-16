import java.nio.*;
//import org.jnetpcap.nio.*;
import org.jnetpcap.protocol.tcpip.Udp;

public class ReliableUDP extends Udp //Seems like an appropriate place to start
{
  private int totalPackets;
	private int seqNumber;

	public ReliableUDP(){
		super();

		ByteBuffer bb = ByteBuffer.allocate(2);
		byte[] buf = new byte[2];
		
		//This is the best way I could think of to do this - get the relevant bytes,
		//move them into an array, and derive the field values from there.
		
		this.transferTo(bb,8,2);
		bb.get(buf);
		seqNumber = (buf[1] << 8) + buf[0];
		
		this.transferTo(bb,10,2);
		bb.get(buf);
		totalPackets = (buf[1] << 8) + buf[0];
		
	}
	
	@Field(offset=64,length=16)
	public int seqNumber(){
		return seqNumber;
	}
	
	@Field(offset=80,length=16,description="Total number of packets in this transmission")
	public int totalPackets(){
		return totalPackets;
	} 
}
