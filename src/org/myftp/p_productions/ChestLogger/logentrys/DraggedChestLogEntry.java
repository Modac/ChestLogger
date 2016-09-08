package org.myftp.p_productions.ChestLogger.logentrys;

import java.util.Date;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.InventoryView;

public class DraggedChestLogEntry extends ChestLogEntry {

	protected Material itemType;
	protected int amount;
	protected InventoryView view;
	protected Set<Integer> rawSlots;
	protected DragType type;
	
	public DraggedChestLogEntry(long unixTimestamp, Location loc, HumanEntity player, Material itemType, int amount, InventoryView view, Set<Integer> rawSlots, DragType type) {
		super(unixTimestamp, player, "dragged", loc);
		this.itemType=itemType;
		this.amount=amount;
		this.view=view;
		this.rawSlots=rawSlots;
		this.type=type;
		
		consoleMsg = consoleMsg + " " + itemType + " " + amount + " " + getTopSlotsCount() + " " + getBottomSlotsCount() + " " + type;
		fileLogNativeMsg = unix + " " + consoleMsg;
	}
	
	public DraggedChestLogEntry(HumanEntity player, Location loc, Material itemType, int amount, InventoryView view, Set<Integer> rawSlots, DragType type) {
		this(new Date().getTime(), loc, player, itemType, amount, view, rawSlots, type);
	}
	
	public int getAmount() {
		return amount;
	}

	public InventoryView getView() {
		return view;
	}

	public Set<Integer> getRawSlots() {
		return rawSlots;
	}
	
	public int getTopSlotsCount(){
		int i=0;
		for (Integer slot : rawSlots) {
			if(slot==view.convertSlot(slot))i++;
		}
		return i;
	}
	
	public int getBottomSlotsCount(){
		int i=0;
		for (Integer slot : rawSlots) {
			if(slot!=view.convertSlot(slot))i++;
		}
		return i;
	}
	
	public DragType getType() {
		return type;
	}

	// MAYBE: static method to construct log entry of a msg

}
