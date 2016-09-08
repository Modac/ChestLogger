package org.myftp.p_productions.ChestLogger;

import static org.bukkit.ChatColor.COLOR_CHAR;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.GLASS;
import static org.bukkit.Material.STAINED_GLASS;
import static org.bukkit.Material.STAINED_GLASS_PANE;
import static org.bukkit.Material.STATIONARY_WATER;
import static org.bukkit.Material.THIN_GLASS;
import static org.bukkit.Material.WATER;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class ShowCommand {
	private static List<String> possibleNameColors = new ArrayList<>();
	static{
		for(char i=108;i<=111;i++){
			for(char m=48;m<=57;m++)
				possibleNameColors.add(COLOR_CHAR+""+m+""+COLOR_CHAR+""+i);
			for(char m=97;m<=101;m++)
				possibleNameColors.add(COLOR_CHAR+""+m+""+COLOR_CHAR+""+i);
		}
	}
	
	// TODO: Multiple Days support
	public static void showForPlayer(Player player, Location loc, long seconds, boolean excludeMe){
		Set<String> name = new HashSet<String>();
		name.add(player.getName());
		
		show(player, loc, seconds, excludeMe?name:null, true);
	}
	
	public static void showForPlayer(Player player, long seconds, boolean excludeMe){
		Set<Material> ibSet = new HashSet<>(Arrays.asList(WATER, STATIONARY_WATER, AIR, GLASS, STAINED_GLASS, STAINED_GLASS_PANE, THIN_GLASS));
		
		showForPlayer(player, player.getTargetBlock(ibSet, 50).getLocation(), seconds, excludeMe);
	}
	
	// TODO: More efficient file reading algorithm
	@SuppressWarnings("unused")
	public static void show(CommandSender sender, Location loc, long seconds, Set<String> ignoredPlayers, boolean colored ){
		if(!(loc.getBlock().getType()==Material.CHEST)){
			sender.sendMessage("There should be a chest!");
			return;
		}
		final Set<String> iPs = ((ignoredPlayers==null)?new HashSet<>():ignoredPlayers);
		Stream<String> lines=null;
		try {
			
			lines=Files.lines(new File(ChestLogger.instance.getDataFolder(),Utils.getLogfileName()).toPath());

			Map<String, String> nameColors = new HashMap<String, String>();
			List<String> posNColors = new ArrayList<>(possibleNameColors);
			
			Location chestloc = ((InventoryHolder)loc.getBlock().getState()).getInventory().getLocation();
			
			lines = lines
					.filter(line->{ String[] lineParts = line.split(" ");
									if(ChestLogger.instance.debug) sender.sendMessage(Arrays.toString(lineParts));
									boolean l = (lineParts.length>1);
									boolean nE = !lineParts[0].startsWith("[Exception]");
									boolean t = (seconds<0)?true:((Long.decode(lineParts[0])+seconds)>=new Date().getTime());
									boolean iP = !iPs.contains(lineParts[1]);
									boolean w = lineParts[3].equals(loc.getWorld().getName());
									boolean x = lineParts[4].equals(String.valueOf(chestloc.getBlockX()));
									boolean y = lineParts[5].equals(String.valueOf(chestloc.getBlockY()));
									boolean z = lineParts[6].equals(String.valueOf(chestloc.getBlockZ()));
									if(ChestLogger.instance.debug && false){
										sender.sendMessage("l: " + l);
										sender.sendMessage("nE: " + nE);
										sender.sendMessage("t: " + t);
										sender.sendMessage("iP: " + iP);
										sender.sendMessage("w: " + w + " | " + lineParts[3] + "==" + loc.getWorld().getName());
										sender.sendMessage("x: " + x + " | " + lineParts[4] + "==" + String.valueOf(chestloc.getBlockX()));
										sender.sendMessage("y: " + y + " | " + lineParts[5] + "==" + String.valueOf(chestloc.getBlockY()));
										sender.sendMessage("Z: " + z + " | " + lineParts[6] + "==" + String.valueOf(chestloc.getBlockZ()));
									}
									return l && nE && t && iP && w && x && y && z;
								});
			
			Map<String, MaterialAmounts> playerMAs = new HashMap<>(); 		// playerMaterialAmounts is too long
			Map<String, OpenedClosedTimes> playerOCs = new HashMap<>();		// playerOpenedClosedTimes is too long
			
			lines.forEach(msg->{
					String[] lineParts = msg.split(" ");
					long utimes = Long.valueOf(lineParts[0]);
					String player = lineParts[1];
					String actionType = lineParts[2];
					String world = lineParts[3];
					String x = lineParts[4];
					String y = lineParts[5];
					String z = lineParts[6];
					// DONE: Fix colouring
					/*
					if((!nameColors.containsKey(lineParts[1]))&&colored){
						int rand=(int)(Math.random() * posNColors.size());
						String color = posNColors.remove(rand);
						nameColors.put(lineParts[1], color);
						if(ChestLogger.instance.debug) sender.sendMessage(lineParts[1]+": "+color.replace(ChatColor.COLOR_CHAR, '&')+"("+posNColors.size()+"|"+rand+")");
					}
					if(colored){
						msg=msg.replace(lineParts[1], nameColors.get(lineParts[1])+lineParts[1]+ChatColor.RESET);
						if(ChestLogger.instance.debug) sender.sendMessage(msg.replace(ChatColor.COLOR_CHAR, '&'));
					}
					
					
					sender.sendMessage(Utils.niceFormatOfFileLogNativeMsg(msg));
					*/
					
					if(!playerMAs.containsKey(player))
						playerMAs.put(player, new MaterialAmounts());
					if(!playerOCs.containsKey(player))
						playerOCs.put(player, new OpenedClosedTimes());
					
					// DONE: impl other actionTypes
					
					MaterialAmounts playerMA = playerMAs.get(player);
					
					
					switch (actionType) {
					case "dropped":
						if(!(InventoryAction.valueOf(lineParts[10])==InventoryAction.NOTHING))
							playerMA.addToDrop(Material.getMaterial(lineParts[7]), Integer.valueOf(lineParts[8]));
						break;
					case "opened":
						playerOCs.get(player).addOpenedTime(utimes);
						break;
					case "closed":
						playerOCs.get(player).addClosedTime(utimes);
						break;
					case "dragged":
						Integer topCount = Integer.valueOf(lineParts[9]);
						Integer bottomCount = Integer.valueOf(lineParts[10]);
						int amountPerSlot = Integer.valueOf(lineParts[8]) / (topCount+bottomCount);
						Material mat = Material.getMaterial(lineParts[7]);
						playerMA.addToTop(mat, amountPerSlot*topCount);
						playerMA.addToBottom(mat, amountPerSlot*bottomCount);
						/*switch (DragType.valueOf(lineParts[11])) {
						case EVEN:
							playerMA.addToTop(mat, amountPerSlot*topCount);
							playerMA.addToBottom(mat, amountPerSlot*bottomCount);
							break;
						case SINGLE:
							break;
						}*/		// Unnecessary
						break;
					case "clicked":
						Material slotMat = Material.valueOf(lineParts[7]);
						Material cursorMat = Material.valueOf(lineParts[9]);
						int slotAmount = Integer.valueOf(lineParts[8]);
						int cursorAmount = Integer.valueOf(lineParts[10]);
						String invHolder = lineParts[11];
						// DONE: All inventory actions
						switch (InventoryAction.valueOf(lineParts[14])) {
						case COLLECT_TO_CURSOR:
							// DONE: COLLECT_TO_CURSOR handled by actionType collected
							break;
						case DROP_ALL_CURSOR:
							// Normally handled by drop actionType but you never know
							playerMA.addToDrop(cursorMat, cursorAmount);
							break;
						case DROP_ALL_SLOT:
							playerMA.addToDrop(slotMat, slotAmount);
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(slotMat, -slotAmount);
							else
								playerMA.addToTop(slotMat, -slotAmount);
							break;
						case DROP_ONE_CURSOR:
							playerMA.addToDrop(cursorMat, 1);
							break;
						case DROP_ONE_SLOT:
							playerMA.addToDrop(slotMat, 1);
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(slotMat, -1);
							else
								playerMA.addToTop(slotMat, -1);
							break;
						case HOTBAR_MOVE_AND_READD:
							// TODO: HOTBAR_MOVE_AND_READD
							break;
						case HOTBAR_SWAP:
							// TODO: HOTBAR_SWAP
							break;
						case MOVE_TO_OTHER_INVENTORY:
							if(invHolder.equals("CraftPlayer")){
								playerMA.addToBottom(slotMat, -slotAmount);
								playerMA.addToTop(slotMat, slotAmount);
							}else{
								playerMA.addToTop(slotMat, -slotAmount);
								playerMA.addToBottom(slotMat, slotAmount);
							}
							break;
						case NOTHING:
							break;
						case PICKUP_ALL:
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(slotMat, -slotAmount);
							else
								playerMA.addToTop(slotMat, -slotAmount);
							break;
						case PICKUP_HALF:
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(slotMat, -(slotAmount-slotAmount/2));
							else
								playerMA.addToTop(slotMat, -(slotAmount-slotAmount/2));
							break;
						case PICKUP_ONE:
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(slotMat, -1);
							else
								playerMA.addToTop(slotMat, -1);
							break;
						case PICKUP_SOME:
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(slotMat, -(cursorMat.getMaxStackSize()-cursorAmount));
							else
								playerMA.addToTop(slotMat, -(cursorMat.getMaxStackSize()-cursorAmount));
							break;
						case PLACE_ALL:
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(cursorMat, cursorAmount);
							else
								playerMA.addToTop(cursorMat, cursorAmount);
							break;
						case PLACE_ONE:
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(cursorMat, 1);
							else
								playerMA.addToTop(cursorMat, 1);
							break;
						case PLACE_SOME:
							if(invHolder.equals("CraftPlayer"))
								playerMA.addToBottom(cursorMat, slotMat.getMaxStackSize()-slotAmount);
							else
								playerMA.addToTop(cursorMat, slotMat.getMaxStackSize()-slotAmount);
							break;
						case SWAP_WITH_CURSOR:
							if(invHolder.equals("CraftPlayer")) {
								playerMA.addToBottom(slotMat, -slotAmount);
								playerMA.addToBottom(cursorMat, cursorAmount);
							} else {
								playerMA.addToTop(slotMat, -slotAmount);
								playerMA.addToTop(cursorMat, cursorAmount);
							}
							break;
						case UNKNOWN:
							// TODO: UNKNOWN
							break;
						default:
							break;
						}
						break;
					case "collected":
						String invHolder1 = lineParts[9];
						Material colMat = Material.valueOf(lineParts[7]);
						int colAmount = Integer.valueOf(lineParts[8]);
						if(invHolder1.equals("CraftPlayer")) {
							playerMA.addToBottom(colMat, -colAmount);
						} else {
							playerMA.addToTop(colMat, -colAmount);
						}
						break;
					default:
						break;
					}
					
					
				});
				
			
			// TODO: way nicer output
			for (Entry<String, MaterialAmounts> entry : playerMAs.entrySet()) {
				sender.sendMessage(entry.getKey());
				sender.sendMessage("  Top:");
				for (Entry<Material, Integer> maEntry : entry.getValue().topMaterialAmounts.entrySet()) {
					if(maEntry.getValue()!=0)
						sender.sendMessage("    "+maEntry.getValue() + " " + maEntry.getKey());
				}
				sender.sendMessage("  Bottom:");
				for (Entry<Material, Integer> maEntry : entry.getValue().bottomMaterialAmounts.entrySet()) {
					if(maEntry.getValue()!=0)
						sender.sendMessage("    "+maEntry.getValue() + " " + maEntry.getKey());
				}
				sender.sendMessage("  Drop:");
				for (Entry<Material, Integer> maEntry : entry.getValue().dropMaterialAmounts.entrySet()) {
					if(maEntry.getValue()!=0)
						sender.sendMessage("    "+maEntry.getValue() + " " + maEntry.getKey());
				}
			}
			
		} catch (IOException e) {
			sender.sendMessage("Error reading ChestLogger file");
			sender.sendMessage(e.getMessage());
		} finally {
			if(lines!=null) lines.close();
		}
	}
	
	public static void _debug_printNameColors(JavaPlugin plugin){
		plugin.getLogger().info(Arrays.toString(possibleNameColors.toArray())+"|"+possibleNameColors.size());
	}
	
	private static class MaterialAmounts{
		private Map<Material, Integer> bottomMaterialAmounts;
		private Map<Material, Integer> topMaterialAmounts;
		private Map<Material, Integer> dropMaterialAmounts;
		
		private CommandSender _DEBUG_sender;
		
		public MaterialAmounts() {
			bottomMaterialAmounts=new HashMap<>();
			topMaterialAmounts=new HashMap<>();
			dropMaterialAmounts=new HashMap<>();
		}
		
		@SuppressWarnings("unused")
		public MaterialAmounts(CommandSender sender){
			this();
			_DEBUG_sender=sender;
		}
		
		public void addToBottom(Material material, int amount){
			if(_DEBUG_sender!=null) _DEBUG_sender.sendMessage("addToBottom: " + material + " " + amount);
			bottomMaterialAmounts.put(material, bottomMaterialAmounts.getOrDefault(material, 0)+amount);
			//if(_DEBUG_sender!=null) _DEBUG_sender.sendMessage(ChatColor.AQUA+"bottomSize: " + bottomMaterialAmounts.size());
		}
		
		public void addToTop(Material material, int amount){
			if(_DEBUG_sender!=null) _DEBUG_sender.sendMessage("addToTop: " + material + " " + amount);
			topMaterialAmounts.put(material, topMaterialAmounts.getOrDefault(material, 0)+amount);
		}

		public void addToDrop(Material material, int amount){
			if(_DEBUG_sender!=null) _DEBUG_sender.sendMessage("addToDrop: " + material + " " + amount);
			dropMaterialAmounts.put(material, dropMaterialAmounts.getOrDefault(material, 0)+amount);
		}
		
	}
	
	private static class OpenedClosedTimes{
		private Set<Long> openedTimes;
		private Set<Long> closedTimes;
		
		public OpenedClosedTimes() {
			openedTimes = new HashSet<>();
			closedTimes = new HashSet<>();
		}
		
		public void addOpenedTime(long time){
			openedTimes.add(time);
		}
		
		public void addClosedTime(long time){
			closedTimes.add(time);
		}
		
	}
	
	
}
