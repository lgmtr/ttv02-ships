package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;

public class Player {

	private ID playerID;
	private ID[] sectors;
	private boolean[] shipsInSector;
	private int shipsLeft;

	public ID getPlayerID() {
		return playerID;
	}

	public void setPlayerID(ID playerID) {
		this.playerID = playerID;
	}

	public int getShipsLeft() {
		return shipsLeft;
	}

	public void setShipsLeft(int shipsLeft) {
		this.shipsLeft = shipsLeft;
	}

	public ID[] getSectors() {
		return sectors;
	}

	public void setSectors(ID[] sectors) {
		this.sectors = sectors;
	}

	public boolean[] getShipsInSector() {
		return shipsInSector;
	}

	public void setShipsInSector(boolean[] shipsInSector) {
		this.shipsInSector = shipsInSector;
	}

}
