package de.haw.ttv.main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

public class GameState implements NotifyCallback {

	private static final int SECTOR_COUNT = 100; // Number of Sectors
	private static final int SHIP_COUNT = 10; // Number of Ships
	private static final ID BIGGEST_ID = new ID(BigInteger.valueOf(2).pow(160).subtract(BigInteger.ONE).toByteArray());

	private Player ownPlayer;
	private List<ID> allPlayerList;
	private Map<ID, ID[]> allPlayerSectors;
	private List<ID> sectorsToShoot;

	private ChordImpl chordImpl;

	private List<BroadcastLog> broadcastLog;

	private Map<ID, Integer> hitsForID = new HashMap<ID, Integer>();

	public GameState(ChordImpl chord) {
		this.chordImpl = chord;
		this.broadcastLog = new ArrayList<>();
	}

	public void shootPlayer() {
		Random rnd = new Random();
		RetrieveThread retrieve = new RetrieveThread(chordImpl, sectorsToShoot.get(rnd.nextInt(sectorsToShoot.size())).add(10).mod(BIGGEST_ID));
		retrieve.start();
	}

	private Player createPlayer(ID playerID, ID from, ID to) {
		Player player = new Player();
		player.setPlayerID(playerID);
		player.setSectors(calculateSectors(from, to));
		return player;
	}

	public void createOwnPlayer() {
		ownPlayer = createPlayer(chordImpl.getID(), chordImpl.getPredecessorID(), chordImpl.getID());
		ownPlayer.setShipsInSector(setShips());
		ownPlayer.setShipsLeft(SHIP_COUNT);
	}

	private ID[] calculateSectors(ID from, ID to) {
		ID[] result = new ID[SECTOR_COUNT];
		ID distance;
		// predecessorID might be bigger than our ID, due to Chord circle
		if (from.compareTo(to) < 0) {
			distance = to.subtract(from);
		} else {
			distance = BIGGEST_ID.subtract(from).add(to);
		}
		ID step = distance.divide(SECTOR_COUNT);
		for (int i = 0; i < SECTOR_COUNT; i++) {
			result[i] = from.add(1).add(step.multiply(i)).mod(BIGGEST_ID);
		}
		return result;
	}

	private boolean[] setShips() {
		Random rnd = new Random();
		int random;
		boolean[] ships = new boolean[SECTOR_COUNT];
		for (int i = 0; i < ships.length; i++) {
			ships[i] = false;
		}
		for (int i = 0; i < SHIP_COUNT; i++) {
			do {
				random = rnd.nextInt(SECTOR_COUNT);
			} while (ships[random] == true);
			ships[random] = true;
		}
		return ships;
	}

	public void calculateAllPlayers(Set<Node> uniquePlayer, ID ownID) {
		allPlayerList = new ArrayList<>();
		allPlayerSectors = new HashMap<>();
		allPlayerList.add(ownID);
		for (Node node : uniquePlayer) {
			allPlayerList.add(node.getNodeID());
		}
		Collections.sort(allPlayerList);
		for (int i = 0; i < allPlayerList.size() - 1; i++) {
			ID[] newSectors = calculateSectors(allPlayerList.get(i), allPlayerList.get(i + 1));
			allPlayerSectors.put(allPlayerList.get(i), newSectors);
		}
		// case for the last player
		ID[] newSectors = calculateSectors(allPlayerList.get(allPlayerList.size() - 1), allPlayerList.get(0));
		allPlayerSectors.put(allPlayerList.get(allPlayerList.size() - 1), newSectors);
	}

	public void calculateSectorsToShoot() {
		sectorsToShoot = new ArrayList<>();
		// fill List
		for (ID uniquePlayer : allPlayerList) {
			for (int j = 0; j < SECTOR_COUNT; j++) {
				sectorsToShoot.add(allPlayerSectors.get(uniquePlayer)[j]);
			}
		}

		// remove fields, that are destroyed
		for (BroadcastLog bl : broadcastLog.toArray(new BroadcastLog[0])) {
			for (ID uniquePlayer : allPlayerList) {
				ID[] sectors = allPlayerSectors.get(uniquePlayer);
				int index = isInSector(bl.getTarget(), sectors);
				if (index != -1) {
					sectorsToShoot.remove(sectors[index]);
				}
			}
		}

		// remove own fielde
		for (ID id : ownPlayer.getSectors()) {
			sectorsToShoot.remove(id);
		}
	}

