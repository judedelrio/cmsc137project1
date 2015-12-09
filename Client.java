/*
Jude Dominic T. del Rio
2008-60129
CMSC 137 CD-4L
 */

import java.net.* ;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Client {
	private final static int PACKETSIZE = 100 ;
	private final static int SERVERPORT = 1112;
	private final static int CLIENTPORT = 1113;
	private final static String hostAddress="127.0.0.1";
	private final static int WINDOWSIZE = 8;
	private static HashMap<String,Integer> data = new HashMap<String,Integer>();
	private static HashMap<String,String> data2 = new HashMap<String,String>();
	private static HashMap<String,String> data3 = new HashMap<String,String>();
	private static LinkedList<DatagramPacket> dropPack = new LinkedList<DatagramPacket>();
	private static String sentence ="this is a sample sentence";
	private static int[] dropPackets = {0, 25, 50, 75};
	 static DatagramSocket socket = null ;
	   
	 public static void main( String args[] )
	   {
	      try{	
	         InetAddress host = InetAddress.getByName( hostAddress) ;

	         socket = new DatagramSocket(CLIENTPORT) ;
	         data.put("SYN",1);
	         data.put("ACK",0);
	         data.put("ISN",2000);
	         
	         byte [] data = data.toString().getBytes() ;
	         DatagramPacket packet = new DatagramPacket( data, data.length, host, SERVERPORT ) ;

	         socket.send( packet ) ;

	         socket.setSoTimeout( 4000 ) ;

	         byte[] receivedData = new byte[PACKETSIZE];
	         DatagramPacket receivePacket = new DatagramPacket(receivedData,receivedData.length);
	         
	         socket.receive( receivePacket ) ;
	         
	         String dataReceived = new String(receivePacket.getData());
	        

	         System.out.println(dataReceived);
	         
	         dataReceived=dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));

	            String[] num = dataReceived.split(", ");
	            for(int y=0;y<num.length;y++){
	            	String[] temp = num[y].split("=");
	            	data.put(temp[0],Integer.parseInt(temp[1]));
	            }
	            
		     data.put("SYN",0);
		     
		     int ack_no =data.get("ACK NO");
		     int isn =data.get("ISN");
		     
		     data.put("ACK NO",isn+1);
		     data.put("SEQ NO",ack_no);
		     data.remove("ISN");
		     int ident = (data.get("SEQ NO")+1);
		     data = data.toString().getBytes() ;
	         packet = new DatagramPacket( data, data.length, host, SERVERPORT ) ;

	         socket.send( packet ) ;

	         socket.setSoTimeout( 4000 ) ;
	         
	         int sub=0;
	         for(;sub<sentence.length();ident++){

	        	 data2.put("SRC",Integer.toString(CLIENTPORT));
		         data2.put("DST",Integer.toString(SERVERPORT));
		         data2.put("SYN",Integer.toString(ident));
		         data2.put("ACK",Integer.toString(0));
		         data2.put("SYNF",Integer.toString(1));
		         data2.put("ACKF",Integer.toString(0));
		         data2.put("FIN",Integer.toString(0));
		         data2.put("CS",Integer.toString(0));
		         data2.put("WS",Integer.toString(WINDOWSIZE));
		         if(sub+WINDOWSIZE>sentence.length()){
		        	 data2.put("DATA",sentence.substring(sub,sentence.length()));
		         }

		         else{
		         data2.put("DATA",sentence.substring(sub,sub+WINDOWSIZE));
		         }

		         sub=sub+8;
	        	 data = data2.toString().getBytes() ;
		         packet = new DatagramPacket( data, data.length, host, SERVERPORT ) ;

		         if(random()==true){
		        	 System.out.println(ident);
		        	 socket.send( packet ) ;
		        	 socket.receive( receivePacket ) ;
		        	 dataReceived = new String(receivePacket.getData());
		        	 System.out.println(dataReceived);
		        	 dataReceived=dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));
		        	 String[] temporary = dataReceived.split(", ");
		        	 for(int y=0;y<temporary.length;y++){
		            	String[] temp = temporary[y].split("=");
		            	data3.put(temp[0],temp[1]);
		            }
		           
		         }
		         else{
		        	 dropPack.add(packet);
		         }
	        }
	        	Thread timerThread = new Thread(new Runnable() {
		        	  int cnt=0;
		        	  public void run() {
		        		  while(!dropPack.isEmpty()){
		        			  cnt++;
		        			  if(cnt%4==0){
		        			  if(random()==true){
		      	        		
		        				  try{
		        				socket.send(dropPack.getFirst());
		      	        		dropPack.removeFirst();
		      	        		socket.receive( receivePacket ) ;
		      		        	 String dataReceived = new String(receivePacket.getData());
		      		        	 System.out.println(dataReceived);
		      		        	 dataReceived=dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));
		      		        	 String[] temporary = dataReceived.split(", ");
		      		        	 for(int y=0;y<temporary.length;y++){
		      		            	String[] temp = temporary[y].split("=");
		      		            	data3.put(temp[0],temp[1]);
		      		            }
		      		           
		        				  }catch(Exception o){}
		      	        	}
		        			  
		        			  try {
								Thread.sleep(10);
							} catch (InterruptedException e) {					
							}
		        			  }
		        		  }
		        	  }
		          });
		          timerThread.start();
	        
	        while(!dropPack.isEmpty()){
	        	
	        }

	        data2.clear();
	        data2.put("SRC",Integer.toString(CLIENTPORT));
	        data2.put("DST",Integer.toString(SERVERPORT));
	        data2.put("SYN",Integer.toString(ident));
	        data2.put("ACK",Integer.toString(0));
	        data2.put("SYNF",Integer.toString(1));
	        data2.put("ACKF",Integer.toString(0));
	        data2.put("FIN",Integer.toString(1));
	        data2.put("CS",Integer.toString(0));
	        data2.put("WS",Integer.toString(WINDOWSIZE));
	        data2.put("DATA",null);
	         
	         data = data2.toString().getBytes() ;
	         packet = new DatagramPacket( data, data.length, host, SERVERPORT ) ;

	         socket.send( packet ) ;
	        
	         socket.receive( receivePacket ) ;		         
	         dataReceived = new String(receivePacket.getData());
	         System.out.println(dataReceived);
	         dataReceived=dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));
	         String[] temporary = dataReceived.split(", ");
	            for(int y=0;y<temporary.length;y++){
	            	String[] temp = temporary[y].split("=");
	            	data3.put(temp[0],temp[1]);
	            }
	          
	          if(Integer.parseInt(data3.get("ACKF"))==1){
	        	  while(true){

	        		  socket.receive( receivePacket ) ;		         
	     	         dataReceived = new String(receivePacket.getData());

	     	         System.out.println(dataReceived);

	     	         dataReceived=dataReceived.substring(1, dataReceived.length()-(PACKETSIZE-dataReceived.lastIndexOf('}')));
	     	         String[] tempor = dataReceived.split(", ");
	     	            for(int y=0;y<tempor.length;y++){
	     	            	String[] temp = tempor[y].split("=");
	     	            	data3.put(temp[0],temp[1]);
	     	            }
	     	          if(Integer.parseInt(data3.get("FIN"))==1){
	     	        	 data3.put("ACKF",Integer.toString(1));
	     	        	 data = data3.toString().getBytes() ;
	    		         packet = new DatagramPacket( data, data.length, host, SERVERPORT ) ;
	    		         socket.send(packet ) ;
	    		         break;
	     	          }
	        	  }
	          }
	          System.out.print("closing");
	          Thread timerThread1 = new Thread(new Runnable() {
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
	          timerThread1.start();
	      }
	      catch( Exception e )
	      {
	         System.out.println( e ) ;
	      }
	      finally
	      {
	         if( socket != null )
	            socket.close() ;
	      }
	   }
	   
	   public static boolean random(){
	   		Random ran = new Random();
			 int temp = ran.nextInt(4);
			 int var = dropPackets[temp];
			 int random = ran.nextInt(101);
			 if(random<=var){
				 return false;
			 }
			 else{
			 return true;
			 }
		 }
}
