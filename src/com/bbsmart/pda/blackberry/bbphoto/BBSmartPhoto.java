package com.bbsmart.pda.blackberry.bbphoto;

import com.bbsmart.pda.blackberry.bbphoto.ui.screens.AlbumViewScreen;
import com.bbsmart.pda.blackberry.bbphoto.util.FileRootListener;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileListener;
import com.bbsmart.pda.blackberry.bbphoto.util.ScreensaverListener;

import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

import com.bbsmart.pda.blackberry.bbphoto.io.TrialManager;
import com.bbsmart.pda.blackberry.bbphoto.ui.screens.TrialEndedScreen;
import com.bbsmart.pda.blackberry.bbphoto.ui.screens.RegisterScreen;

public final class BBSmartPhoto extends UiApplication {
	public static UiApplication instance;
	public static ImageFileListener imageFileListener = new ImageFileListener();
	public static FileRootListener fileRootListener = new FileRootListener();
	private TrialManager tMan;

	private BBSmartPhoto() {
		tMan = TrialManager.getInstance();
		addFileSystemJournalListener(imageFileListener);
		addFileSystemListener(fileRootListener);
		addRealtimeClockListener(new ScreensaverListener());
		pushScreen(new AlbumViewScreen());
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0 && args[0].equals("gui")) {
			// Code to initialise the app
			// Check to see if the app exists, if it does, foreground it...
			RuntimeStore appReg = RuntimeStore.getRuntimeStore();
			if (appReg.get(AppInfo.APP_KEY) != null) {
				instance = (UiApplication) appReg.waitFor(AppInfo.APP_KEY);
			}
			
			if(!instance.isAlive()) {
				// There has been an unrecoverable error.  Bootstrap the app
				appReg.remove(AppInfo.APP_KEY);
				instance = null;
				appReg.put(AppInfo.APP_KEY, new BBSmartPhoto());
				instance = (UiApplication) appReg.waitFor(AppInfo.APP_KEY);
				fileRootListener.rootChanged(FileRootListener.ROOT_ADDED, "/SDCard"); //Use this to trigger a memory scan
				instance.enterEventDispatcher();
			}
			
			instance.requestForeground();
		} else {
			// Code to launch the background thread
			// Register the alternate on first run
			RuntimeStore appReg = RuntimeStore.getRuntimeStore();
			if (appReg.get(AppInfo.APP_KEY) == null) {
				appReg.put(AppInfo.APP_KEY, new BBSmartPhoto());
			}
			instance = (UiApplication) appReg.waitFor(AppInfo.APP_KEY);
			// HomeScreen.setRolloverIcon(Bitmap.getBitmapResource("img/icon-roll1.png"),
			// 0);
			instance.enterEventDispatcher();
		}
	}

	public void activate() {
		Screen activeScreen = instance.getActiveScreen();

		switch (tMan.state) {
		case TrialManager.STATE_TRIAL:
			// Remember the first time the application is run
			if (tMan.isFirstRun()) {
				tMan.setFirstTimeRun();
				tMan.save();
			}

			if (tMan.isTrialExpired()) {
				tMan.state = TrialManager.STATE_TRIAL_EX;
				tMan.save();

				pushScreen(new TrialEndedScreen());
				break;
			}

			// intentional fall-through
		case TrialManager.STATE_REG:
			if (activeScreen instanceof AlbumViewScreen) {
				((AlbumViewScreen) activeScreen).doScan();
			}
			break;
		case TrialManager.STATE_TRIAL_EX:
			pushScreen(new TrialEndedScreen());
			break;
		case TrialManager.STATE_FULL:
			pushScreen(new RegisterScreen(true));
			break;
		}
	}
}
