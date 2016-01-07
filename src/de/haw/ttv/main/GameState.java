package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;

public class GameState {

	// constants for config
	private static final String PROTOCOL = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
	private static final String SERVER_IP = "192.168.99.99";
	private static final String SERVER_PORT = "8080";
	private static final String CLIENT_IP = "192.168.99.225";
	private static final String CLIENT_PORT = "8181";
	private static final String joinOrCreate = "join";

	private Chord chord;

}
