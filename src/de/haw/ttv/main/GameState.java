package de.haw.ttv.main;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

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

	public void createOwnPlayer() {
		ownPlayer = new Player();
		ownPlayer.setPlayerID(chord.getID());
		ownPlayer.setPlayerSectors(calculateSectors(chord.getPredecessorID(), ownPlayer.getPlayerID()));
		ownPlayer.setPlayerShips(placeShips());
	}

	private Sector[] calculateSectors(ID from, ID to) {
		Sector[] sectors = new Sector[SECTOR_COUNT];
		ID sectorLength;
		if (from.compareTo(to) < 0) {
			sectorLength = to.subtract(from);
		} else {
			sectorLength = BIGGEST_ID.subtract(from).add(to);
		}
		ID startInt = new ID(from.toBigInteger().add(BigInteger.ONE).toByteArray());
		ID endInt = startInt.add(sectorLength);
		for (int i = 0; i < sectors.length; i++) {
			sectors[i] = new Sector(startInt, endInt);
			startInt = new ID(endInt.toBigInteger().add(BigInteger.ONE).toByteArray());
			endInt = startInt.add(sectorLength);
		}
		return sectors;
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

	public void addNewPlayer() {

	}

	public Player getOwnPlayer() {
		return ownPlayer;
	}

	public void setOwnPlayer(Player ownPlayer) {
		this.ownPlayer = ownPlayer;
	}

}
