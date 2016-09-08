package org.myftp.p_productions.ChestLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.myftp.p_productions.ChestLogger.logentrys.ChestLogEntry;

import com.google.common.io.Files;

public class Utils {
	
	private static String logfileName = String.format("chestLog_%1$td-%1$tm.txt", new Date());
	
	public static String getLogfileName(){
		return logfileName;
	}
	
	public static final int FILE_LOGGING_NATIVE = 1;
	@Deprecated
	public static final int FILE_LOGGING_JAVA = 2;
	public static final int CONSOLE_LOGGING = 3;
	private static int loggingMode = -1;
	private static Logger logger;
	private static PrintStream ps;
	
	
	public static int broadcastToPlayers(Server server, String msg, String perm){
		AtomicInteger count = new AtomicInteger(0);
		server.getOnlinePlayers().forEach(player->{if(player.hasPermission(perm)) player.sendMessage(msg); count.incrementAndGet();});
		return count.get();
	}
	
	public static int broadcastToPlayers(String msg, String perm){
		return broadcastToPlayers(Bukkit.getServer(), msg, perm);
	}
	
	
	// Very specific for my needs.
	public static void notify(ChestLogger plugin, String msg){
		notify(plugin, msg, Level.INFO);
	}
	
	public static void notify(ChestLogger plugin, String msg, Level level){
		plugin.getLogger().log(level, msg);
		broadcastToPlayers(plugin.getServer(), plugin.prefix + msg, "chestlogger.msg");
	}
	
	public static boolean log(ChestLogEntry logEntry){
		switch(loggingMode){
			case FILE_LOGGING_NATIVE:
				logNative(logEntry);
				return true;
			case CONSOLE_LOGGING:
				logConsole(logEntry);
				return true;
		}
		return false;
	}
	
	private static void logNative(ChestLogEntry logEntry) {
		ps.println(logEntry.getFileLogNativeMsg());
	}

	private static void logConsole(ChestLogEntry logEntry) {
		logger.info(logEntry.getConsoleMsg());
	}

	public static boolean logException(String msg, Exception exception){
		switch(loggingMode){
		case FILE_LOGGING_NATIVE:
			logExceptionNative(msg, exception);
			return true;
		case CONSOLE_LOGGING:
			logExceptionConsole(msg, exception);
			return true;
		}
		return false;
	}
	
	private static void logExceptionNative(String msg, Exception exception) {
		ps.println("[Exception] " + new Date().getTime() + " " + msg);
		
		String throwable = "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.close();
        throwable = "[Exception] " + sw.toString().replace("\n", "\n[Exception] ");
        
        ps.println(throwable);
        
	}
	
	private static void logExceptionConsole(String msg, Exception exception) {
		logger.log(Level.WARNING, msg, exception);
	}

	public static boolean initLogging(JavaPlugin plugin, int loggingMode){

		Utils.loggingMode=loggingMode;
		
		switch(loggingMode){
			case FILE_LOGGING_NATIVE:
				try{
					File logfile = new File(plugin.getDataFolder(),logfileName);
					try {
						Files.createParentDirs(logfile);
					} catch (IOException e) {
						return false;
					}
					ps=new PrintStream(new FileOutputStream(logfile, true), true, StandardCharsets.UTF_8.name());
				} catch (UnsupportedEncodingException | FileNotFoundException e) {
					return false;
				}
				return true;
			case CONSOLE_LOGGING:
				logger=plugin.getLogger();
				return true;
			
		}
		return false;
		
	}
	
	static Pattern spacePattern = Pattern.compile(" ");
	
	
	public static String niceFormatOfFileLogNativeMsg(String msg){
		return niceFormatOfFileLogNativeMsg(spacePattern.split(msg));
	}
	
