package com.bbsmart.pda.blackberry.bbphoto.util;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.models.GeneralOptions;
import com.bbsmart.pda.blackberry.bbphoto.models.ScreensaverOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.screens.SlideshowScreen;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.UiApplication;

public final class ScreensaverListener implements RealtimeClockListener {
	public static boolean screensaverRunning = false;
	private SlideshowScreen screensaver;
	
	public ScreensaverListener() {
		super();
	}
	
	public void clockUpdated() {
		int startDelay = ScreensaverOptions.getInstance().startDelay*60;
		int timeOut = startDelay + (ScreensaverOptions.getInstance().timeOut*3600);
		if(ScreensaverOptions.getInstance().enabled) {
			if(screensaverRunning) {
				if(DeviceInfo.getBatteryLevel() < ScreensaverOptions.getInstance().batteryStop || 
						DeviceInfo.getIdleTime() >= timeOut) {
					// Stop Screensaver
					screensaver.onClose();
					screensaver = null;
					screensaverRunning = false;
					Backlight.enable(false);
				} else {
					// Continue playing screensaver
					if(ScreensaverOptions.getInstance().backlight) {
						Backlight.enable(true, 255);
					}
				}
			} else {
				if(DeviceInfo.getIdleTime() > startDelay && DeviceInfo.getIdleTime() < timeOut) {
					Album a = AlbumList.getInstance().getAlbum(ScreensaverOptions.getInstance().useAlbum);
					if(a.isPrivate() && !GeneralOptions.getInstance().privacy_ShowHide) {
						a = AlbumList.getInstance().getUnsortedAlbum();
					}
					if(a.getSize() > 0) {
						// Start Screensaver
						screensaver = new SlideshowScreen(a, 0, 
											ScreensaverOptions.getInstance().slideDelay, 
											ScreensaverOptions.getInstance().transEffect, 
											ScreensaverOptions.getInstance().randSeq);
						BBSmartPhoto.instance.pushGlobalScreen(screensaver, 0, UiApplication.GLOBAL_SHOW_LOWER);
						if(ScreensaverOptions.getInstance().backlight) {
							Backlight.enable(true, 255);
						} else {
							Backlight.enable(true);
						}
						screensaverRunning = true;
					}
				} 
			}
		}
	}
}