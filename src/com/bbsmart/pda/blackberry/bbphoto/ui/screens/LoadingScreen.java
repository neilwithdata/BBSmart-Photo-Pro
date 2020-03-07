package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.util.MemoryScanner;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FullScreen;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;

public class LoadingScreen extends FullScreen {
	private MediaManager manager;
	private MediaPlayer player;
	
	private Timer scanPollTimer;
	private MemoryScanner scan;
	
	private boolean started = false;

	public LoadingScreen() {
		super();
		setScreenFont();
		
		add(new BitmapField(UiUtil.BBSMART_LOGO, BitmapField.FIELD_HCENTER));

		VerticalFieldManager vfm = new VerticalFieldManager(VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.USE_ALL_WIDTH) {
			protected void paint(Graphics g) {
				g.drawBitmap(0, 0, getWidth(), getHeight(), UiUtil.LOADING, 0, 0);
				super.paint(g);
			}
		};
		vfm.add(new LabelField("Scanning for new images..."));
		vfm.add(new LabelField("(This may take several minutes)"));

		manager = new MediaManager();
        player = new MediaPlayer();
        
        try {
            Object media= manager.createMedia("cod://BBSmartPhotoPro/ajax-loader.pme");
            player.setMedia(media);
        } catch (IOException ioex) {
        } catch (MediaException mex) {
        }
        HorizontalFieldManager hfm = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER);
        hfm.add((Field)player.getUI());
        vfm.add(hfm);
        add(vfm);
        try {
            player.start();
        } catch (MediaException mex) {
        }
        scan = new MemoryScanner();
        scanPollTimer = new Timer();
	}

	// ************************** KEYSTROKE HANDLER ************************
	protected boolean keyDown(int keycode, int time) {
		// Consume all events
		return true;
	}
	
	private void setScreenFont() {
		Font f;
		try {
			f = FontFamily.forName("BBMillbankTall").getFont(Font.BOLD, 17);
			f = f.derive(f.getStyle(), f.getHeight(), Ui.UNITS_px,
					Font.ANTIALIAS_STANDARD, 0);
		} catch (ClassNotFoundException cnfe) {
			f = getFont().derive(Font.BOLD, 17, Ui.UNITS_px,
					Font.ANTIALIAS_STANDARD, 0);
		}
		setFont(f);
	}
	
	protected void onDisplay(){
		scanPollTimer.schedule(new TimerTask() {
				public void run() {
					Backlight.enable(true);
					if(!started){
						scan.start();
						started = true;
					} else {
						if(!scan.isAlive()) {
							onClose();
						} 
					}
				}}, 0, 500);
	}

	public boolean onClose() {
		scanPollTimer.cancel();
		player.close();
		manager.dispose();
		synchronized(BBSmartPhoto.getEventLock()) {
			close();
		}
		return true;
	}
}