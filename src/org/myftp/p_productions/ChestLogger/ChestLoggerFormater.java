package org.myftp.p_productions.ChestLogger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ChestLoggerFormater extends Formatter{

	//private final String format = "[%1$tF][%1$tT] %2$s: %3$s%4$s%n";
	//private final String format = "[%1$tF | %1$tT | %2$s] %3$s%4$s%n";
	private final String format = "[%1$tT] %3$s%4$s%n";
	private final Date dat = new Date();
	
	@Override
	public synchronized String format(LogRecord record) {
		dat.setTime(record.getMillis());
        
		String message = formatMessage(record);
		String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
		
		return String.format(format,
				dat,
				getShortName(record.getLevel()),
				message,
				throwable);
		
		
	}

	private String getShortName(Level level){
		
		if(level==Level.CONFIG) return "CONF";
		else if(level==Level.INFO) return "INFO";
		else if(level==Level.SEVERE) return "SEVR";
		else if(level==Level.WARNING) return "WARN";
		else if(level==Level.FINEST) return "FINS";
		else if(level==Level.FINER) return "FINR";
		else return level.getName();
		
	}
	
}
