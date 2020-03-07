package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.ScreensaverListener;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public final class SlideshowScreen extends MainScreen {
	private static final int SCREEN_WIDTH = Display.getWidth();
	private static final int SCREEN_HEIGHT = Display.getHeight();
	private static final int NUM_EFFECTS = 7;	// The total number of transition effects available 
												// including None and Random.
	private static final int MODE_SLIDESHOW = 0;
	private static final int MODE_SCREENSAVER = 1;
	private int mode;

	// TODO: Provide indication of pause
	private boolean paused = false;
	
	private Album album;
	private int currentIndex;
	private int slideDelay = 5;
	private int transEffect = 0;
	private boolean playRandom = false;
	private boolean showNotes = false;
	
	private Timer slideshow;
	private EncodedImage picture;
	private EncodedImage nextPicture;
	
	private Bitmap slide = new Bitmap(SCREEN_WIDTH, SCREEN_HEIGHT);
	private Bitmap nextSlide;
	private Graphics slideGraphics = new Graphics(slide);
	private BitmapField slideField = new BitmapField(slide);
	
	// Constructor for a screensaver
	public SlideshowScreen(Album a, int startIndex, int slideDelay, int transEffect, int playOrder) {
		mode = MODE_SCREENSAVER;
		
		album = a;
		currentIndex = startIndex;
		if(playOrder == 0) {
			playRandom = true;
		}
		
		picture = ImageFileUtil.getFileAsEncodedImage(album.getPicture(startIndex).getPath());
		picture = ImageFileUtil.resizeEI(picture, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		slideGraphics.drawImage((SCREEN_WIDTH/2)-(picture.getScaledWidth()/2), (SCREEN_HEIGHT/2)-(picture.getScaledHeight()/2), 
					SCREEN_WIDTH, SCREEN_HEIGHT, picture, 0, 0, 0);
		
		add(slideField);
		
		this.slideDelay = slideDelay;
		this.transEffect = transEffect;
		
		slideshow = new Timer();
		slideshow.schedule(new NextSlide(), (slideDelay)*1000, (slideDelay)*1000);
	}
	
	// Constructor for a slideshow
	public SlideshowScreen(Album a, int startIndex, int slideDelay, int transEffect, int playOrder, boolean showNotes) {
		this(a,startIndex, slideDelay, transEffect, playOrder);
		mode = MODE_SLIDESHOW;
		if(this.showNotes = showNotes) {
			String note = album.getPicture(currentIndex).getNote();
			if(note.equals("")) {
				setStatus(null);
			} else {
				setStatus(new LabelField(note));
			}
		}
	}
	
	private void resetSlideshow() {
		slideshow.cancel();
		slideshow = null;
		
		picture = ImageFileUtil.getFileAsEncodedImage(album.getPicture(currentIndex).getPath());
		picture = ImageFileUtil.resizeEI(picture, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		slideGraphics.clear();
		slideGraphics.drawImage((SCREEN_WIDTH/2)-(picture.getScaledWidth()/2), (SCREEN_HEIGHT/2)-(picture.getScaledHeight()/2), 
					SCREEN_WIDTH, SCREEN_HEIGHT, picture, 0, 0, 0);
		slideField.setBitmap(slide);
		
		if(showNotes) {
			String note = album.getPicture(currentIndex).getNote();
			if(note.equals("")) {
				setStatus(null);
			} else {
				setStatus(new LabelField(note));
			}
		}
		slideshow = new Timer();
		slideshow.schedule(new NextSlide(), (slideDelay)*1000, (slideDelay)*1000);
	}
	
	// Transform picture into nextPicture.  All pictures are set at Display Dimensions
	private void transitionEffect(int effect) {
		// Random Effect (select number between 2 and NUM_EFFECTS)
		if(effect == 1) {
			effect = (new Random().nextInt(NUM_EFFECTS-2))+2;
		}
		switch(effect) {
			case 2:
				wipeUp();
				break;
			case 3:
				wipeDown();
				break;
			case 4:
				horizontalBlind();
				break;
			case 5:
				verticalBlind();
				break;
			case 6:
				fade();
				break;
			default:
				// No effect
				synchronized(UiApplication.getEventLock()) {
					slideField.setBitmap(nextSlide);
				}
		}
	}
	
	private class NextSlide extends TimerTask {
		public void run() {
			if(!paused) {
				if(playRandom) {
					int randIndex = new Random().nextInt(album.getSize());
					while(currentIndex == randIndex && album.getSize() > 1) {
						randIndex = new Random().nextInt(album.getSize());
					}
					currentIndex = randIndex;
				} else {
					if(++currentIndex >= album.getSize()) { currentIndex = 0; }
				}
				nextPicture = ImageFileUtil.getFileAsEncodedImage(album.getPicture(currentIndex).getPath());
				nextPicture = ImageFileUtil.resizeEI(nextPicture, SCREEN_WIDTH, SCREEN_HEIGHT);
				nextSlide = new Bitmap(SCREEN_WIDTH, SCREEN_HEIGHT);
				Graphics g = new Graphics(nextSlide);
				g.drawImage((SCREEN_WIDTH/2)-(nextPicture.getScaledWidth()/2), (SCREEN_HEIGHT/2)-(nextPicture.getScaledHeight()/2), 
							SCREEN_WIDTH, SCREEN_HEIGHT, nextPicture, 0, 0, 0);
				transitionEffect(transEffect);
				synchronized (UiApplication.getEventLock()) {
					if(showNotes) {
						String note = album.getPicture(currentIndex).getNote();
						if(note.equals("")) {
							setStatus(null);
						} else {
							setStatus(new LabelField(note));
						}
					}
				}
			}
			if(mode == MODE_SLIDESHOW) {
				Backlight.enable(true, 255);
			}
		}
	}
	
	// *********************** TRANSITION EFFECTS ***********************
	private void wipeDown() {
		int lines = 5;
		int[] data = new int[SCREEN_WIDTH*lines];
		for(int i = 0; i < SCREEN_HEIGHT; i=i+lines) {
			nextSlide.getARGB(data, 0, nextSlide.getWidth(), 0, i, nextSlide.getWidth(), lines);
			slide.setARGB(data, 0, slide.getWidth(), 0, i, slide.getWidth(), lines);
			synchronized(UiApplication.getEventLock()) {
				slideField.setBitmap(slide);
			}
		}
	}
	
	private void wipeUp() {
		int lines = 5;
		int[] data = new int[SCREEN_WIDTH*lines];
		for(int i = SCREEN_HEIGHT-lines; i >= 0; i=i-lines) {
			nextSlide.getARGB(data, 0, nextSlide.getWidth(), 0, i, nextSlide.getWidth(), lines);
			slide.setARGB(data, 0, slide.getWidth(), 0, i, slide.getWidth(), lines);
			synchronized(UiApplication.getEventLock()) {
				slideField.setBitmap(slide);
			}
		}
	}
	
	private void horizontalBlind() {
		int lines = 2;
		int linesPerSection = 10;
		int sections = SCREEN_HEIGHT/linesPerSection;
		int[] data = new int[SCREEN_WIDTH*lines];
		for(int i = 0; i < linesPerSection; i=i+lines) {
			for(int j = 0; j < sections; j++) {
				nextSlide.getARGB(data, 0, nextSlide.getWidth(), 0, (j*linesPerSection)+i, nextSlide.getWidth(), lines);
				slide.setARGB(data, 0, slide.getWidth(), 0, (j*linesPerSection)+i, slide.getWidth(), lines);
			}
			synchronized(UiApplication.getEventLock()) {
				slideField.setBitmap(slide);
			}
		}
	}
	
	private void verticalBlind() {
		int lines = 2;
		int linesPerSection = 10;
		int sections = SCREEN_WIDTH/linesPerSection;
		int[] data = new int[SCREEN_HEIGHT*lines];
		for(int i = 0; i < linesPerSection; i=i+lines) {
			for(int j = 0; j < sections; j++) {
				nextSlide.getARGB(data, 0, lines, (j*linesPerSection)+i, 0, lines, nextSlide.getHeight());
				slide.setARGB(data, 0, lines, (j*linesPerSection)+i, 0, lines, slide.getHeight());
			}
			synchronized(UiApplication.getEventLock()) {
				slideField.setBitmap(slide);
			}
		}
	}
	
	private void fade() {
		for(int i = 5; i <= 255; i=i+10) {
			slideGraphics.setGlobalAlpha(i);
			slideGraphics.rop(Graphics.ROP_SRC_GLOBALALPHA, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, nextSlide, 0, 0);
			synchronized(UiApplication.getEventLock()) {
				slideField.setBitmap(slide);
			}
		}
	}
	
	// ********************* TRACKWHEEL HANDLER *********************
	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		if(mode == MODE_SCREENSAVER) {
			onClose();
			return true;
		}
		if(dx > 0 || dy > 0) {
			// Load Next Image
			if(++currentIndex >= album.getSize()) { currentIndex = 0; }
		} else {
			// Load Previous Image
			if(--currentIndex < 0) { currentIndex = album.getSize() - 1; }
		}
		resetSlideshow();
		return true;
	}
	
	// *********************** KEYSTROKE HANDLER *********************
	protected boolean keyDown(int keycode, int time) {
		switch(Keypad.key(keycode)) {
			case Keypad.KEY_END:
				onClose();
				break;
			default:
				return super.keyDown(keycode, time);
		}
		return true;
	}
	
	protected boolean keyChar(char c, int status, int time) {
		if(mode == MODE_SCREENSAVER) {
			onClose();
			return true;
		}
		switch(c) {
			case Keypad.KEY_SPACE:
				paused = !paused;
				break;
			case 'n':
				menuNext.run();
				break;
			case 'p': 
				menuPrevious.run();
				break;
			default:
				return super.keyChar(c, status, time);
		}
		return true;
	}
	
	// ************************* MENU ITEMS ***********************
	protected void makeMenu(Menu menu, int instance) {
		if(mode == MODE_SLIDESHOW) {
			if(paused) {
				menu.add(menuResume);
			} else {
				menu.add(menuPause);
			}
			menu.add(MenuItem.separator(0));
	
			if(UiUtil.DEVICE_SURETYPE) { menuPrevious.setText("Previous (O)"); 
										 menuNext.setText("Next (B)"); }
			menu.add(menuPrevious);
			menu.add(menuNext);
	        menu.add(MenuItem.separator(0));
	
		}
		menu.add(menuClose);
	}
	
	private MenuItem menuPause = new MenuItem("Pause", 0, 0) {
		public void run() {
			paused = true;
		}
	};
	
	private MenuItem menuResume = new MenuItem("Resume", 0, 0) {
		public void run() {
			paused = false;
		}
	};
	
	private MenuItem menuPrevious = new MenuItem("Previous (P)", 0, 0) {
		public void run() {
			if(--currentIndex < 0) { currentIndex = album.getSize()-1; }
			resetSlideshow();
		}
	};
	
	private MenuItem menuNext = new MenuItem("Next (N)", 0, 0) {
		public void run() {
			if(++currentIndex >= album.getSize()) { currentIndex = 0; }
			resetSlideshow();
		}
	};
	
	private MenuItem menuClose = new MenuItem("Close", 0, 0) {
		public void run() {
			onClose();
		}
	};
	
	public boolean onClose() {
		if(mode == MODE_SCREENSAVER) {
			ScreensaverListener.screensaverRunning = false;
		}
		slideshow.cancel();
		return super.onClose();
	}
}