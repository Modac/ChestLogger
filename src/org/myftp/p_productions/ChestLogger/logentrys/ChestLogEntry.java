/**
 * 
 */
package org.myftp.p_productions.ChestLogger.logentrys;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;

/**
 * @author Keller
 *
 */
public class ChestLogEntry {
	protected long unix;
	protected HumanEntity player;
	protected String actionType;
	protected Location loc;
	
	protected String consoleMsg;
	protected String fileLogNativeMsg;
	
	public ChestLogEntry(long unixTimestamp, HumanEntity player, String actionType, Location loc) {
		unix=unixTimestamp;
		this.player=player;
		this.actionType=actionType;
		this.loc=loc;
		
		consoleMsg=player.getName() + " " +
					actionType + " " +
					loc.getWorld().getName() + " " +
					loc.getBlockX() + " " +
					loc.getBlockY() + " " +
					loc.getBlockZ();
		fileLogNativeMsg=unix + " " + consoleMsg;
	}
	
	public long getUnixTimestamp(){
		return unix;
	}
	
	public HumanEntity getPlayer(){
		return player;
	}
	
	public String getActionType(){
		return actionType;
	}

	public String getConsoleMsg() {
		return consoleMsg;
	}

	public String getFileLogNativeMsg() {
		return fileLogNativeMsg;
	}
	
	// MAYBE: static method to construct log entry of a msg
	/*public static ChestLogEntry interpretConsoleMsg(String msg){
		interpretConsoleMsg(msg.split(" "));
	}

	public static ChestLogEntry interpretConsoleMsg(String[] split) {
		new ChestLogEntry(-1, HumanEntity, actionType)
	}*/
	
	

}
