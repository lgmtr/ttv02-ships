package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;

public class Player {

	private ID playerID;
	private Sector[] playerSectors;
	private boolean[] playerShips;
	private int shipsLeft;

	public ID getPlayerID() {
		return playerID;
	}

	public void setPlayerID(ID playerID) {
		this.playerID = playerID;
	}

	public Sector[] getPlayerSectors() {
		return playerSectors;
	}

	public void setPlayerSectors(Sector[] playerSectors) {
		this.playerSectors = playerSectors;
	}

	public boolean[] getPlayerShips() {
		return playerShips;
	}

	public void setPlayerShips(boolean[] playerShips) {
		this.playerShips = playerShips;
	}

	public int getShipsLeft() {
		return shipsLeft;
	}

	public void setShipsLeft(int shipsLeft) {
		this.shipsLeft = shipsLeft;
	}

}
