package de.stationary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoCommunicator {
	
	private SerialPort port;
	
	public ArduinoCommunicator(SerialPort port){
		this.port = port;
	}
	
	public void send(char[] chars){
		PrintWriter writer = new PrintWriter(port.getOutputStream());
		
		writer.println(chars);
		writer.flush();
	}
	
	public SerialPort getPort(){
		return this.port;
	}
	
	public void registerInputs(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getPort().getInputStream()));
		String inputLine;
		
		while(this.getPort().isOpen()){
			try{
				if(reader.ready())
					storeData(reader.readLine());				
				
				try{Thread.sleep(500);}catch(Exception exc){}
				
			}catch(Exception exc){
				exc.printStackTrace();
			}
		}
	}
	
	private final String dataStoreDir = "storedData/"; 
	private void storeData(String inputLine){
		File dataFile = new File(dataStoreDir + "data_" + getCurrentDate() + ".txt");
		
		try{
			PrintWriter writer = new PrintWriter(new FileWriter(dataFile, true));
			
			writer.println(getCurrentTime() + ":" + inputLine);
			writer.flush();
			
			
			writer.close();			
		}catch(Exception exc){}
		
	}
	
	private String getCurrentDate(){
		return "27.04.1965";
	}
	
	private String getCurrentTime(){
		return "14.32";
	}
}
