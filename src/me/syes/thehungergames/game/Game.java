package me.syes.thehungergames.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.syes.thehungergames.TheHungerGames;

public class Game {

	private TheHungerGames plugin;

	private int countdownTime;
	private int gameTime;
	private int graceTime;
	private int deathmatchTime;
	private int gameEndTime;
	
	private ArrayList<Location> spawnPoints;
	
	private Location center;
	
	private ArrayList<Player> alivePlayers;
	private ArrayList<Player> deadPlayers;
	
	private ArrayList<Location> openedChests;
	private ArrayList<BlockState> openedEnderChests;
	
	private String winnerName;
	
	public Game(TheHungerGames plugin) {
		this.plugin = plugin;
		this.spawnPoints = new ArrayList<>();
		this.alivePlayers = new ArrayList<>();
		this.deadPlayers = new ArrayList<>();
		this.openedChests = new ArrayList<>();
		this.openedEnderChests = new ArrayList<>();
	}
	
	public void startCountdown() {
		this.countdownTime = 11;
		loadSpawns();
		loadPlayers();
		if(alivePlayers.size() < 2) {
			msgAll("&a&l[HG] &cNot enough players, the game has been cancelled.");
			resetGame();
			return;
		}
		plugin.getGameManager().setRunningGame(this);
		for(Player p : alivePlayers) {
			teleportToSpawn(p);
			resetPlayer(p, GameMode.SURVIVAL);
		}
		new BukkitRunnable() {
			public void run() {
				countdownTime--;
				if(countdownTime == 0) {
					startGame();
					this.cancel();
					return;
				}
				if(isMessageTime(countdownTime))
					msgAll("&a&l[HG] &fGame starting in &e" + plugin.getTimeUtils().getTime(countdownTime));
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void startGame() {
		this.gameTime = 0;
		this.graceTime = 20;
		//this.deathmatchTime = 7 * 60;
		this.deathmatchTime = 40;
		this.gameEndTime = 10 * 60;
		msgAll("&a&l[HG] &fThe Game has started, &aGood Luck!");
		msgAll("&a&l[HG] &fYou have a Grace Period of &e20 seconds");
		for(Player p : alivePlayers)
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
		new BukkitRunnable() {
			public void run() {
				gameTime++;
				if(deathmatchTime - gameTime == 0) {
					startDeathmatch();
					this.cancel();
					return;
				}else if(alivePlayers.size() == 1) {
					endGame(false);
					this.cancel();
					return;
				}
				if(gameTime == deathmatchTime/2)
					refillChests();
				if(gameTime == graceTime)
					msgAll("&a&l[HG] &fThe Grace Period has ended!");
				if(isMessageTime(deathmatchTime - gameTime))
					msgAll("&a&l[HG] &fDeathmatch starting in &e" + plugin.getTimeUtils().getTime(deathmatchTime - gameTime));
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	public void startDeathmatch() {
		msgAll("&a&l[HG] &fThe Deathmatch has begun, &cKill your opponents!");
		for(Player p : alivePlayers)
			teleportToSpawn(p);
		new BukkitRunnable() {
			public void run() {
				for(Player p : alivePlayers)
					if(p.getLocation().distance(center) > 25) {
						p.damage(1);
						p.setFoodLevel(p.getFoodLevel() - 1);
						p.sendMessage("§a§l[HG] §cYou are taking damage, get closer to the center!!");
					}
				if(gameEndTime - gameTime == 0) {
					endGame(true);
					this.cancel();
					return;
				}else if(alivePlayers.size() == 1) {
					endGame(false);
					this.cancel();
					return;
				}
				gameTime++;
			}
		}.runTaskTimer(plugin, 0, 20);
	}
	
	public void endGame(boolean tie) {
		winnerName = alivePlayers.get(0).getName();
		msgAll("&7&m------------------------------");
		msgAll("       &a&lThe Hunger Games");
		msgAll("&7");
		if(tie)
			msgAll("The game ended in a draw, there was no victorious player!");
		if(!tie)
			msgAll("Congratulations to &b" + winnerName + " §ffor winning the game!");
		msgAll("&7");
		msgAll("&7§m------------------------------");
		new BukkitRunnable() {
			public void run() {
				resetGame();
			}
		}.runTaskLater(plugin, 15 * 20);
	}
	
	public void resetGame() {
		plugin.getGameManager().resetRunningGame();
		plugin.getGameManager().setRunningGame(null);
		
		for(BlockState bs : openedEnderChests) {
			bs.getLocation().getBlock().setType(bs.getType());
			bs.getLocation().getBlock().setData(bs.getRawData());
		}
		
		for(Entity e : spawnPoints.get(0).getWorld().getEntities())
			if(!(e instanceof Player))
				e.remove();
		
		for(Player p : alivePlayers) {
			resetPlayer(p, GameMode.SPECTATOR);
		}
	}
	
	//GAME MANAGEMENT

	private void loadPlayers() {
		for(Player p : Bukkit.getOnlinePlayers())
			if(!p.isDead())
				alivePlayers.add(p);
	}
	
	private void loadSpawns() {
		String worldName = plugin.getConfig().getString("World");
		this.center = new Location(Bukkit.getWorld(worldName)
				, plugin.getConfig().getInt("Center.x")
				, plugin.getConfig().getInt("Center.y")
				, plugin.getConfig().getInt("Center.z"));
		for(String str : plugin.getConfig().getKeys(false)) {
			if(str.equals("World"))
				continue;
			int x = plugin.getConfig().getInt(str + ".x");
			int y = plugin.getConfig().getInt(str + ".y");
			int z = plugin.getConfig().getInt(str + ".z");
			int yaw = plugin.getConfig().getInt(str + ".yaw");
			int pitch = plugin.getConfig().getInt(str + ".pitch");
			this.spawnPoints.add(new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch));
		}
	}
	
	private void teleportToSpawn(Player p) {
		if(alivePlayers.size() <= spawnPoints.size())
			p.teleport(spawnPoints.get(alivePlayers.indexOf(p)).clone().add(0.5, 0.5, 0.5));
		else
			p.teleport(spawnPoints.get(0).clone().add(0.5, 0.5, 0.5));
	}
	
	public void killPlayerLeave(Player p) {
		for(ItemStack i : p.getInventory()) {
			if(i!=null) 
				if(i.getType() != Material.AIR)
					p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.2, 0), i);
		}
		for(ItemStack i : p.getInventory().getArmorContents()) {
			if(i!=null)
				if(i.getType() != Material.AIR)
					p.getWorld().dropItemNaturally(p.getLocation().add(0, 0.2, 0), i);
		}
		msgAll("&c" + p.getName() + " was eliminated!");
		deadPlayers.add(p);
		alivePlayers.remove(p);
	}
	
	private void refillChests() {
		this.openedChests.clear();
		
		for(BlockState bs : openedEnderChests) {
			bs.getLocation().getBlock().setType(bs.getType());
			bs.getLocation().getBlock().setData(bs.getRawData());
		}
		
		openedEnderChests.clear();
			
		msgAll("&a&l[HG] &fAll chests have been refilled!");
	}
	
	private void resetPlayer(Player p, GameMode gameMode) {
		p.closeInventory();
		p.getInventory().clear();
		p.getInventory().setArmorContents((ItemStack[]) null);
		p.setFoodLevel(20);
		p.setSaturation(10);
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setExp(0);
		p.setLevel(0);
		p.setGameMode(gameMode);
		for(PotionEffect pe : p.getActivePotionEffects())
			p.removePotionEffect(pe.getType());
	}
	
	
	//EXTRA UTILS

	private void msgAll(String msg) {
		for(Player p : Bukkit.getServer().getOnlinePlayers())
			p.sendMessage(msg.replace("&", "§"));
	}
	
	private boolean isMessageTime(int time) {
		if(time <= 5)
			return true;
		if(time == 10)
			return true;
		if(time == 20)
			return true;
		if(time == 30)
			return true;
		if(time == 60)
			return true;
		if(time == 60*5)
			return true;
		if(time == 60*10)
			return true;
		return false;
	}
	
	//-------------------------------
	//            GETTERS
	//-------------------------------
	
	public int getCountdownTime() {
		return countdownTime;
	}

	public int getGameTime() {
		return gameTime;
	}

	public int getGraceTime() {
		return graceTime;
	}

	public int getDeathmatchTime() {
		return deathmatchTime;
	}

	public int getGameEndTime() {
		return gameEndTime;
	}

	public ArrayList<Player> getAlivePlayers() {
		return alivePlayers;
	}

	public ArrayList<Player> getDeadPlayers() {
		return deadPlayers;
	}

	public ArrayList<Location> getSpawnPoints() {
		return spawnPoints;
	}

	public ArrayList<Location> getOpenedChests() {
		return openedChests;
	}

	public ArrayList<BlockState> getOpenedEnderChests() {
		return openedEnderChests;
	}

	public String getWinnerName() {
		return winnerName;
	}
	
}
