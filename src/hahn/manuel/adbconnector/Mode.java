package hahn.manuel.adbconnector;

/**
 * Der Modus, in welchem eine Nachricht angezeigt werden soll.
 * 
 * @author Manuel Hahn
 * @since 10.02.2017
 */
public enum Mode {
	/**
	 * Erfolgreiche Aktion anzeigen.
	 */
	SUCCESS,
	/**
	 * Fehlgeschlagene Aktion anzeigen.
	 */
	ERROR,
	/**
	 * Aktion mit Warnung anzeigen.
	 */
	WARNING,
	/**
	 * Aktion ohne bestimmten Anzeigemodus.
	 */
	NO_FLAG;
}