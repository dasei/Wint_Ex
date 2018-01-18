package de.stationary;

//import com.fazecast.jSerialComm.*;
import com.fazecast.jSerialComm.*;
import java.io.*;

public class Main {

	private static ArduinoCommunicator arduinoCom;
	
	public static void main(String[] args) {
		
		
		//Initialisiere Serielle Schnittstelle
		SerialPort chosenPort = SerialPort.getCommPort("COM3");
		chosenPort.setBaudRate(250000);
		chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		
		arduinoCom = new ArduinoCommunicator(chosenPort);
		

	}
	
	private static void startCommunication(){
		
		//Öffne die Schnitstelle und starte Datentransfer
		if(arduinoCom.getPort().openPort()){
			
			System.out.println("pls wait...");
			try {Thread.sleep(2000); } catch(Exception e) {}
			System.out.println("Connected to Arduino");
			
			
			arduinoCom.send("helloo".toCharArray());
			
			arduinoCom.registerInputs();	
			
		} 
//-----------------------------------------------------------------------------------
		else{
			System.out.println("Connecting with Arduino failed");
			arduinoCom.getPort().closePort();
		}
	}
}
	