package de.stationary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoCommunicator {
	
	private SerialPort serialport;
	
	public ArduinoCommunicator(SerialPort port){
		this.serialport = port;
	}
	
	public void send(char[] chars){
		PrintWriter writer = new PrintWriter(serialport.getOutputStream());
		
		writer.println(chars);
		writer.flush();
	}
	
	public SerialPort getPort(){
		return this.serialport;
	}
	
	public void startInputCollectorLoop(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getPort().getInputStream()));
		
		float[] pureData = new float[6];
		int dataCursor = 0;
		String inputLine;
		
		
		
		while(this.getPort().isOpen()){
			try{
				
				inputLine = reader.readLine();
				
				if(inputLine.equals(""))
					continue;
				
				System.out.println("Received input: '" + inputLine + "'");
				
				if(inputLine.equals("start")){
					dataCursor = 0;
					storeLog("INFO", "started new data-reading");
				}else if(inputLine.equals("end")){
					sendDataToWebserver(pureData);
					storeData(convertToStorageFormat(pureData));
					storeLog("INFO", "	-data-reading ended successfully");
				}else{
					pureData[dataCursor] = Float.parseFloat(inputLine);
					dataCursor++;
				}
				
			
				try{Thread.sleep(50);}catch(Exception exc){}
				
			}catch(Exception exc){
				exc.printStackTrace();
			}
		}
	}
	
	
	private String webserverAddress = "localhost";
	private int webserverPort = 7637;
	public void sendDataToWebserver(float[] pureData){
		
		String lineToSend = "";
		for(float f : pureData){
			lineToSend += f + ";";
		}		
		
		try{
			
			System.out.println("Initializing socket");
			Socket s = new Socket(webserverAddress, webserverPort);
			System.out.println("Initialized socket");
			
			if(s.isConnected()){
				System.out.println("socket is connected");
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
				writer.println(lineToSend);
				writer.flush();
				writer.close();				
			}
			
			//Thread.sleep(500);
			
			s.close();
			
		}catch(Exception exc){}
		
	}
	
	private final File dataStoreDir = new File("H://arduinoDaten/"); 
	private void storeData(String inputLine){
		dataStoreDir.mkdirs();
		File dataFile = new File(dataStoreDir + "/data_" + getCurrentDate("dd_MM_yyyy") + ".txt");
		
		try{
		
			System.out.println("adding data to file: " + dataFile.getAbsolutePath());
			//creates file IF FILE DOESNT YET EXIST
			dataFile.createNewFile();
		
			PrintWriter writer = new PrintWriter(new FileWriter(dataFile, true));
			
			writer.println("[" + getCurrentTime() + "]: '" + inputLine + "'");
			writer.flush();
			
			writer.close();			
		}catch(Exception exc){}
		
	}
	
	private final File logFile = new File("H://arduinoDaten/log.txt");
	private void storeLog(String lineHeader, String log){
		
		try{
			//creates file IF FILE DOESNT YET EXIST
			logFile.createNewFile();
		
			PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
			
			writer.println("[" + getCurrentDate() + "][" + getCurrentTime() + "] [" + lineHeader + "]: '" + log + "'");
			writer.flush();
			
			writer.close();
		}catch(Exception exc){}		
	}
	
	private String getCurrentDate(){	return getCurrentDate("dd:MM:yyyy");	}
	private String getCurrentDate(String format){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);                
        return sdf.format(calendar.getTime());		
	}
	
	private String getCurrentTime(){	return getCurrentTime("HH:mm:ss");	}	
	private String getCurrentTime(String format){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);                
        return sdf.format(calendar.getTime());		
	}
	
	public String convertToStorageFormat(float[] pureData){
		return "AP:" + pureData[0] + "HUM:" + pureData[1];
	}
}
