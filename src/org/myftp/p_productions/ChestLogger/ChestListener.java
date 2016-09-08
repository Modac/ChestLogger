package org.myftp.p_productions.ChestLogger;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.myftp.p_productions.ChestLogger.logentrys.ClickedChestLogEntry;
import org.myftp.p_productions.ChestLogger.logentrys.CollectedChestLogEntry;
import org.myftp.p_productions.ChestLogger.logentrys.DraggedChestLogEntry;
import org.myftp.p_productions.ChestLogger.logentrys.DroppedChestLogEntry;
import org.myftp.p_productions.ChestLogger.logentrys.OpenClosedChestLogEntry;

public class ChestListener implements Listener{
	ChestLogger plugin;
	
	public ChestListener(ChestLogger plugin) {
		this.plugin=plugin;
	}
	
	// DONE: fit it to new system
	
	// MAYBE: close and open sometimes not passed
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event){
		try{	
			String chestType = "unknown";
			if(event.getInventory().getHolder() instanceof Chest)
				chestType = "SingleChest";
			if(event.getInventory().getHolder() instanceof DoubleChest)
				chestType = "DoubleChest";
			else return;
		
			boolean logRet = Utils.log(new OpenClosedChestLogEntry(event.getPlayer()
												, event.getInventory().getLocation()
												, true
												, chestType));
			
			if(plugin.debug) Utils.notify(plugin, "onInvOpen: " + logRet);
		}catch(Exception e){
			Utils.logException("Exception occured", e);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event){
		try{	
			String chestType = "unknown";
			if(event.getInventory().getHolder() instanceof Chest)
				chestType = "SingleChest";
			if(event.getInventory().getHolder() instanceof DoubleChest)
				chestType = "DoubleChest";
			else return;
			
			boolean logRet = Utils.log(new OpenClosedChestLogEntry(event.getPlayer()
												, event.getInventory().getLocation()
												, false
												, chestType));
			if(plugin.debug) Utils.notify(plugin, "onInvClose: " + logRet);
		}catch(Exception e){
			Utils.logException("Exception occured", e);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		try{
			if(event.getView().getTopInventory().getHolder() instanceof Chest || event.getView().getTopInventory().getHolder() instanceof DoubleChest){
				boolean logRet = false;
				if(event.getAction()==InventoryAction.COLLECT_TO_CURSOR){
					AtomicInteger itemAmount=new AtomicInteger(0);
					event.getClickedInventory().all(event.getCurrentItem().getType()).forEach((slot, itemStack) -> itemAmount.addAndGet(itemStack.getAmount()));
					int amount= (itemAmount.get()+event.getCursor().getAmount())<=event.getCursor().getMaxStackSize()?itemAmount.get():event.getCursor().getMaxStackSize();
					logRet = Utils.log(new CollectedChestLogEntry(event.getWhoClicked(), 					event.getView().getTopInventory().getLocation(), 
																  event.getCursor().getType(), 				amount, 
																  event.getClickedInventory().getHolder(), 	event.getClick(), 
																  event.getAction()));
				} else if(!(event.getSlotType()==SlotType.OUTSIDE)){
					logRet = Utils.log(new ClickedChestLogEntry(event.getWhoClicked(), 					event.getView().getTopInventory().getLocation(),
																event.getCurrentItem().getType(),		event.getCurrentItem().getAmount(), event.getCursor().getType(), event.getCursor().getAmount(), 
																event.getClickedInventory().getHolder(),event.getRawSlot(), 
																event.getClick(), 						event.getAction()));
				} else{
					logRet = Utils.log(new DroppedChestLogEntry(event.getWhoClicked(), event.getView().getTopInventory().getLocation(), event.getCursor().getType(), event.getCursor().getAmount(), event.getClick(), event.getAction()));
				}
				if(plugin.debug) Utils.notify(plugin, "onInvClick: " + logRet);
			}
		}catch(Exception e){
			Utils.logException("Exception occured", e);
		}
	}
	
	/*@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event){
		if(event.getInitiator().getHolder() instanceof Hopper) return;
		Utils.notify(plugin, event.getInitiator().getHolder().getClass() + " moved " + event.getItem().getType() + " from " + event.getSource().getName() + " to " + event.getDestination().getName());
	}*/
	
	@EventHandler
	public void onInverntoryDrag(InventoryDragEvent event){
		try{
			if(event.getView().getTopInventory().getHolder() instanceof Chest || event.getView().getTopInventory().getHolder() instanceof DoubleChest){
				boolean logRet = Utils.log(new DraggedChestLogEntry(event.getWhoClicked()
												, event.getView().getTopInventory().getLocation()
												, event.getOldCursor().getType()
												, (event.getOldCursor()==null?0:event.getOldCursor().getAmount()
														-(event.getCursor()==null?0:event.getCursor().getAmount()))
												, event.getView()
												, event.getRawSlots()
												, event.getType()));
				if(plugin.debug) Utils.notify(plugin, "onInvDrag: " + logRet);
			}
		}catch(Exception e){
			Utils.logException("Exception occured", e);
		}
	}
	
	/*@EventHandler
	public void onChestInteract(PlayerInteractEvent event){
		Block clickedBlock = event.getClickedBlock();
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK && clickedBlock.getType()==Material.CHEST){
			Location chestloc = ((InventoryHolder)clickedBlock.getState()).getInventory().getLocation();
			Utils.notify(plugin, event.getPlayer().getName() + " right clicked chest at " + chestloc.getX() + "|" + chestloc.getY() + "|" + chestloc.getZ());
		}
	}*/
	
}
