package me.syes.thehungergames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.syes.thehungergames.TheHungerGames;
import me.syes.thehungergames.game.Game;
import me.syes.thehungergames.game.GameManager;

public class CommandManager implements CommandExecutor {

	private TheHungerGames plugin;
	private GameManager gameManager;
	
	public CommandManager(TheHungerGames plugin) {
		this.plugin = plugin;
		this.gameManager = plugin.getGameManager();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return true;
		if(args.length == 0)
			return true;
		if(args[0].equalsIgnoreCase("start")) {
			if(plugin.getGameManager().isGameRunning()) {
				sender.sendMessage("§a§l[HG] §cA game is already running!");
				return true;
			}
			Bukkit.broadcastMessage("§a§l[HG] §fInitiating game...");
			Game g = new Game(plugin);
			g.startCountdown();
			//gameManager.setRunningGame(g);
		}else if(args[0].equalsIgnoreCase("setworld")) {
			plugin.getConfig().set("World", ((Player)sender).getWorld().getName());
		}else if(args[0].equalsIgnoreCase("setcenter")) {
			plugin.getConfig().set("Center.x", ((Player)sender).getLocation().getBlockX());
			plugin.getConfig().set("Center.y", ((Player)sender).getLocation().getBlockY());
			plugin.getConfig().set("Center.z", ((Player)sender).getLocation().getBlockZ());
		}else if(args[0].equalsIgnoreCase("addspawn")) {
			int amount = plugin.getConfig().getKeys(false).size();
			plugin.getConfig().set("spawn_" + amount + ".x", ((Player)sender).getLocation().getBlockX());
			plugin.getConfig().set("spawn_" + amount + ".y", ((Player)sender).getLocation().getBlockY());
			plugin.getConfig().set("spawn_" + amount + ".z", ((Player)sender).getLocation().getBlockZ());
			plugin.getConfig().set("spawn_" + amount + ".yaw", ((Player)sender).getLocation().getYaw());
			plugin.getConfig().set("spawn_" + amount + ".pitch", ((Player)sender).getLocation().getPitch());
		}else if(args[0].equalsIgnoreCase("resetspawns")) {
			for(String str : plugin.getConfig().getKeys(false))
				plugin.getConfig().set(str, null);
		}
		return false;
	}
	
}
