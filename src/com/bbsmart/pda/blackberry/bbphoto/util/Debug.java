package com.bbsmart.pda.blackberry.bbphoto.util;

import net.rim.device.api.system.EventLogger;

public final class Debug {
	// ID is hash of com.bbsmart.pda.blackberry.bbphoto
	private static final long ID = 0x8472dda923d89b7cL;
	private static long ticTime;
	private static String eventString = "";
	
	static {
		EventLogger.register(ID, "BBSmart Photo Pro", EventLogger.VIEWER_STRING);
	}
	
	public static void catEventString(String text) {
		eventString = eventString + text + ": " + (System.currentTimeMillis() - ticTime) + " ms\n";
	}
	
	public static void resetEventString() {
		eventString = "";
	}
	
	public static void flushEventString() {
		trace(eventString);
		eventString = "";
	}
	
	public static void tic() {
		ticTime = System.currentTimeMillis();
	}
	
	public static void toc(String text) {
		trace(text + ": " + (System.currentTimeMillis() - ticTime) + " ms");
	}
	
	public static void trace(String str) {
		EventLogger.logEvent(ID, str.getBytes());
	}
	
}