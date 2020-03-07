package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import javax.microedition.io.HttpConnection;

import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;

import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public final class ImageScreen extends MainScreen implements RenderingApplication {
	private ImageScreen instance;
	private RenderingSession renderingSession;   
	private HttpConnection  currentConnection;
	
	private Album album;
	private int currentIndex;
	
	public ImageScreen(Album a, int startIndex) {
		album = a;
		currentIndex = startIndex;
        renderingSession = RenderingSession.getNewInstance();
        
        instance = this;
        
        ImageFetchThread thread = new ImageFetchThread(album.getPicture(startIndex).getPath(), instance);
        thread.start(); 
	}
	
	public void processConnection(HttpConnection connection, Event e) {
        // cancel previous request
        if (currentConnection != null) {
            try {
                currentConnection.close();
            } catch (Exception ioe) {
            }
        }
        currentConnection = connection;
        BrowserContent browserContent = null;
        try {
            browserContent = renderingSession.getBrowserContent(connection, instance, e);
            if (browserContent != null) {
                Field field = browserContent.getDisplayableContent();
                if (field != null) {
                    synchronized (Application.getEventLock()) {
                        deleteAll();
                        add(field);
                    }
                }
                browserContent.finishLoading();
            }
        } catch (Exception re) {}
    }
	
	class ImageFetchThread extends Thread {
	    private ImageScreen parent;
	    private String url;
	    
	    ImageFetchThread(String url, ImageScreen parent) {
	        this.url = url;
	        this.parent = parent;
	    }

	    public void run() {
	        HttpConnection connection = ImageFileUtil.makeConnection(url);
	        parent.processConnection(connection, null);        
	    }
	}
	
	// ********************** TRACKWHEEL HANDLER **************************
	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		if(!super.navigationMovement(dx, dy, status, time)) {
			if(dx > 0 || dy > 0) {
				// Load Next Image
				if(++currentIndex >= album.getSize()) { currentIndex = 0; }
			} else {
				// Load Previous Image
				if(--currentIndex < 0) { currentIndex = album.getSize() - 1; }
			}
			ImageFetchThread thread = new ImageFetchThread(album.getPicture(currentIndex).getPath(), instance);
			thread.start();
		}
		return true;
	}
	
	// ******************** KEYSTROKE HANDLER ****************
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
	
	// ************************* MENU ITEMS ****************************
	protected void makeMenu(Menu menu, int instance) {
		menu.add(menuNext);
		menu.add(menuPrevious);
		menu.add(MenuItem.separator(0));
		
		super.makeMenu(menu, instance);
	}
	
	private MenuItem menuNext = new MenuItem("Next", 0, 0) {
		public void run() {
			if(++currentIndex >= album.getSize()) { currentIndex = 0; }
			ImageFetchThread thread = new ImageFetchThread(album.getPicture(currentIndex).getPath(), instance);
			thread.start();
		}
	};
	
	private MenuItem menuPrevious = new MenuItem("Previous", 0, 0) {
		public void run() {
			if(--currentIndex < 0) { currentIndex = album.getSize() - 1; }
			ImageFetchThread thread = new ImageFetchThread(album.getPicture(currentIndex).getPath(), instance);
			thread.start();
		}
	};
	
	// ************************* INTERFACE REQUIRED METHODS *******************************
	public Object eventOccurred(Event event) {
        // No event support
        return null;
    }
	
	public int getAvailableHeight(BrowserContent browserField) {
        // Field has full screen
        return Display.getHeight();
    }

    public int getAvailableWidth(BrowserContent browserField) {
        // Field has full screen
        return Display.getWidth();
    }
	
    public int getHistoryPosition(BrowserContent browserField) {
        // No history support
        return 0;
    }
    
    public String getHTTPCookie(String url) {
        // No cookie support
        return null;
    }
	
    public HttpConnection getResource( RequestedResource resource, BrowserContent referrer) {
        // No resource support
        return null;
    }
    
    public void invokeRunnable(Runnable runnable) {
        (new Thread(runnable)).run();
    }
}