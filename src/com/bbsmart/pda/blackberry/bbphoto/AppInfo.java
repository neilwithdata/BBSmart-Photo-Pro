package com.bbsmart.pda.blackberry.bbphoto;

import com.bbsmart.pda.blackberry.bbphoto.io.TrialManager;

/**
 * Container class for application meta information
 */
public final class AppInfo {
	// Determines the start state of the application
	public static final int APP_START_STATE = TrialManager.STATE_FULL;

	// Duration of the trial version
	public static final int TRIAL_DURATION_DAYS = 15;

	public static final String VERSION_STRING = "1.0";

	// key is hash of string "com.bbsmart.pda.blackberry.bbphoto"
	public static final long APP_KEY = 0x8472dda923d89b7cL;

	// key is hash of string "com.bbsmart.pda.blackberry.bbphoto.private"
	public static final long APP_PRIVATE_KEY = 0x534cfe644bc8703cL;
}
