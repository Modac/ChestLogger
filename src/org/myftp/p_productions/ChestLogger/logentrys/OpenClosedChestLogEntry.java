package org.myftp.p_productions.ChestLogger.logentrys;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;

public class OpenClosedChestLogEntry extends ChestLogEntry {

	protected String chestType;
	protected Location loc;
	
	public OpenClosedChestLogEntry(long unixTimestamp, HumanEntity player, Location loc, boolean opened, String chestType) {
		super(unixTimestamp, player, opened?"opened":"closed", loc);
		this.chestType=chestType;
		this.loc=loc;
		
		consoleMsg = consoleMsg + " " + chestType;
		fileLogNativeMsg = unix + " " + consoleMsg;
	}

	public OpenClosedChestLogEntry(HumanEntity player, Location loc, boolean opened, String chestType) {
		this(new Date().getTime(), player, loc, opened, chestType);
	}
	
	public String getChestType(){
		return chestType;
	}

	public Location getLoc() {
		return loc;
	}
	
	// MAYBE: static method to construct log entry of a msg

}
