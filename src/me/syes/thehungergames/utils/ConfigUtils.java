package me.syes.thehungergames.utils;

import org.bukkit.configuration.file.FileConfiguration;

import me.syes.thehungergames.TheHungerGames;

public class ConfigUtils {

	public static FileConfiguration config;
	private TheHungerGames plugin;
	
	public ConfigUtils(TheHungerGames plugin) {
		this.plugin = plugin;
		loadConfig();
	}
	
	public void loadConfig() {
		config = plugin.getConfig();
		config.options().copyDefaults(true);
		config.options().copyHeader(true);
	}
	
}
