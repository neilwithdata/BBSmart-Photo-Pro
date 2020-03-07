package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.models.SlideshowOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.AlbumListField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoDialog;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoScreen;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class AlbumManagementScreen extends BBPhotoScreen {
	public static boolean dirty = false;
	private AlbumListField albumList;
	
	public AlbumManagementScreen() {
		setTitle(UiUtil.ALBUMS_TITLE);

		VerticalFieldManager vfm = new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL);
		
		albumList = new AlbumListField();
		vfm.add(albumList);
		add(vfm);
	}
	
	// ******************** TRACKWHEEL HANDLER *********************
	protected boolean navigationClick(int status, int time) {
		menuOpen.run();
		return true;
	}
	
	// ************************* KEYSTROKE HANDLER *******************
	protected boolean keyDown(int keycode, int time) {
		switch(Keypad.key(keycode)) {
			case Keypad.KEY_BACKSPACE:
				menuDelete.run();
				break;
			case Keypad.KEY_ENTER:
				menuOpen.run();
				break;
			default:
				return super.keyDown(keycode, time);
		}
		return true;
	}
	
	// ********************** MENU ITEMS **********************
	protected void makeMenu(Menu menu, int instance) {
		String selectedAlbum = (String)albumList.get(albumList, albumList.getSelectedIndex());
		selectedAlbum = selectedAlbum.substring(0, selectedAlbum.lastIndexOf(' '));
		
        menu.add(menuOpen);
        menu.add(menuNew);
        menu.add(menuEdit);
        menu.add(MenuItem.separator(0));
        
        if(AlbumList.getInstance().getSize() > 0) {
        	menu.add(menuDelete);
        	menu.add(MenuItem.separator(0));
        }
        
        if(AlbumList.getInstance().getAlbum(selectedAlbum).getSize() > 0) {
        	menu.add(menuSlideshow);
        }
        menu.add(menuOptions);
        menu.add(MenuItem.separator(0));
        
        menu.add(menuClose);
    }
	
	private MenuItem menuOpen = new MenuItem("Open Album", 0, 0) {
		public void run() {
			String displayAlbum = (String)albumList.get(albumList, albumList.getSelectedIndex());
			displayAlbum = displayAlbum.substring(0, displayAlbum.lastIndexOf(' '));
			
			BBSmartPhoto.instance.popScreen(BBSmartPhoto.instance.getActiveScreen());
			BBSmartPhoto.instance.popScreen(BBSmartPhoto.instance.getActiveScreen());
			BBSmartPhoto.instance.pushScreen(new AlbumViewScreen(displayAlbum));
		}
	};
	
	private MenuItem menuNew = new MenuItem("New Album", 0, 0) {
		public void run() {
			BBSmartPhoto.instance.pushScreen(new AlbumScreen());
		}
	};
	
	private MenuItem menuEdit = new MenuItem("Edit Album", 0 ,0) {
		public void run() {
			String selectedAlbum = (String)albumList.get(albumList, albumList.getSelectedIndex());
			selectedAlbum = selectedAlbum.substring(0, selectedAlbum.lastIndexOf(' '));
			
			BBSmartPhoto.instance.pushScreen(new AlbumScreen(selectedAlbum));
		}
	};
	
	private MenuItem menuDelete = new MenuItem("Delete Album", 0, 0) {
		public void run() {
			if(albumList.getSelectedIndex() > 0) {
				String selectedAlbum = (String)albumList.get(albumList, albumList.getSelectedIndex());
				selectedAlbum = selectedAlbum.substring(0, selectedAlbum.lastIndexOf(' '));
				
				if(BBPhotoDialog.askDelete(
						"All pictures will be moved to " + AlbumList.UNSORTED_ALBUM_NAME + ". Delete this Album?", 
						BBPhotoDialog.CANCEL, false) == BBPhotoDialog.DELETE) {
					AlbumList.getInstance().deleteAlbum(selectedAlbum);
					AlbumViewScreen.dirty = true;
					AlbumViewScreen.dataValid = false;
					albumList.update();
				}
			} else {
				// Unsorted Album is selected and cannot be deleted
				BBPhotoDialog.alert("Cannot delete " + AlbumList.UNSORTED_ALBUM_NAME + " Album");
			}
		}
	};
	
	private MenuItem menuSlideshow = new MenuItem("Slideshow", 0, 0) {
		public void run() {
			String selectedAlbum = (String)albumList.get(albumList, albumList.getSelectedIndex());
			selectedAlbum = selectedAlbum.substring(0, selectedAlbum.lastIndexOf(' '));
		
			BBSmartPhoto.instance.pushScreen(new SlideshowScreen(AlbumList.getInstance().getAlbum(selectedAlbum), 0, 
    				SlideshowOptions.getInstance().slideDelay, SlideshowOptions.getInstance().transEffect, 
    				SlideshowOptions.getInstance().randSeq, SlideshowOptions.getInstance().showNotes));
		}
	};
	
	private MenuItem menuOptions = new MenuItem("Options", 0, 0) {
		public void run() {
			BBSmartPhoto.instance.pushScreen(new OptionsScreen());
		}
	};
	
	private MenuItem menuClose = new MenuItem("Close", 0, 0) {
        public void run() {
            onClose();
        }
    };
    
    public void onExposed() {
    	if(dirty) {
    		albumList.update();
    		dirty = false;
    	}
    }

    public boolean onClose() {
    	close();
		return true;
    }
}