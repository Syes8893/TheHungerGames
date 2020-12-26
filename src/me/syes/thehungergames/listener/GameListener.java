package me.syes.thehungergames.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;

import me.syes.thehungergames.TheHungerGames;
import me.syes.thehungergames.utils.ChestUtils;

public class GameListener implements Listener {

	private TheHungerGames plugin;
	
	private ChestUtils chestUtils;
	
	public GameListener(TheHungerGames plugin) {
		this.plugin = plugin;
		this.chestUtils = new ChestUtils();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);
		if(plugin.getGameManager().isGameRunning()) {
			int playersLeft = plugin.getGameManager().getRunningGame().getAlivePlayers().size();
			p.teleport(plugin.getGameManager().getRunningGame().getAlivePlayers().get(new Random().nextInt(playersLeft)));
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);
		if(plugin.getGameManager().isGameRunning()) {
			int playersLeft = plugin.getGameManager().getRunningGame().getAlivePlayers().size();
			p.teleport(plugin.getGameManager().getRunningGame().getAlivePlayers().get(new Random().nextInt(playersLeft)));
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(plugin.getGameManager().isGameRunning()) {
			if(plugin.getGameManager().getRunningGame().getCountdownTime() > 0) {
				if(e.getTo().getBlockX() != p.getLocation().getBlockX() || e.getTo().getBlockZ() != p.getLocation().getBlockZ()) {
		        	int x = e.getPlayer().getLocation().getBlockX();
		            int z = e.getPlayer().getLocation().getBlockZ();
		            e.getTo().setX(x + 0.5);
		            e.getTo().setZ(z + 0.5);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player))
			return;
		Player p = (Player) e.getEntity();
		if(plugin.getGameManager().isGameRunning())
			if(plugin.getGameManager().getRunningGame().getGameTime() < plugin.getGameManager().getRunningGame().getGraceTime()) {
				e.setCancelled(true);
			}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(plugin.getGameManager().isGameRunning()) {
			plugin.getGameManager().getRunningGame().killPlayerLeave(p);
		}
	}
	
	@EventHandler
	public void onBlockInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(plugin.getGameManager().isGameRunning())
			if(e.getClickedBlock() != null)
				if(e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
					//e.setCancelled(true);
					if(!plugin.getGameManager().getRunningGame().getOpenedChests().contains(e.getClickedBlock().getLocation())) {
						Chest chest = (Chest) e.getClickedBlock().getState();
						chest.getInventory().clear();
						chestUtils.generateChestLoot(chest.getInventory(), 3);
						chest.update();
						plugin.getGameManager().getRunningGame().getOpenedChests().add(e.getClickedBlock().getLocation());
					}
				}else if(e.getClickedBlock().getType() == Material.ENDER_CHEST) {
					e.setCancelled(true);
					BlockState blockstate = e.getClickedBlock().getState();
					if(!plugin.getGameManager().getRunningGame().getOpenedEnderChests().contains(blockstate)) {
						Inventory newInventory = Bukkit.createInventory(null, 27);
						chestUtils.generateChestLoot(newInventory, 5);
						p.openInventory(newInventory);
						e.getClickedBlock().setType(Material.AIR);
						e.getClickedBlock().getWorld().playEffect(blockstate.getLocation().add(0.5, 0.5, 0.5), Effect.MOBSPAWNER_FLAMES, 1);
						e.getClickedBlock().getWorld().playSound(blockstate.getLocation().add(0.5, 0.5, 0.5), Sound.ZOMBIE_WOODBREAK, 1, 1);
						plugin.getGameManager().getRunningGame().getOpenedEnderChests().add(blockstate);
					}
				}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(plugin.getGameManager().isGameRunning())
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(plugin.getGameManager().isGameRunning())
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		p.getWorld().strikeLightningEffect(p.getLocation());
		if(plugin.getGameManager().isGameRunning()) {
			String[] split = e.getDeathMessage().split(" ");
			e.setDeathMessage("§a§l[HG] " + "§c" + split[0] + " "
				 + split[1] + e.getDeathMessage().replace(split[0] + " " + split[1] + "", ""));
			plugin.getGameManager().getRunningGame().getDeadPlayers().add(p);
			plugin.getGameManager().getRunningGame().getAlivePlayers().remove(p);
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player))
			return;
		if(((Player)e.getEntity()).getFoodLevel() < e.getFoodLevel())
			return;
		if(new Random().nextInt(10) != 0)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
        if(e.toWeatherState())
            e.setCancelled(true);
	}
	
}
