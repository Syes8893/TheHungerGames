package me.syes.thehungergames.scoreboard;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.syes.thehungergames.TheHungerGames;
import me.syes.thehungergames.game.Game;
import me.syes.thehungergames.game.GameManager;

public class ScoreboardManager extends BukkitRunnable {
    
    private TheHungerGames plugin;
	
    private ScoreboardHandler scoreboardHandler;
    private String lines;
    private String separator;
    
    public ScoreboardManager(TheHungerGames plugin) {
    	this.plugin = plugin;
        this.scoreboardHandler = new ScoreboardHandler();
        this.lines = "&7&m---------------------";
        this.separator = "&f";
        this.runTaskTimer(plugin, 2, 2);
    }
    
    public void run() {
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            ScoreboardHelper board = this.scoreboardHandler.getScoreboard(p);
            board.clear();
    		Date now = new Date();
    		if(!plugin.getGameManager().isGameRunning()) {
                board.add(lines);
                //board.add("&7" + now.getDate() + "/" + (now.getMonth()+1) + " &8Mini01");
                board.add("&fOnline: &e" + Bukkit.getOnlinePlayers().size());
                board.add(separator);
                board.add("&7&o" + "relium.eu");
                //board.add("&7&orelium.eu");
                board.add(lines);
    		}else {
    			GameManager gm = plugin.getGameManager();
    			Game g = gm.getRunningGame();
    			if(gm.isCountdown()) {
                    board.add(lines);
                    //board.add("&7" + now.getDate() + "/" + (now.getMonth()+1) + " &8Mini01");
                    board.add("&fStarting in: &e" + plugin.getTimeUtils().getFullTime(g.getCountdownTime()));
                    board.add(separator);
                    board.add("&fPlayers: &e" + g.getAlivePlayers().size());
                    board.add(separator);
                    board.add("&7&orelium.eu");
                    board.add(lines);
    			}else if(gm.isStarted() && g.getGameTime() < g.getGraceTime() && !gm.isEnded()) {
                    board.add(lines);
                    //board.add("&7" + now.getDate() + "/" + (now.getMonth()+1) + " &8Mini01");
                    board.add("&fGrace Period Ends in:");
                    board.add(" &e" + plugin.getTimeUtils().getFullTime((g.getGraceTime() - g.getGameTime())));
                    board.add(separator);
                    board.add("&fAlive: &e" + g.getAlivePlayers().size() + " &7(" + g.getAlivePlayers().size() + "/" + (g.getDeadPlayers().size() + g.getAlivePlayers().size()) + ")");
                    board.add(separator);
                    board.add("&7&orelium.eu");
                    board.add(lines);
    			}else if(gm.isStarted() && g.getGameTime() <= g.getDeathmatchTime()/2 && !gm.isEnded()) {
                    board.add(lines);
                    //board.add("&7" + now.getDate() + "/" + (now.getMonth()+1) + " &8Mini01");
                    board.add("&fChests Refill in:");
                    board.add(" &e" + plugin.getTimeUtils().getFullTime((g.getDeathmatchTime()/2 - g.getGameTime())));
                    board.add(separator);
                    board.add("&fAlive: &e" + g.getAlivePlayers().size() + " &7(" + g.getAlivePlayers().size() + "/" + (g.getDeadPlayers().size() + g.getAlivePlayers().size()) + ")");
                    board.add(separator);
                    board.add("&7&orelium.eu");
                    board.add(lines);
    			}else if(gm.isStarted() && g.getGameTime() >= g.getDeathmatchTime()/2 && !gm.isEnded()) {
                    board.add(lines);
                    //board.add("&7" + now.getDate() + "/" + (now.getMonth()+1) + " &8Mini01");
                    board.add("&fDeathmatch in:");
                    board.add(" &c" + plugin.getTimeUtils().getFullTime((g.getDeathmatchTime() - g.getGameTime())));
                    board.add(separator);
                    board.add("&fAlive: &e" + g.getAlivePlayers().size() + " &7(" + g.getAlivePlayers().size() + "/" + (g.getDeadPlayers().size() + g.getAlivePlayers().size()) + ")");
                    board.add(separator);
                    board.add("&7&orelium.eu");
                    board.add(lines);
    			}else if(gm.isDeathmatch() && !gm.isEnded()) {
                    board.add(lines);
                    //board.add("&7" + now.getDate() + "/" + (now.getMonth()+1) + " &8Mini01");
                    board.add("&fGame End in:");
                    board.add(" &b" + plugin.getTimeUtils().getFullTime((g.getGameEndTime() - g.getGameTime())));
                    board.add(separator);
                    board.add("&fAlive: &e" + g.getAlivePlayers().size() + " &7(" + g.getAlivePlayers().size() + "/" + (g.getDeadPlayers().size() + g.getAlivePlayers().size()) + ")");
                    board.add(separator);
                    board.add("&7&orelium.eu");
                    board.add(lines);
    			}else if(gm.isEnded()) {
                    board.add(lines);
                    //board.add("&7" + now.getDate() + "/" + (now.getMonth()+1) + " &8Mini01");
                    board.add("&bGame Ended!");
                    board.add(separator);
                    if(g.getAlivePlayers().size() == 1)
                    	board.add("&fWinner: &e" + g.getAlivePlayers().get(0).getName());
                    else
                    	board.add("&fDraw!");
                    board.add(separator);
                    board.add("&7&orelium.eu");
                    board.add(lines);
    			}
    		}
            board.update(p);
        }
    }
    
    public static String convert(final int seconds) {
        final int h = seconds / 3600;
        final int i = seconds - h * 3600;
        final int m = i / 60;
        final int s = i - m * 60;
        String timeF = "";
        if (h < 10) {
            timeF = String.valueOf(timeF) + "0";
        }
        timeF = String.valueOf(timeF) + h + ":";
        if (m < 10) {
            timeF = String.valueOf(timeF) + "0";
        }
        timeF = String.valueOf(timeF) + m + ":";
        if (s < 10) {
            timeF = String.valueOf(timeF) + "0";
        }
        timeF = String.valueOf(timeF) + s;
        return timeF;
    }
    
    public String smallConvert(final int seconds) {
        final int h = seconds / 3600;
        final int i = seconds - h * 3600;
        final int m = i / 60;
        final int s = i - m * 60;
        String timeF = "";
        if (m >= 1) {
            timeF = String.valueOf(timeF) + m + "m";
        }
        /*if (s < 10) {
            timeF = String.valueOf(timeF) + "0";
        }*/
        timeF = String.valueOf(timeF) + s + "s";
        return timeF;
    }
    
    private String format(final double data) {
        final int minutes = (int)(data / 60.0);
        final int seconds = (int)(data % 60.0);
        final String str = String.format("%02d:%02d", minutes, seconds);
        return str;
    }
    
    public ScoreboardHandler getScoreboardHandler() {
    	return this.scoreboardHandler;
    }
}
