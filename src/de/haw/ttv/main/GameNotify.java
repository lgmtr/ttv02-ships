package de.haw.ttv.main;

import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.service.NotifyCallback;

public class GameNotify implements NotifyCallback {

	private GameState gameState;

	public GameNotify(GameState gameState) {
		this.gameState = gameState;
	}

	/**
	 * gets called, when someone shoots at us. It calls the proper methods to
	 * continue.
	 *
	 * @param target
	 *            is the target ID
	 */
	@Override
	public void retrieved(ID target) {
		// System.out.println(target);
		if (gameState.isGameRunning()) {
			gameState.handleHit(target);
			gameState.shootPlayer();
		}
	}

	@Override
	public void broadcast(ID source, ID target, Boolean hit) {
		if (gameState.isGameRunning()) {
			gameState.updateGameState(source, target, hit);
			gameState.showAllPlayersStatus();
		}
	}
}
