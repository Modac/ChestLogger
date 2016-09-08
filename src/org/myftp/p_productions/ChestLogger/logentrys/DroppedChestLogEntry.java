package org.myftp.p_productions.ChestLogger.logentrys;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class DroppedChestLogEntry extends ChestLogEntry {
	
	protected Material itemType;
	protected int amount;
	protected ClickType type;
	protected InventoryAction action;
	
	public DroppedChestLogEntry(long unixTimestamp, Location loc, HumanEntity player, Material itemType, int amount, ClickType type, InventoryAction action) {
		super(unixTimestamp, player, "dropped", loc);
		this.itemType=itemType;
		this.amount=amount;
		this.type=type;
		this.action=action;
		
		consoleMsg = consoleMsg + " " + itemType + " " + amount + " " + type + " " + action;
		fileLogNativeMsg = unix + " " + consoleMsg;
	}

	public DroppedChestLogEntry(HumanEntity player, Location loc, Material itemType, int amount, ClickType type, InventoryAction action) {
		this(new Date().getTime(), loc, player, itemType, amount, type, action);
	}
	
	public Material getItemType() {
		return itemType;
	}
	
	public int getAmount() {
		return amount;
	}

	public ClickType getType() {
		return type;
	}

	public InventoryAction getAction() {
		return action;
	}

	// MAYBE: static method to construct log entry of a msg

}
