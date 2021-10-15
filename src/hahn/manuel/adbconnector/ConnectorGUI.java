package hahn.manuel.adbconnector;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Main class.
 * 
 * @author Manuel Hahn
 * @since 09.02.2017
 */
public class ConnectorGUI implements ActionListener {
	private JLabel mainText;
	private Timer timer;
	private Color standardColor;
	private String deviceName = "MotoG3";
	private String ipAdress = "192.168.1.12";
	private String pathToADB;
	private JFrame frame;
	private boolean blinkON;
	private int greenCounter;
	private boolean first;
	private JButton skip;

	public ConnectorGUI() {
		pathToADB = System.getProperty("user.home");
		if(System.getProperty("os.name").contains("Windows")) {
			pathToADB += "\\AppData\\Local\\Android\\sdk\\platform-tools\\";
		} else {
			pathToADB += "/Library/Android/sdk/platform-tools/";
		}
		initGUI();
		first = true;
		timer = new Timer(500, this);
		timer.setActionCommand("connectUSB");
		timer.start();
	}
	
	private void initGUI() {
		frame = new JFrame("Verbinden...");
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		mainText = new JLabel("Bitte Verbindung per USB herstellen. Bitte Verbindung per USB herstellen. Bitte Verbindung per USB herstellen.");
		mainText.setOpaque(true);
		standardColor = mainText.getBackground();
		JButton abort = new JButton("Abbrechen");
		skip = new JButton(">>");
		skip.setActionCommand("skip");
		skip.addActionListener(this);
		abort.setActionCommand("abort");
		abort.addActionListener(this);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		frame.getContentPane().setLayout(new GridLayout(2, 1));
		frame.getContentPane().add(mainText);
		buttons.add(abort);
		buttons.add(skip);
		frame.getContentPane().add(buttons);
		frame.pack();
		frame.setVisible(true);
	}
	
