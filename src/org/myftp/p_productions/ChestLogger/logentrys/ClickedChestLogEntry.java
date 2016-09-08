package org.myftp.p_productions.ChestLogger.logentrys;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.InventoryHolder;

public class ClickedChestLogEntry extends ChestLogEntry {

	protected Material slotItemType;
	protected int slotAmount;
	protected Material cursorItemType;
	protected int cursorAmount;
	protected InventoryHolder invHolder;
	protected int slot;
	protected ClickType type;
	protected InventoryAction action;
	
	public ClickedChestLogEntry(long unixTimestamp, HumanEntity player, Location loc, Material slotItemType, int slotAmount, Material cursorItemType, int cursorAmount, InventoryHolder invHolder, int slot, ClickType type, InventoryAction action) {
		super(unixTimestamp, player, "clicked", loc);
		this.slotItemType=slotItemType;
		this.slotAmount=slotAmount;
		this.cursorItemType=cursorItemType;
		this.cursorAmount=cursorAmount;
		this.invHolder=invHolder;
		this.slot=slot;
		this.type=type;
		this.action=action;
		
		consoleMsg = consoleMsg + " " + slotItemType + " " + slotAmount + " " + cursorItemType + " " + cursorAmount + " " + invHolder.getClass().getSimpleName() + " " + slot + " " + type + " " + action;
		fileLogNativeMsg = unix + " " + consoleMsg;
	}

	public ClickedChestLogEntry(HumanEntity player, Location loc, Material slotItemType, int slotAmount, Material cursorItemType, int cursorAmount, InventoryHolder invHolder, int slot, ClickType type, InventoryAction action) {
		this(new Date().getTime(), player, loc, slotItemType, slotAmount, cursorItemType, cursorAmount, invHolder, slot, type, action);
	}
	
	public Material getItemType() {
		return slotItemType;
	}

	public InventoryHolder getInvHolder() {
		return invHolder;
	}

	public int getSlot() {
		return slot;
	}

	public ClickType getType() {
		return type;
	}

	public InventoryAction getAction() {
		return action;
	}
	
	// MAYBE: static method to construct log entry of a msg

}
