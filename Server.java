/*
Jude Dominic T. del Rio
2008-60129
CMSC 137 CD-4L
 */
import java.net.* ;
import java.util.HashMap;
import java.util.Random;

public class Server{

	 private final static int PACKETSIZE = 100 ;
	 private final static int SERVERPORT = 1112;
	 private final static int CLIENTPORT = 1113;
	 private final static String hostAddress="127.0.0.1";
	 private final static int WINDOWSIZE = 8;
	 
	 private static int initial=0;
	 
	 private static HashMap<String,Integer> data = new HashMap<String,Integer>();
	 private static HashMap<String,String> data2 = new HashMap<String,String>();
	 private static HashMap<Integer,String> message = new HashMap<Integer,String>();
	 

	 static boolean continued = true;
	 public static void main( String args[] ){
	      try{
	    	  InetAddress host = InetAddress.getByName(hostAddress) ;
	         // Create the socket
	         DatagramSocket socket = new DatagramSocket( SERVERPORT ) ;

	         System.out.println( "server ready" ) ;


	         for( ;; ){
	            DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;

	            socket.receive( packet ) ;
	            
	            String data3 = new String(packet.getData());
	            System.out.println(data3);
	            data3=data3.substring(1, data3.length()-(PACKETSIZE-data3.lastIndexOf('}')));
	            String[] num = data3.split(", ");
	            for(int y=0;y<num.length;y++){
	            	String[] temp = num[y].split("=");
	            	data.put(temp[0],Integer.parseInt(temp[1]));
	            }
	           
	            //increment the acknowledgment bit
	           int syn =data.get("SYN");
	           int ack = data.get("ACK");
	           data.put("ACK",ack+1);
	           int isn =data.get("ISN");
	           data.put("ACK NO",isn+1);
	           data.put("ISN",5000);
	            
	           if(syn==0 && ack==1){
	        	   initial =(data.get("SEQ NO")+1);
	        	   System.out.println("Connection established to "+packet.getPort());
	        	   break;
	           }
	            // Return the packet to the sender
	            byte [] data = data.toString().getBytes() ;
		        DatagramPacket response = new DatagramPacket( data, data.length, packet.getAddress(), packet.getPort() ) ;

		         socket.send( response ) ; 
	        }  
	         
	         DatagramPacket packet = new DatagramPacket( new byte[PACKETSIZE], PACKETSIZE ) ;
	         

	         for(;;){
	        	 	
	        	 	socket.receive( packet ) ;
		            String data3 = new String(packet.getData());
		            data3=data3.substring(1, data3.length()-(PACKETSIZE-data3.lastIndexOf('}')));
		            String[] num = data3.split(", ");
		            
		            for(int y=0;y<num.length;y++){
		            	String[] temp = num[y].split("=");
		            	data2.put(temp[0],temp[1]);
		            }
		            
		            int fin = Integer.parseInt(data2.get("FIN"));
		            if(fin == 1){
		            	continued=false;
		            	break;
		            }
		            message.put(Integer.parseInt(data2.get("SYN")), data2.get("DATA"));
		            data2.put("ACKF", Integer.toString(1));
		            byte [] data = data2.toString().getBytes() ;
			        DatagramPacket response = new DatagramPacket( data, data.length, packet.getAddress(), packet.getPort() ) ;

			         // Send it			 
			         socket.send( response ) ;
			         
			         //Print out the message from the client
			         Thread timerThread = new Thread(new Runnable() {
			        	  int cnt=0;
			        	  public void run() {
			        		  while(continued==true){
			        			  cnt++;
			        			  if(cnt%2==0){
			        				 try{
			        				  for(int key : message.keySet()) {
			        					   System.out.print(message.get(key));
			        					}
			        				 }catch(Exception m){}
			        				 System.out.println("");
			        			  }
			        			  try {
									Thread.sleep(10);
								} catch (InterruptedException e) {					
								}
			        		  }
			        	  }
			          });
			          timerThread.start();
	         }
	         
	         

	         try{
				  for(int key : message.keySet()) {
					   System.out.print(message.get(key));
					}
				 }catch(Exception m){}
				 System.out.println("");
				 
	         
	         	int currentseq = Integer.parseInt(data2.get("SYN"));
	         	currentseq++;
	            data2.put("ACKF", Integer.toString(1));
	            byte [] data = data2.toString().getBytes() ;
		        
		        DatagramPacket response = new DatagramPacket( data, data.length, packet.getAddress(), packet.getPort()) ;
		        socket.send( response ) ;
		       
		        data2.clear();
		        data2.put("SRC",Integer.toString(CLIENTPORT));
		        data2.put("DST",Integer.toString(SERVERPORT));
		        data2.put("SYN",Integer.toString(0));
		        data2.put("ACK",Integer.toString(0));
		        data2.put("SYNF",Integer.toString(1));
		        data2.put("ACKF",Integer.toString(0));
		        data2.put("FIN",Integer.toString(1));
		        data2.put("CS",Integer.toString(0));
		        data2.put("WS",Integer.toString(WINDOWSIZE));
		        data2.put("DATA",null);		         
		    
		        data = data2.toString().getBytes() ;
		        packet = new DatagramPacket( data, data.length, host, CLIENTPORT ) ;
		         socket.send( packet ) ;
		         
		         while(true){
		         socket.receive( packet ) ;		         
		         String data3 = new String(packet.getData());
		         System.out.println(data3);
		         data3=data3.substring(1, data3.length()-(PACKETSIZE-data3.lastIndexOf('}')));
		         String[] temporary = data3.split(", ");
		            
		         for(int y=0;y<temporary.length;y++){
		            	String[] temp = temporary[y].split("=");
		            	data2.put(temp[0],temp[1]);
		            }
		          if(Integer.parseInt(data2.get("ACKF"))==1){
		        	  break;
		          }
		         }
		         
		        System.out.print("closing");
		          Thread timerThread = new Thread(new Runnable() {
		        	  int cnt=0;
		        	  public void run() {
		        		  while(cnt<11){
		        			  cnt++;
		        			  System.out.print(".");
		        			  try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {					
							}
		        		  }
		        	  }
		          });
		          timerThread.start();
	     }
	     catch( Exception e )
	     {
	        System.out.println( e ) ;
	     }
	   }	 
}
