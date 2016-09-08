package org.myftp.p_productions.ChestLogger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;


public class ChestLogger extends JavaPlugin{
	String prefix = ChatColor.RED+"["+getName()+"] "+ChatColor.RESET;
	static ChestLogger instance;
	
	boolean debug;
	
	static boolean active=false;
	
	@Override
	public void onEnable() {
		
		if(getConfig().getBoolean("enabled", true)){
			getServer().getPluginManager().registerEvents(new ChestListener(this), this);
			active=true;
		}
		
		getCommand("chestlogger").setExecutor(new ChestLoggerExecutor(this));
			
		debug = getConfig().getBoolean("debug", false);
		
		if(!Utils.initLogging(this, Utils.FILE_LOGGING_NATIVE)){
			getLogger().warning("Could not init logging");
			return;
		}
		
		// DONE: rewrite all this shit
		//		logger = Logger.getLogger(getName());
//		try {
//			String logfileName = String.format("chestLog_%1$td-%1$tm.txt", new Date());
//			String logfile = getDataFolder().getCanonicalPath().replace(new File(".").getCanonicalPath()+"\\", "").replace('\\', '/')+"/"+logfileName;
//			Files.createParentDirs(new File(logfile));
//			Handler hand = new FileHandler(logfile, true);//Integer.toHexString(new Long(System.currentTimeMillis()).hashCode()).toUpperCase() +".txt");
//			hand.setLevel(Level.ALL);
//			hand.setFormatter(new ChestLoggerFormater());
//			logger.setLevel(Level.ALL);
//			logger.setUseParentHandlers(false);
//			logger.addHandler(hand);
//			getLogger().info("Logging to file: " + logfile);
//		} catch (SecurityException | IOException | IllegalArgumentException e) {
//			getLogger().warning("Could not log to File");
//		}
		
		instance=this;
		
		// DEBUG
		// ShowCommand._debug_printNameColors(this);
		// DEBUG
		
		Utils.notify(this, "ChestLogger activated");
		
	}
	
	@Override
	public void onDisable() {

		Utils.notify(this, "ChestLogger deactivated");
	}
	
}