	private int isInSector(ID target, ID[] sector) {
		if (!target.isInInterval(sector[0], sector[sector.length - 1])) {
			return -1;
		}

		for (int i = 0; i < sector.length - 1; i++) {
			if (target.compareTo(sector[i]) >= 0 && target.compareTo(sector[i + 1]) < 0) {
				return i;
			}
		}

		if (target.compareTo(sector[sector.length - 1]) >= 0 && target.compareTo(findNextPlayer(target)) < 0) {
			return sector.length - 1;
		}

		return -1;
	}

	private ID findNextPlayer(ID target) {
		List<ID> uniquePlayers = allPlayerList;
		Collections.sort(uniquePlayers);

		for (int i = 0; i < uniquePlayers.size() - 1; i++) {
			if (target.isInInterval(uniquePlayers.get(i), uniquePlayers.get(i + 1))) {
				return uniquePlayers.get(i + 1);
			}
		}
		return uniquePlayers.get(0);
	}

	public Player getOwnPlayer() {
		return ownPlayer;
	}

	public void setOwnPlayer(Player ownPlayer) {
		this.ownPlayer = ownPlayer;
	}

	public BroadcastLog addBroadcastLog(ID source, ID target, boolean hit) {
		final BroadcastLog broadcastLogLocal = new BroadcastLog(source, target, hit, chordImpl.getLocalNode().getTransactionNumber());
		broadcastLog.add(broadcastLogLocal);
		return broadcastLogLocal;
	}

	public void showAllPlayersStatus() {
		System.out.println("====================================================================================");
		System.out.println("Self ships left: " + ownPlayer.getShipsLeft());
		System.out.print("Player Nr.: ");
		for (int i = 0; i < allPlayerList.size(); i++) {
			System.out.print(i + "\t");
		}
		System.out.print("\nShips left: ");
		for (ID player : allPlayerList) {
			if(hitsForID.containsKey(player)){
			System.out.print((SHIP_COUNT-hitsForID.get(player)) + "\t");
			} else {
				System.out.print(SHIP_COUNT + "\t");
			}
		}
		System.out.println("\n====================================================================================");
	}

	private void handleHit(ID target){
		ID[] sectors = ownPlayer.getSectors();
		for (int i = 0; i < sectors.length - 1; i++) {
            if (target.compareTo(sectors[i]) >= 0 && target.compareTo(sectors[i + 1]) < 0) {
				if(ownPlayer.getShipsInSector()[i]){
					ownPlayer.setShipsLeft(ownPlayer.getShipsLeft()-1);
					ownPlayer.getShipsInSector()[i] = false;
                    chordImpl.broadcast(target, Boolean.TRUE);
                    break;
                } else {
                    chordImpl.broadcast(target, Boolean.FALSE);
                    break;
				}
			}
		}
        if (target.compareTo(sectors[sectors.length - 1]) >= 0 && target.compareTo(ownPlayer.getPlayerID()) <= 0) {
            if (ownPlayer.getShipsInSector()[sectors.length - 1]) {
                ownPlayer.setShipsLeft(ownPlayer.getShipsLeft()-1);
                ownPlayer.getShipsInSector()[sectors.length - 1] = false;
                chordImpl.broadcast(target, Boolean.TRUE);
            } else {
                chordImpl.broadcast(target, Boolean.FALSE);
            }
        }

        if (ownPlayer.getShipsLeft() < 1) {
            System.out.println("I LOST!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
	}
	
	
	@Override
	public void retrieved(ID target) {
		handleHit(target);
		calculateSectorsToShoot();
		shootPlayer();
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		addBroadcastLog(source, target, hit);
		if (hit) {
			if (hitsForID.containsKey(source)) {
				int hits = hitsForID.get(source) + 1;
				hitsForID.replace(source, hits);
				if (hits >= 10) {
					System.err.println("Player: " + source + " lost!");
				}
			} else {
				hitsForID.put(source, 1);
			}
			showAllPlayersStatus();
		}
	}
}
