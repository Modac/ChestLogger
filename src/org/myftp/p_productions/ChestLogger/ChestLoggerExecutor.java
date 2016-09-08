package org.myftp.p_productions.ChestLogger;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ChestLoggerExecutor implements CommandExecutor {
	ChestLogger plugin;
	
	public ChestLoggerExecutor(ChestLogger plugin) {
		this.plugin=plugin;
	}
	
	// TODO: introduce option interpreter

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("chestlogger")) {
			if( !sender.hasPermission("chestlogger.chestlogger")){
				sender.sendMessage(ChatColor.RED+"You don't have permission!");
				return true;
			}
			if(args.length<1)
				return false;
			String[] argsNew = Arrays.copyOfRange(args, 1, args.length);
			switch(args[0].toLowerCase()){
				case "enable":
					return enableCommand(sender, cmd, label);
				case "disable":
					return disableCommand(sender, cmd, label);
				case "debug":
					if(args.length<2){
						sender.sendMessage("debug true|false");
						return true;
					}
					return debugCommand(sender, cmd, label, args[1]);
				
			}
			if(sender instanceof Player){
				if(args.length<1)
					return false;
				switch(args[0].toLowerCase()){
					case "show":
						return showCommandPlayer(sender, cmd, label, argsNew);
				}
			} else{
				
			}
		}	
		return false;
	}

	private boolean debugCommand(CommandSender sender, Command cmd, String label, String arg) {
		try{
			plugin.debug=Boolean.parseBoolean(arg);
			plugin.getConfig().set("debug",	plugin.debug);
			plugin.saveConfig();
			sender.sendMessage("Debug changed to: "+plugin.debug);
		} catch(Exception e){
			sender.sendMessage("debug true|false");
		}
		return true;
	}

	private boolean enableCommand(CommandSender sender, Command cmd, String label) {
		if( !sender.hasPermission("chestlogger.disable")){
			sender.sendMessage(ChatColor.RED+"You don't have permission!");
			return true;
		}
		if(ChestLogger.active){
			sender.sendMessage(ChatColor.BLUE+"Chest Logger is already activated!");
			return true;
		}
		plugin.getServer().getPluginManager().registerEvents(new ChestListener(plugin), plugin);
		ChestLogger.active=true;
		plugin.getConfig().set("enabled", true);
		plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN+"Chest Logger activated");
		return true;
	}

	private boolean disableCommand(CommandSender sender, Command cmd, String label) {
		if( !sender.hasPermission("chestlogger.disable")){
			sender.sendMessage(ChatColor.RED+"You don't have permission!");
			return true;
		}
		if(!ChestLogger.active){
			sender.sendMessage(ChatColor.BLUE+"Chest Logger is already disabled!");
			return true;
		}
		HandlerList.unregisterAll(plugin);
		ChestLogger.active=false;
		plugin.getConfig().set("enabled", false);
		plugin.saveConfig();
		sender.sendMessage(ChatColor.GREEN+"Chest Logger disabled");
		return true;
	}
	
	// TODO: Usage, Implement all possibilities specified in formats.txt
	private boolean showCommandPlayer(CommandSender sender, Command cmd, String label, String[] args) {
		String usage="show (looking block) (true) (-1)\n"+
					 "show x y z\n"+
					 "show x y z world\n"+
					 "show x y z world excludeme\n"+
					 "show x y z world excludeme seconds";
		
		if( !sender.hasPermission("chestlogger.show")){
			sender.sendMessage(ChatColor.RED+"You don't have permission!");
			return true;
		}
		if(args.length==0){
			try {
				ShowCommand.showForPlayer((Player) sender, -1, true);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.AQUA+usage);
				if(plugin.debug){
					sender.sendMessage(e.toString());
					for (StackTraceElement stackTraceElem : e.getStackTrace()) {
						sender.sendMessage("    "+stackTraceElem);
					}
				}
			}
		} else if(args.length==3){
			try {
				ShowCommand.showForPlayer((Player) sender, new Location(sender.getServer().getWorlds().get(0),
																		Double.parseDouble(args[0]),
																		Double.parseDouble(args[1]),
																		Double.parseDouble(args[2])
																				), -1, true);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.AQUA+usage);
				if(plugin.debug){
					sender.sendMessage(e.toString());
					for (StackTraceElement stackTraceElem : e.getStackTrace()) {
						sender.sendMessage("    "+stackTraceElem);
					}
				}
			}
		} else if(args.length==4){
			try {
				ShowCommand.showForPlayer((Player) sender, new Location(sender.getServer().getWorld(args[3]),
																		Double.parseDouble(args[0]),
																		Double.parseDouble(args[1]),
																		Double.parseDouble(args[2])
																				), -1, true);
			} catch (Exception e) {
				sender.sendMessage(ChatColor.AQUA+usage);
				if(plugin.debug){
					sender.sendMessage(e.toString());
					for (StackTraceElement stackTraceElem : e.getStackTrace()) {
						sender.sendMessage("    "+stackTraceElem);
					}
				}
			}
		} else if(args.length==5){
			try {
				ShowCommand.showForPlayer((Player) sender, new Location(sender.getServer().getWorld(args[3]),
																		Double.parseDouble(args[0]),
																		Double.parseDouble(args[1]),
																		Double.parseDouble(args[2])
																				), -1, Boolean.valueOf(args[4]));
			} catch (Exception e) {
				sender.sendMessage(ChatColor.AQUA+usage);
				if(plugin.debug){
					sender.sendMessage(e.toString());
					for (StackTraceElement stackTraceElem : e.getStackTrace()) {
						sender.sendMessage("    "+stackTraceElem);
					}
				}
			}
		} else if(args.length==6){
			try {
				ShowCommand.showForPlayer((Player) sender, new Location(sender.getServer().getWorld(args[3]),
																		Double.parseDouble(args[0]),
																		Double.parseDouble(args[1]),
																		Double.parseDouble(args[2])
																				),
															Long.parseLong(args[5]),
															Boolean.valueOf(args[4]));
			} catch (Exception e) {
				sender.sendMessage(ChatColor.AQUA+usage);
				if(plugin.debug){
					sender.sendMessage(e.toString());
					for (StackTraceElement stackTraceElem : e.getStackTrace()) {
						sender.sendMessage("    "+stackTraceElem);
					}
				}
			}
		} else{
			sender.sendMessage(ChatColor.AQUA + usage);
			if(plugin.debug) sender.sendMessage(Arrays.toString(args));
		}
		return true;
	}

}
