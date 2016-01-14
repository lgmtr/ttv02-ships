package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

public class GameNotify implements NotifyCallback {

	private GameState gameState;
	
	public GameNotify(GameState gameState) {
		this.gameState = gameState;
	}
	
	@Override
	public void retrieved(ID target) {
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		// TODO Auto-generated method stub

	}

}