	private boolean connectUSB() {
		inform("Es wird nach dem MotoG3 gesucht...", Mode.NO_FLAG);
		String ioText = null;
		try {
			ioText = exec(pathToADB + "adb devices -l", 1000);
		} catch(IOException e) {
			inform("Konnte adb nicht ausführen oder -lesen: " + e.getMessage(), Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(InterruptedException e) {
			inform("Prozess wurde unterbrochen!", Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			inform("Unbekannter Fehler aufgetreten!", Mode.ERROR);
			e.printStackTrace();
			return false;
		}
		if(ioText.contains("MotoG3")) {
			inform("Gerät gefunden!", Mode.SUCCESS);
			blinkON = false;
			return true;
		}
		if(blinkON) {
			inform("Bitte Verbindung per USB herstellen.", Mode.NO_FLAG);
			blinkON = false;
		} else {
			inform("Bitte Verbindung per USB herstellen.", Mode.WARNING);
			blinkON = true;
		}
		return false;
	}
	
	private boolean disconnectUSB() {
		if(blinkON) {
			inform("Bitte USB-Verbindung von " + deviceName + " trennen.", Mode.NO_FLAG);
			blinkON = false;
		} else {
			inform("Bitte USB-Verbindung von " + deviceName + " trennen.", Mode.WARNING);
			blinkON = true;
		}
		String ioText = null;
		try {
			ioText = exec(pathToADB + "adb devices -l", 1000);
		} catch(IOException e) {
			inform("Konnte adb nicht ausführen oder -lesen:" + e.getMessage(), Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(InterruptedException e) {
			inform("Prozess wurde unterbrochen!", Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			inform("Unbekannter Fehler aufgetreten!", Mode.ERROR);
			e.printStackTrace();
			return false;
		}
		if(ioText.contains("MotoG3")) {
			return false;
		}
		inform("Verbindung getrennt.", Mode.NO_FLAG);
		blinkON = false;
		return true;
	}
	
	private boolean superDisconnectUSB() {
		if(first) {
			if(connectUSB()) {
				first = false;
			}
			return false;
		} else {
			return disconnectUSB();
		}
	}
	
	private boolean connectWLAN() {
		inform("WLAN-Verbindung mit " + deviceName + " wird aufgebaut...", Mode.NO_FLAG);
		String ioText = null;
		try {
			ioText = exec(pathToADB + "adb connect " + ipAdress, 1000);
		} catch(IOException e) {
			inform("Konnte adb nicht ausführen oder -lesen: " + e.getMessage(), Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(InterruptedException e) {
			inform("Prozess wurde unterbrochen!", Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			inform("Unbekannter Fehler aufgetreten!", Mode.ERROR);
			e.printStackTrace();
			return false;
		}
		if(ioText.contains("unable") && (ioText.contains("connect"))) {
			timer.stop();
			inform("Falsche IP-Adresse!", Mode.ERROR);
			ipAdress = JOptionPane.showInputDialog(frame, "Bitte IP-Adresse von " + deviceName + " eingeben:", "IP-Adresse eingeben", JOptionPane.WARNING_MESSAGE);
			if(ipAdress == null) {
				System.exit(0);
			}
			timer.start();
			return false;
		}
		if(ioText.contains("connected")) {
			inform("Über WLAN verbunden mit " + deviceName + ".", Mode.SUCCESS);
			return true;
		}
		return false;
	}
	
	private String exec(String command, int chars) throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor();
		InputStream in = p.getInputStream();
		int byteData, index = 0;
		char[] text = new char[chars];
		do {
			byteData = in.read();
			if(byteData != -1) {
				text[index] = (char) byteData;
			}
			index++;
		} while(byteData != -1);
		index--;
		text[--index] = '\0';
		String s = new String(text);
		System.out.println(command);
		System.out.println(s);
		return s;
	}
	
	private boolean checkWLANConnection() {
		inform("WLAN-Verbindung mit " + deviceName + " wird überprüft...", Mode.NO_FLAG);
		String ioText = null;
		try {
			ioText = exec(pathToADB + "adb devices", 1000);
		} catch(IOException e) {
			inform("Konnte adb nicht ausführen oder -lesen: " + e.getMessage(), Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(InterruptedException e) {
			inform("Prozess wurde unterbrochen!", Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			inform("Unbekannter Fehler aufgetreten!", Mode.ERROR);
			e.printStackTrace();
			return false;
		}
		if(ioText.contains(ipAdress)) {
			inform("ADB verbunden mit " + ipAdress + ".", Mode.SUCCESS);
			return true;
		}
		inform("Nicht mit " + ipAdress + " verbunden!", Mode.ERROR);
		return false;
	}
	
	private boolean openLANPort() {
		inform("LAN-Port wird geöffnet...", Mode.NO_FLAG);
		String ioText = null;
		try {
			ioText = exec(pathToADB + "adb tcpip 5555", 1000);
		} catch (IOException e) {
			inform("Konnte adb nicht ausführen oder -lesen: " + e.getMessage(), Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			inform("Prozess wurde unterbrochen!", Mode.ERROR);
			e.printStackTrace();
			return false;
		} catch(Exception e) {
			inform("Unbekannter Fehler aufgetreten!", Mode.ERROR);
			e.printStackTrace();
			return false;
		}
		if(ioText.contains("error")) {
			inform("adb konnte LAN-Port nicht öffnen!", Mode.ERROR);
			return false;
		}
		if(ioText.contains("restarting") && (ioText.contains("TCP mode"))) {
			inform("LAN-Port erfolgreich geöffnet. Port: 5555", Mode.SUCCESS);
			return true;
		}
		return false;
		
	}
	
	private void inform(String text, Mode mode) {
		mainText.setText(text);
		System.out.print(text + ": ");
		switch(mode) {
		case SUCCESS:
			mainText.setBackground(Color.GREEN);
			mainText.setForeground(Color.BLACK);
			System.out.println(mode.toString());
			break;
		case ERROR:
			mainText.setBackground(Color.RED);
			mainText.setForeground(Color.WHITE);
			System.out.println(mode.toString());
			break;
		case WARNING:
			mainText.setBackground(Color.YELLOW);
			mainText.setForeground(Color.BLACK);
			System.out.println(mode.toString());
			break;
		case NO_FLAG:
		default:
			mainText.setBackground(standardColor);
			mainText.setForeground(Color.BLACK);
			System.out.println(mode.toString());
			break; 
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "abort" : System.exit(0);
		break;
		case "connectUSB" : 
			if(connectUSB()) {
				if(!openLANPort()) {
					timer.setActionCommand("port");
				} else {
					if(!superDisconnectUSB()) {
						timer.setActionCommand("disconnect");
					} else {
						if(!connectWLAN()) {
							timer.setActionCommand("WLAN");
						} else {
							if(!checkWLANConnection()) {
								timer.setActionCommand("check");
							} else {
								timer.setActionCommand("blinkGreen");
								inform("Verbunden.", Mode.SUCCESS);
								blinkON = true;
							}
						}
					}
				}
			}
		break;
		case "port":
			if(openLANPort()) {
				if(!superDisconnectUSB()) {
					timer.setActionCommand("disconnect");
				} else {
					if(!connectWLAN()) {
						timer.setActionCommand("WLAN");
					} else {
						if(!checkWLANConnection()) {
							timer.setActionCommand("check");
						} else {
							timer.setActionCommand("blinkGreen");
							inform("Verbunden.", Mode.SUCCESS);
							blinkON = true;
						}
					}
				}
			}
			break;
		case "disconnect":
			if(superDisconnectUSB()) {
				if(!connectWLAN()) {
					timer.setActionCommand("WLAN");
				} else {
					if(!checkWLANConnection()) {
						timer.setActionCommand("check");
					} else {
						timer.setActionCommand("blinkGreen");
						inform("Verbunden.", Mode.SUCCESS);
						blinkON = true;
					}
				}
			}
			break;
		case "WLAN":
			if(connectWLAN()) {
				if(!checkWLANConnection()) {
					timer.setActionCommand("check");
				} else {
					timer.setActionCommand("blinkGreen");
					inform("Verbunden.", Mode.SUCCESS);
					blinkON = true;
				}
			}
			break;
		case "check":
			if(checkWLANConnection()) {
				timer.setActionCommand("blinkGreen");
				inform("Verbunden.", Mode.SUCCESS);
				blinkON = true;
			}
			break;
		case "blinkGreen":
			if(greenCounter < 10) {
				if(!checkWLANConnection()) {
					greenCounter = 0;
					timer.setActionCommand("WLAN");
				}
				if(blinkON) {
					inform("Verbunden.", Mode.NO_FLAG);
					blinkON = false;
				} else {
					inform("Verbunden.", Mode.SUCCESS);
					blinkON = true;
				}
				greenCounter++;
			} else {
				System.exit(0);
			}
			break;
		case "skip":
			switch(timer.getActionCommand()) {
			case "connectUSB":
			case "disconnect":
				timer.setActionCommand("WLAN");
				skip.setEnabled(false);
				break;
			}
			break;
		}
	}
	
	public static void main(String[] args) {
		new ConnectorGUI();
	}
}