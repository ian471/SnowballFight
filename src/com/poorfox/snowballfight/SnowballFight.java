package com.poorfox.snowballfight;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class SnowballFight extends JavaPlugin implements Listener {

	Objective scoreObjective;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard scoreboard = manager.getMainScoreboard();
		scoreObjective = scoreboard.getObjective("SnowScore");
		if(scoreObjective == null) {
			scoreObjective =
					scoreboard.registerNewObjective("SnowScore", "dummy");
		}
		scoreObjective.setDisplayName("Snow Score");
	}
	
	public void onDisable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		sender.sendMessage("You sent me a command: " + cmd);
		return true;
	}
	
	@EventHandler
	public void stopPvp(EntityDamageByEntityEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) return;
		if(event.getDamager().getType().equals(EntityType.PLAYER)){
			event.setCancelled(true);
			return;
		}
		if(event.getDamager() instanceof Arrow){
			Arrow arrow = (Arrow) event.getDamager();
			if(arrow.getShooter() instanceof Player){
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onSnowballHit(EntityDamageByEntityEvent event) {
		
		if (event.getEntityType() != EntityType.PLAYER) return;
		if (event.getDamager().getType() != EntityType.SNOWBALL) return;
		
		Player target = (Player) event.getEntity();
		
		Snowball snowball = (Snowball) event.getDamager();
		if (snowball.getShooter() == null ||
				snowball.getShooter().getType() != EntityType.PLAYER) return;
		Player thrower = (Player) snowball.getShooter();

		Score targetScore = scoreObjective.getScore(target.getDisplayName());
		Score throwerScore = scoreObjective.getScore(thrower.getDisplayName());
		
		// SCORING RULES:
		// The thrower takes 10% of the target's score, or 1 point at least,
		// plus one additional point.

		int scoreDelta = Math.max(1, targetScore.getScore() / 10);

		targetScore.setScore(targetScore.getScore() - scoreDelta);
		throwerScore.setScore(throwerScore.getScore() + scoreDelta + 1);
	}
	
}