	public static String niceFormatOfFileLogNativeMsg(String[] msgParts){
		Date time = new Date(Long.valueOf(msgParts[0]));
		String playerName = msgParts[1];
		String actionType = msgParts[2];
		String suffixMsg = "";
		String[] suffixMsgParts = Arrays.copyOfRange(msgParts, 2, msgParts.length);
		switch(actionType){
			case "opened":
			case "closed":
				suffixMsg = niceFormatOfFileLogNativeMsgOpenedClosed(suffixMsgParts);
				break;
			case "clicked":
				suffixMsg = niceFormatOfFileLogNativeMsgClicked(suffixMsgParts);
				//actionType = "   "+actionType;
				break;
			case "dropped":
				suffixMsg = niceFormatOfFileLogNativeMsgDropped(suffixMsgParts);
				//actionType = "   "+actionType;
				break;
			case "dragged":
				suffixMsg = niceFormatOfFileLogNativeMsgDragged(suffixMsgParts);
				//actionType = "   "+actionType;
				break;
				
		}
		
		return String.format("[%td.%<tm %<tT] %s %s", time, playerName, suffixMsg);
	}
	
	/**
	 * Gets a nice formated String out of the input opened/closed message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: world x y z chest|doublechest
	 * 
	 * Output: chest|doublechest at x y z in world
	 * 
	 * @param msg file logging format string
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgOpenedClosed(String msg){
		return niceFormatOfFileLogNativeMsgOpenedClosed(spacePattern.split(msg));
	}
	
	
	/**
	 * Gets a nice formated String out of the pre-splitted input opened/closed message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: {"world", "x", "y", "z", "chest|doublechest"}
	 * 
	 * Output: chest|doublechest at x y z in world
	 * 
	 * @param msgParts file logging format string parts
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgOpenedClosed(String[] msgParts){
		if(msgParts.length!=6) return ChestLogger.instance.debug?Arrays.toString(msgParts):"";
		Object[] msgPartsObj = Arrays.copyOf(msgParts, msgParts.length, Object[].class);
		return String.format("\n  %1$s %6$s"+
							 "\n  at %3$s %4$s %5$s in %2$s\n", msgPartsObj);
	}
	
	/**
	 * Gets a nice formated String out of the input clicked message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: clicked world x y z slotItemName slotAmount cursorItemName cursorAmount invHolderClassName slot type action
	 * 
	 * Output: clicked on itemName in invHolderClassName Inventory on slot slot using (type) type to perform action
	 * 
	 * @param msg file logging format string
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgClicked(String msg){
		return niceFormatOfFileLogNativeMsgClicked(spacePattern.split(msg));
	}
	
	
	/**
	 * Gets a nice formated String out of the pre-splitted input clicked message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: {"clicked", "world", "x", "y", "z", "slotItemName", "slotAmount", "cursorItemName", "cursorAmount", "invHolderClassName", "slot", "type", "action"}
	 * 
	 * Output: clicked on itemName in invHolderClassName Inventory on slot slot using (type) type to perform action
	 * 
	 * @param msgParts file logging format string parts
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgClicked(String[] msgParts){
		if(msgParts.length!=13) return ChestLogger.instance.debug?Arrays.toString(msgParts):"";
		Object[] msgPartsObj = Arrays.copyOfRange(msgParts, 9, msgParts.length, Object[].class);
		return String.format("\n    %s on %s"+
							 "\n    in %s Inventory"+
							 "\n    on slot %s"+
							 "\n    to perform %6$s\n", insertFront(msgPartsObj, msgParts[0], msgParts[5]));
	}
	
	/**
	 * Gets a nice formated String out of the input dropped message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: dropped world x y z itemName amount type action
	 * 
	 * Output: dropped amount itemName using type to perform action
	 * 
	 * @param msg file logging format string
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgDropped(String msg){
		return niceFormatOfFileLogNativeMsgDropped(spacePattern.split(msg));
	}
	
	
	/**
	 * Gets a nice formated String out of the pre-splitted input dropped message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: {"dropped", "world", "x", "y", "z", "itemName", "amount", "type", "action"}
	 * 
	 * Output: dropped amount itemName using type to perform action / dropped amount itemName*
	 * 
	 * @param msgParts file logging format string parts
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgDropped(String[] msgParts){
		if(msgParts.length!=8) return ChestLogger.instance.debug?Arrays.toString(msgParts):"";
		Object[] msgPartsObj = Arrays.copyOfRange(msgParts, 5, msgParts.length, Object[].class);
		return String.format("\n    %s %s %s\n", insertFront(msgParts[0], msgPartsObj));
	}
	
	/**
	 * Gets a nice formated String out of the input dragged message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: world x y z itemName amount topSlotsCount bottomSlotsCount type
	 * 
	 * Output: amount itemName onto topSlotsCount top slots and bottomSlotsCount bottom slots using drag (type) type
	 * 
	 * @param msg file logging format string
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgDragged(String msg){
		return niceFormatOfFileLogNativeMsgDragged(spacePattern.split(msg));
	}
	
	
	/**
	 * Gets a nice formated String out of the pre-splitted input dragged message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: {"world", "x", "y", "z", "itemName", "amount", "topSlotsCount", "bottomSlotsCount", "type"}
	 * 
	 * Output: amount itemName onto topSlotsCount top slots and bottomSlotsCount bottom slots using drag type type
	 * 
	 * @param msgParts file logging format string parts
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgDragged(String[] msgParts){
		if(msgParts.length!=10) return ChestLogger.instance.debug?Arrays.toString(msgParts):"";
		Object[] msgPartsObj = Arrays.copyOfRange(msgParts, 5, msgParts.length, Object[].class);
		return String.format("\n    %s %2$s %1$s"+
							 "\n    onto %3$s top slots"+
							 "\n    and %4$s bottom slots"+
							 "\n    using drag type %5$s\n", insertBack(msgPartsObj, msgParts[0]));
	}
	

	/**
	 * Gets a nice formated String out of the input dragged message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: collected world x y z itemName amount invHolder type action
	 * 
	 * deprecated
	 * Output: collected amount itemName from invHolder to cursor using type to perform action
	 * 
	 * @param msg file logging format string
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgCollected(String msg){
		return niceFormatOfFileLogNativeMsgCollected(spacePattern.split(msg));
	}
	
	
	/**
	 * Gets a nice formated String out of the pre-splitted input dragged message with
	 * the format defined in /info/formats.txt.
	 * 
	 * Input: {"collected", "world", "x", "y", "z", "itemName", "amount", "invHolder", "type", "action"}
	 * 
	 * deprecated
	 * Output: collected amount itemName from invHolder to cursor using type to perform action
	 * 
	 * @param msgParts file logging format string parts
	 * @return nicely formated String
	 */
	public static String niceFormatOfFileLogNativeMsgCollected(String[] msgParts){
		if(msgParts.length!=10) return ChestLogger.instance.debug?Arrays.toString(msgParts):"";
		return String.format("\n    %s %s %s"+
							 "\n    from %s"+
							 "\n    to cursor", msgParts[0], msgParts[6], msgParts[5], msgParts[7]);
	}
	
	
	
	@SuppressWarnings("unchecked")
	static <T> T[] insertFront(T insert, T[] array){
		Object[] newA = new Object[array.length+1];
		newA[0]=insert;
		System.arraycopy(array, 0, newA, 1, array.length);
		return (T[]) newA;
	}
	
	@SuppressWarnings("unchecked")
	static <T> T[] insertFront(T[] array, T... insert){
		Object[] newA = new Object[array.length+insert.length];
		for (int i = 0; i < insert.length; i++)
			newA[i]=insert[i];
		
		System.arraycopy(array, 0, newA, insert.length, array.length);
		return (T[]) newA;
	}

	@SuppressWarnings("unchecked")
	static <T> T[] insertBack(T[] array, T insert){
		Object[] newA = new Object[array.length+1];
		newA[array.length]=insert;
		System.arraycopy(array, 0, newA, 0, array.length);
		return (T[]) newA;
	}
}
