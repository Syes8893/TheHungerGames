package me.syes.thehungergames.game;

public class GameManager {

	private Game runningGame;
	
	public GameManager() {
		runningGame = null;
	}

	public Game getRunningGame() {
		if(runningGame == null)
			return null;
		return runningGame;
	}

	public void setRunningGame(Game runningGame) {
		this.runningGame = runningGame;
	}

	public void resetRunningGame() {
		this.runningGame = null;
	}

	public boolean isGameRunning() {
		if(runningGame == null)
			return false;
		return true;
	}
	
	public boolean isCountdown() {
		if(runningGame.getCountdownTime() > 0)
			return true;
		return false;
	}
	
	public boolean isStarted() {
		if(runningGame.getCountdownTime() == 0 && runningGame.getGameTime() <= runningGame.getDeathmatchTime())
			return true;
		return false;
	}
	
	public boolean isDeathmatch() {
		if(runningGame.getGameTime() > runningGame.getDeathmatchTime() && runningGame.getAlivePlayers().size() > 1)
			return true;
		return false;
	}
	
	public boolean isEnded() {
		if(runningGame.getAlivePlayers().size() == 1)
			return true;
		return false;
	}
	
}
