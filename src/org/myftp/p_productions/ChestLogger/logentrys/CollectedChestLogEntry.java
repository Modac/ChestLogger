package org.myftp.p_productions.ChestLogger.logentrys;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;

public class CollectedChestLogEntry extends ChestLogEntry {
	
	Material itemType;
	int amount;
	InventoryHolder invHolder;
	ClickType type;
	InventoryAction action;
	
	
	public CollectedChestLogEntry(long unixTimestamp, HumanEntity player, Location loc, Material itemType, int amount, InventoryHolder invHolder, ClickType clickType, InventoryAction action) {
		super(unixTimestamp, player, "collected", loc);
		this.itemType=itemType;
		this.amount=amount;
		this.invHolder=invHolder;
		this.type=clickType;
		this.action=action;
		
		consoleMsg = consoleMsg + " " + itemType + " " + amount + " " + invHolder.getClass().getSimpleName() + " " + clickType + " " + action;
		fileLogNativeMsg = unix + " " + consoleMsg;
		
	}
	
	public CollectedChestLogEntry(HumanEntity player, Location loc, Material itemType, int amount, InventoryHolder invHolder, ClickType clickType, InventoryAction action) {
		this(new Date().getTime(), player, loc, itemType, amount, invHolder, clickType, action);
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
}
