package de.stationary;

//import com.fazecast.jSerialComm.*;
import com.fazecast.jSerialComm.SerialPort;

public class Main {

	private static ArduinoCommunicator arduinoCom;

	/**
	 * Dieses Programm sollte auf dem Raspberry Pi, der mit dem Arduino
	 * verbunden in der Wetterstation steht, laufen
	 */
	public static void main(String[] args) {

		System.out.println("listing");
		for (SerialPort s : SerialPort.getCommPorts()) {
			System.out.println(s.getSystemPortName());
		}

		// Initialisiere Serielle Schnittstelle
		SerialPort chosenPort = SerialPort.getCommPorts()[0];
		// SerialPort chosenPort = SerialPort.getCommPort("COM8");
		chosenPort.setBaudRate(9600);
		chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

		arduinoCom = new ArduinoCommunicator(chosenPort);

		startCommunication();
	}

	private static void startCommunication() {

		// Öffne die Schnitstelle und starte Datentransfer
		if (arduinoCom.getPort().openPort()) {

			System.out.println("pls wait...");
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			System.out.println("Connected to Arduino");

			arduinoCom.send("helloo".toCharArray());

			arduinoCom.startInputCollectorLoop();

			System.out.println(
					"communication ended (--> no future inputs from arduino will be received, program is probably terminating");
		}
		// -----------------------------------------------------------------------------------
		else {
			System.out.println("Connecting with Arduino failed");
		}
		arduinoCom.getPort().closePort();
	}
}
