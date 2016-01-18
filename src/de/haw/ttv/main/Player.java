package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;

public class Player {

	private ID playerID;
	private Sector[] playerSectors;
	private boolean[] playerShips;
	private int shipsLeft;
	private int shootsGet = 0;

	public boolean handleHit(ID target) {
		int id = getTargetSectorID(target);
		if (id >= 0) {
			if (playerShips[id]) {
				shipsLeft--;
				return true;
			}
		}
		return false;
	}

	public int getTargetSectorID(ID target) {
		for (int i = 0; i < playerSectors.length; i++)
			if (target.isInInterval(playerSectors[i].getStart(), playerSectors[i].getEnd()))
				return i;
		return -1;
	}

	public Sector getTargetSector(ID target) {
		for (int i = 0; i < playerSectors.length; i++)
			if (target.isInInterval(playerSectors[i].getStart(), playerSectors[i].getEnd()))
				return playerSectors[i];
		return null;
	}

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

	public int getShootsGet() {
		return shootsGet;
	}

	public void setShootsGet(int shootsGet) {
		this.shootsGet = shootsGet;
	}

}
