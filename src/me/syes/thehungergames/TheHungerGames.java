package me.syes.thehungergames;

import org.bukkit.plugin.java.JavaPlugin;

import me.syes.thehungergames.commands.CommandManager;
import me.syes.thehungergames.game.GameManager;
import me.syes.thehungergames.listener.GameListener;
import me.syes.thehungergames.scoreboard.ScoreboardManager;
import me.syes.thehungergames.utils.ConfigUtils;
import me.syes.thehungergames.utils.TimeUtils;

public class TheHungerGames extends JavaPlugin {

	private ConfigUtils configUtils;
	private TimeUtils timeUtils;
	
	private GameManager gameManager;
	private ScoreboardManager scoreboardManager;
	
	public void onEnable() {
		registerManagers();
		registerHandlers();
		registerCommands();
		registerUtils();
	}
	
	public void onDisable() {
		this.saveConfig();
		if(gameManager.isGameRunning())
			gameManager.getRunningGame().resetGame();
	}
	
	private void registerManagers() {
		this.gameManager = new GameManager();
		this.scoreboardManager = new ScoreboardManager(this);
	}
	
	private void registerHandlers() {
		this.getServer().getPluginManager().registerEvents(new GameListener(this), this);
		this.getServer().getPluginManager().registerEvents(scoreboardManager.getScoreboardHandler(), this);
	}

	private void registerCommands() {
		this.getCommand("hg").setExecutor(new CommandManager(this));
	}
	
	private void registerUtils() {
		this.configUtils = new ConfigUtils(this);
		this.timeUtils = new TimeUtils();
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public ConfigUtils getConfigUtils() {
		return configUtils;
	}

	public TimeUtils getTimeUtils() {
		return timeUtils;
	}

}
