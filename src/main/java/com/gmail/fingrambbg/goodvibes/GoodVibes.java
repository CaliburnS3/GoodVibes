package com.gmail.fingrambbg.goodvibes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.*;

public class GoodVibes extends JavaPlugin {
	public static Plugin plugin;
	List<String> encourage;
	Random rand = new Random();
	int cooldownTime = 10;
	HashMap<Player, Long> hashy = new HashMap<Player, Long>();

	private enum Phases {
		ENABLE, DISABLE
	}

	Phases phase;

	FileConfiguration config = this.getConfig();

	@Override
	public void onEnable() {
		plugin = this;
		this.getConfig();
		phase = Phases.DISABLE;
		encourage = new ArrayList<String>();
		getLogger().info("GoodVibes enabled");
		init();
	}

	@Override
	public void onDisable() {
		phase = Phases.DISABLE;
		this.saveDefaultConfig();
		getLogger().info("GoodVibes disabled");
	}

	@SuppressWarnings("unchecked")
	public void init() {
		encourage.clear();
		encourage = (List<String>) config.getList("path.to.list");
		this.getConfig().set("path.to.list", encourage);
		config.options().copyDefaults(true);
		saveConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player){
		if (hashy.containsKey((Player) sender)) {
			long secondsLeft = ((hashy.get((Player) sender) / 1000) + cooldownTime)
					- (System.currentTimeMillis() / 1000);
			if (secondsLeft > 0) {
				// Still cooling down
				sender.sendMessage(
						ChatColor.GOLD + "You cant use that commands for another " + secondsLeft + " seconds!");
				return true;
			}
		}
		}
			
		if (cmd.getName().equalsIgnoreCase("celebrate")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().equals(args[0])) {
					targeted(player);
					hashy.put((Player) sender, System.currentTimeMillis());
					return true;
				}
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("encourage")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().equals(args[0])) {
					String temp = encourage.get(rand.nextInt(encourage.size()));
					String result = temp.replace("FILLER", sender.getName().toString());
					player.sendMessage(ChatColor.GOLD + result);
					sender.sendMessage(ChatColor.GOLD + "To " + player.getName() + ": " + result);
					hashy.put((Player) sender, System.currentTimeMillis());
					return true;
				}
			}
			return true;
			
			}
		
		if (cmd.getName().equalsIgnoreCase("rcelebrate")) {
			int temp = rand.nextInt(Bukkit.getOnlinePlayers().size());
			int count = 0;
			if(Bukkit.getOnlinePlayers().size() < 1){ return true; }
			for (Player player : Bukkit.getOnlinePlayers()){
				if(count == temp){
					targeted(player);
					hashy.put((Player) sender, System.currentTimeMillis());
					return true;
				}
				count++;
			}
			return true;
		}
		return true;
	}
	

	public void targeted(Player target) {
		target.sendMessage(ChatColor.GOLD + "We appreciate you! We hope you know that!");
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!player.equals(target)) {
				player.sendMessage(ChatColor.GOLD + "To show your appreciation to " + target.getName()
						+ " do /encourage " + target.getName());
			}
		}
	}
}
