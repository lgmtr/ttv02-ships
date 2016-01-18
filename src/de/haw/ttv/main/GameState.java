package de.haw.ttv.main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class GameState {

	private static final int SECTOR_COUNT = 100; // Number of Sectors
	private static final int SHIP_COUNT = 10; // Number of Ships
	private static final ID BIGGEST_ID = new ID(BigInteger.valueOf(2).pow(160).subtract(BigInteger.ONE).toByteArray());

	private Player ownPlayer;
	private List<Player> otherPlayerList;

	private ChordImpl chord;
	
	private boolean gameRunning = true;
	
	private ID playerLost = null;

	public GameState(ChordImpl chord) {
		this.chord = chord;
	}

	public void handleHit(ID target) {
		ownPlayer.setShootsGet(ownPlayer.getShootsGet() + 1);
		final boolean handleHit = ownPlayer.handleHit(target);
		if (handleHit) {
			chord.broadcast(target, Boolean.TRUE);
		} else {
			chord.broadcast(target, Boolean.FALSE);
		}
		if (ownPlayer.getShipsLeft() == 0) {
			gameRunning = false;
			playerLost = ownPlayer.getPlayerID();
			System.out.println("I LOST!");
			waitTime(10000);
		} else if (ownPlayer.getShipsLeft() > 0) {
			waitTime(1000);
			shootPlayer();
		}
	}

	public void updateGameState(ID source, ID target, Boolean hit) {
		for (Player player : otherPlayerList) {
			if (source.equals(player.getPlayerID())) {
				if (hit) {
					player.setShipsLeft(player.getShipsLeft() - 1);
					if (player.getShipsLeft() < 0){
						gameRunning = false;
						playerLost = source;
						System.out.println("Player " + target + " lost!!!");
					}
				}
				Sector playerSector = player.getTargetSector(target);
				playerSector.setFiredAt(true);
				break;
			}
		}
	}

	public void shootPlayer() {
		Random rnd = new Random();
		Player lowestPlayer = otherPlayerList.get(0);
		for (Player player : otherPlayerList) {
			if (player.getShipsLeft() < lowestPlayer.getShipsLeft())
				lowestPlayer = player;
		}
		Sector sectorToShoot = null;
		do {
			sectorToShoot = lowestPlayer.getPlayerSectors()[rnd.nextInt(SECTOR_COUNT)];
		} while (sectorToShoot.isFiredAt());
		waitTime(1000);
		// System.out.println("Shooting at: " + sectorToShoot.getMiddle());
		RetrieveThread retrieve = new RetrieveThread(chord, sectorToShoot.getMiddle());
		retrieve.start();
	}

	public Player createPlayer(ID playerID, ID from, ID to) {
		Player player = new Player();
		player.setPlayerID(playerID);
		player.setPlayerSectors(calculateSectors(from, to));
		return player;
	}

	public void createOwnPlayer() {
		ownPlayer = createPlayer(chord.getID(), chord.getPredecessorID(), chord.getID());
		ownPlayer.setPlayerShips(placeShips());
		ownPlayer.setShipsLeft(SHIP_COUNT);
	}

	private Sector[] calculateSectors(ID from, ID to) {
		Sector[] sectors = new Sector[SECTOR_COUNT];
		ID[] sectorStart = calculateSectorsStart(from, to);
		for (int i = 0; i < sectorStart.length - 1; i++) {
			sectors[i] = new Sector(sectorStart[i], sectorStart[i + 1]);
		}
		sectors[sectors.length - 1] = new Sector(sectorStart[sectorStart.length - 1], to);
		return sectors;
	}

	private ID[] calculateSectorsStart(ID from, ID to) {
		ID[] result = new ID[SECTOR_COUNT];
		ID distance;

		// predecessorID might be bigger than our ID, due to Chord circle
		if (from.compareTo(to) < 0)
			distance = to.subtract(from);
		else
			distance = BIGGEST_ID.subtract(from).add(to);
		ID step = distance.divide(SECTOR_COUNT);
		for (int i = 0; i < SECTOR_COUNT; i++)
			result[i] = from.add(1).add(step.multiply(i)).mod(BIGGEST_ID);
		return result;
	}

	private boolean[] placeShips() {
		Random rnd = new Random();
		int random;
		boolean[] ships = new boolean[SECTOR_COUNT];
		for (int i = 0; i < SHIP_COUNT; i++) {
			do {
				random = rnd.nextInt(SECTOR_COUNT);
			} while (ships[random] == true);

			ships[random] = true;
		}
		return ships;
	}

	public void addPlayers(Set<Node> uniquePlayer, ID ownID) {
		List<ID> upl = new ArrayList<ID>();
		upl.add(ownID);
		for (Node node : uniquePlayer) {
			upl.add(node.getNodeID());
		}
		Collections.sort(upl);
		otherPlayerList = new ArrayList<Player>();
		for (int i = 0; i < upl.size(); i++) {
			Player newPlayer;
			if ((i + 1) == upl.size()) {
				newPlayer = createPlayer(upl.get(upl.size() - 1), upl.get(upl.size() - 1), upl.get(0));
			} else {
				newPlayer = createPlayer(upl.get(i), upl.get(i), upl.get(i + 1));
			}
			newPlayer.setShipsLeft(SHIP_COUNT);
			otherPlayerList.add(newPlayer);
		}
		Player ownPlayer = null;
		for (Player player : otherPlayerList) {
			if (player.getPlayerID().equals(this.ownPlayer.getPlayerID()))
				ownPlayer = player;
		}
		otherPlayerList.remove(ownPlayer);
	}

	public Player getOwnPlayer() {
		return ownPlayer;
	}

	public void setOwnPlayer(Player ownPlayer) {
		this.ownPlayer = ownPlayer;
	}

	private void waitTime(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void showAllPlayersStatus() {
		System.out.println("====================================================================================");
		System.out.println("Self ships left: " + ownPlayer.getShipsLeft() + " / Shoots Get: " + ownPlayer.getShootsGet());
		System.out.print("Player Nr.: ");
		for (int i = 0; i < otherPlayerList.size(); i++) {
			System.out.print(i + "\t");
		}
		System.out.print("Ships left: ");
		for (int i = 0; i < otherPlayerList.size(); i++) {
			System.out.print(otherPlayerList.get(i).getShipsLeft() + "\t");
		}
		System.out.println("\n====================================================================================");
	}

	public boolean isGameRunning() {
		return gameRunning;
	}

	public void setGameRunning(boolean gameRunning) {
		this.gameRunning = gameRunning;
	}

	public ID getPlayerLost() {
		return playerLost;
	}

	public void setPlayerLost(ID playerLost) {
		this.playerLost = playerLost;
	}
}
