package de.haw.ttv.main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.Chord;

public class GameState {

	private static final int SECTOR_COUNT = 100; // Number of Sectors
	private static final int SHIP_COUNT = 10; // Number of Ships
	private static final ID BIGGEST_ID = new ID(BigInteger.valueOf(2).pow(160).subtract(BigInteger.ONE).toByteArray());

	private Player ownPlayer;
	private List<Player> otherPlayerList;

	private Chord chord;

	public GameState(Chord chord) {
		this.chord = chord;
	}

	public void handleHit(ID target) {
		for (int i = 0; i < ownPlayer.getPlayerSectors().length; i++) {
			Sector sector = ownPlayer.getPlayerSectors()[i];
			if (target.isInInterval(sector.getStart(), sector.getEnd())) {
				if (ownPlayer.getPlayerShips()[i]) {
					System.err.println("Ship " + ownPlayer.getShipsLeft() + " destroyed!");
					ownPlayer.setShipsLeft(ownPlayer.getShipsLeft() - 1);
					chord.broadcast(target, Boolean.TRUE);
					break;
				} else {
					System.out.println("No Ship in this Sector!");
					chord.broadcast(target, Boolean.FALSE);
					break;
				}
			}
		}
		if(ownPlayer.getShipsLeft() < 1){
			System.out.println("I LOST!");
			waitTime(10000);
		} else{
			shootPlayer();
			waitTime(500);
		}
	}
	
	public void updateGameState(ID source, ID target, Boolean hit){
		for(Player player : otherPlayerList){
			if(player.getPlayerID().compareTo(source) == 0){
				for(Sector sector : player.getPlayerSectors()){
					if(target.isInInterval(sector.getStart(), sector.getEnd())){
						sector.setFiredAt(true);
						if(hit)
							player.setShipsLeft(player.getShipsLeft()-1);
						if(player.getShipsLeft() < 1)
							System.out.println("Player " + target + " lost!!!");
						break;
					}
				}
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
		if(lowestPlayer.getPlayerID().compareTo(ownPlayer.getPlayerID()) == 0)
			throw new IllegalArgumentException();
		Sector sectorToShoot = null;
		do {
			sectorToShoot = lowestPlayer.getPlayerSectors()[rnd.nextInt(SECTOR_COUNT)];
		} while (sectorToShoot.isFiredAt());
//		System.out.println("shooting at: " + sectorToShoot.getMiddle().toBigInteger());
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
			if (i == 0) {
				newPlayer = createPlayer(upl.get(i), upl.get(upl.size() - 1), upl.get(i));
			} else {
				newPlayer = createPlayer(upl.get(i), upl.get(i - 1), upl.get(i));
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
}
