package de.stationary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	
	public void startInputCollectorLoop(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getPort().getInputStream()));
		
		float[] pureData = new float[7];
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
	
	private final File dataStoreDir = new File("H://arduinoDaten/"); 
	private void storeData(String inputLine){
		dataStoreDir.mkdirs();
		File dataFile = new File(dataStoreDir + "/data_" + getCurrentDate() + ".txt");
		
		try{
		
			System.out.println("adding data to file: " + dataFile.getAbsolutePath());
			//creates file IF FILE DOESNT YET EXIST
			dataFile.createNewFile();
		
			PrintWriter writer = new PrintWriter(new FileWriter(dataFile, true));
			
			writer.println(getCurrentTime() + ":" + inputLine);
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
	
	private Calendar calendar = Calendar.getInstance();
	
	private String getCurrentDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");                
        return sdf.format(calendar.getTime());		
	}
	
	private String getCurrentTime(){
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");                
        return sdf.format(calendar.getTime());		
	}
	
	public String convertToStorageFormat(float[] pureData){
		return "AP:" + pureData[0] + "HUM:" + pureData[1];
	}
}
