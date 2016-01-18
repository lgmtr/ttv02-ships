package de.haw.ttv.main;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.PropertiesLoader;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class Main {

	// constants for config
	private static final String PROTOCOL = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
//	private static final String SERVER_IP = "192.168.1.90";
	private static final String SERVER_IP = "141.22.88.82";
	// private static final String SERVER_IP = "192.168.99.225";
	private static final String SERVER_PORT = "8080";
	// private static final String CLIENT_IP = "192.168.99.225";
	private static final String CLIENT_IP = "141.22.88.82";
//	private static final String CLIENT_IP = "192.168.1.90";
	private static final String CLIENT_PORT = "8181";

	// private static final String joinOrCreate = "join";

	/**
	 * The Class InputThread.
	 */
	private class InputThread implements Runnable {

		/** The running. */
		boolean running = true;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Scanner scan = new Scanner(System.in);
			while (running) {
				if (scan.hasNext()) {
					input = scan.next();
				}
			}
			scan.close();
		}

		/**
		 * Stop.
		 */
		public void stop() {
			running = false;
		}
	}

	private ChordImpl chordImpl;
	private GameState gameState;
	private GameNotify gameNotify;
	private String input = "";
	private InputThread in;
	private Thread inputListener;

	// private Scanner scanner;

	public static void main(String[] args) {
		Main game = new Main();
		game.createGameEnv();
		game.start();
	}

	private void createGameEnv() {
		propertyLoader();
		this.chordImpl = new ChordImpl();
		this.gameState = new GameState(chordImpl);
		this.gameNotify = new GameNotify(gameState);
		this.chordImpl.setCallback(gameNotify);

		in = new InputThread();
		inputListener = new Thread(in);
		inputListener.start();

		System.out.print("type \"s\" for Server and \"c\" for client: ");
		while (!input.equals("s") || !input.equals("c")) {
			// input = scanner.next();
			if (input.equals("s")) {
				input = "";
				createServer();
				break;
			} else if (input.equals("c")) {
				input = "";
				createClient();
				break;
			}
			waitTime(500);
		}
	}

	private void start() {
		System.out.println("type \"s\" for Start and \"q\" for Quit");
		Set<Node> fingerSet = new HashSet<>(chordImpl.getFingerTable());
		int playerCount = fingerSet.size();
		System.out.print("Joined Player Count: " + playerCount);
		while (!input.equals("q")) {
			fingerSet = new HashSet<>(chordImpl.getFingerTable());
			if (playerCount != fingerSet.size()) {
				System.out.print(" : " + fingerSet.size());
				playerCount = fingerSet.size();
			}
			if (input.equals("s")) {
				break;
			}
			waitTime(500);
		}
		in.stop();
		inputListener.interrupt();
		if (input.equals("q")) {
			System.out.println("Game closed!!");
			chordImpl.leave();
		} else {
			gameStart();
		}
	}

	private void gameStart() {
		System.out.println("MyID in BigInteger: " + chordImpl.getID().toBigInteger());
		System.out.println("PrID in BigInteger: " + chordImpl.getPredecessorID().toBigInteger());
		gameState.createOwnPlayer();
		waitTime(5000);
		gameState.addPlayers(new HashSet<Node>(chordImpl.getFingerTable()), gameState.getOwnPlayer().getPlayerID());
		if (chordImpl.getPredecessorID().compareTo(chordImpl.getID()) > 0) {
			System.out.println("I Start!!");
			gameState.shootPlayer();
		}
	}

	private void createClient() {
		propertyLoader();

		URL localURL = null;
		try {
			localURL = new URL(PROTOCOL + "://" + CLIENT_IP + ":" + CLIENT_PORT + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		URL serverURL = null;
		try {
			serverURL = new URL(PROTOCOL + "://" + SERVER_IP + ":" + SERVER_PORT + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		try {
			chordImpl.join(localURL, serverURL);
		} catch (ServiceException e) {
			throw new RuntimeException("Could not join DHT!", e);
		}

		System.out.println("Joined Server: " + serverURL);
	}

	private void createServer() {
		propertyLoader();

		URL localURL = null;
		try {
			localURL = new URL(PROTOCOL + "://" + SERVER_IP + ":" + SERVER_PORT + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		try {
			chordImpl.create(localURL);
		} catch (ServiceException e) {
			throw new RuntimeException("Could not create DHT!", e);
		}

		System.out.println("Chord listens on: " + localURL);

	}

	private void propertyLoader() {
		try {
			PropertiesLoader.loadPropertyFile();
		} catch (IllegalStateException e) {
			System.out.println("Properties already loaded!");
		}
	}

	private void waitTime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
