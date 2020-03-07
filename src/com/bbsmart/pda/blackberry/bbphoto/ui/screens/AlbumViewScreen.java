package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import java.util.Vector;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.io.TrialManager;
import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;
import com.bbsmart.pda.blackberry.bbphoto.models.GeneralOptions;
import com.bbsmart.pda.blackberry.bbphoto.models.SlideshowOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoChoiceField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoDialog;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoScreen;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoView;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.DetailView;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.ListView;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.MemoryPopup;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoLabelField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.StyledButtonField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.ThumbnailView;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.ViewChoices;

import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.Multipart;
import net.rim.blackberry.api.mail.SupportedAttachmentPart;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class AlbumViewScreen extends BBPhotoScreen {
	public static boolean inCamera = false;
	public static boolean isMovingImage = false;
	public static boolean dirty = false;
	public static boolean themeValid = true;
	public static boolean dataValid = false;
	
	private BBPhotoChoiceField viewSelect;
    private BBPhotoChoiceField albumSelect;
	private BBPhotoLabelField imageTracker;
    private VerticalFieldManager viewManager;
    private HorizontalFieldManager photoButton;
    
    public BBPhotoView viewInstance;
    private Field currentView;

    public AlbumViewScreen() {
    	super(BBPhotoScreen.NO_VERTICAL_SCROLL);
    	FieldChangeListener albumViewListener = new FieldChangeListener() {
    		public void fieldChanged(Field field, int context) {
    			if(context == PROGRAMMATIC) {
    				redrawScreen();
    			} else {
    				dirty = true;
    			}
    		}
    	};
    	viewSelect = new BBPhotoChoiceField(ViewChoices.getViewOfIndex(0), ViewChoices.getViewChoices(), 
    											albumViewListener, 1, UiUtil.getViewSelect());
    	albumSelect = new BBPhotoChoiceField(AlbumList.UNSORTED_ALBUM_NAME, AlbumList.getInstance().listAlbums(), 
												albumViewListener, 2, UiUtil.getAlbumSelect());
        imageTracker = new BBPhotoLabelField("", UiUtil.getImageTracker(), BBPhotoLabelField.HCENTER);
        imageTracker.setFont(getFont().derive(Font.PLAIN, getFont().getHeight()-2));
        viewManager = new VerticalFieldManager(VerticalFieldManager.VERTICAL_SCROLL);
        
        photoButton = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER | HorizontalFieldManager.USE_ALL_HEIGHT);
        photoButton.add(new StyledButtonField("Take Photo", StyledButtonField.SIZE_LARGE, 0, getFont().derive(Font.BOLD)) {
        	public boolean trackwheelClick(int status, int time) {
        		menuTakePhoto.run();
        		return true;
        	}
        });
        
        add(viewSelect);
        add(albumSelect);
        add(imageTracker);
        add(viewManager);
        dirty = true;
    }
    
    public AlbumViewScreen(String album) {
    	this();
    	reloadAlbumSelect();
    	albumSelect.setSelectedIndex(album);
    	viewSelect.setSelectedIndex(GeneralOptions.getInstance().app_ViewSelection);
    	redrawScreen();
    }
    
    public void doScan() {
        if(GeneralOptions.getInstance().app_DoScan) {
			BBSmartPhoto.instance.pushScreen(new LoadingScreen());
        	GeneralOptions.getInstance().app_DoScan = false;
        }
    }
    
    // Reload the contents of the selected view with the selected album
    public void redrawScreen() {
		showAlbum(AlbumList.getInstance().getAlbum(albumSelect.getChoice()));
        dirty = false;
    }
    
    // Checks if the currently selected album is still valid.  If not select Unsorted
    public void reloadAlbumSelect() {
    	String choice = albumSelect.getChoice();
    	albumSelect.setChoices(AlbumList.getInstance().listAlbums());
    	albumSelect.setSelectedIndex(choice);
    }
    
    private void showAlbum(Album a) {
		// Clear the view. Delete the photo button if its present
    	viewManager.deleteAll();
		if(photoButton.getIndex() > 0) { delete(photoButton); }
		// Create the new view instance
		switch(viewSelect.getSelectedIndex()) {
    		case 0:		// Thumbnails
    			viewInstance = new ThumbnailView(a);
    			break;
    		case 1:		// Details
    			viewInstance = new DetailView(a);
    			break;
    		case 2:		// List
    			viewInstance = new ListView(a);
    			break;
			default:
				// Not a supported view
				viewInstance = null;
    	}
    	// Add the view to the screen or Add photo button if album is empty
    	if(a.getSize() > 0) {
    		currentView = viewInstance.getAsField();
    		if(a.inMemory()) {
    			// Instant load - Block and add all at once for fast loading
    			viewInstance.run();
    			while(viewInstance.isAlive()) {}
    			viewManager.add(currentView);
    		} else {
    			// Incremental load - Generate the view off the event thread
    			viewManager.add(currentView);
    			new Thread(viewInstance).start();
    		}
    	} else {
			add(photoButton);
		}
    	imageTracker.setText("");
    }
    
    private void setMovingImage(boolean value) {
    	isMovingImage = value;
    	viewInstance.updateBorder();
    }
    
    // ********************** TRACKWHEEL HANDLER *******************
    // If the selected picture is not found then the first picture in the album will be shown
	protected boolean navigationClick(int status, int time) {
		if(isMovingImage) {
			setMovingImage(false);
			return true;
		} else {
			if(getFieldWithFocus().equals(viewManager) && !viewInstance.isSelecting()) {
				BBSmartPhoto.instance.pushScreen(
						new ImageScreen(viewInstance.getAlbum(), viewInstance.getAlbumIndex()));
				return true;
			}
		}
		return super.navigationClick(status, time);
	}
    
	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		boolean retVal = true;
		if(isMovingImage) {
			viewInstance.moveFocusedPicture(dx, dy);
		} else {
			retVal = super.navigationMovement(dx, dy, status, time);
			if(getFieldWithFocus().equals(viewManager)) {
				imageTracker.setText(viewInstance.trackImage());
				String note = viewInstance.getNote();
				if(note == null || note.equals("")) {
					setStatus(null);
				} else {
					setStatus(new BBPhotoLabelField(note, UiUtil.getStatusBar()));
				}
			} else {
				imageTracker.setText("");
				setStatus(null);
			}
		}
		return retVal;
	}
	
	// ********************* KEYSTROKE HANDLER ********************	
	protected boolean keyDown(int keycode, int time) {
		if(Keypad.key(keycode) == Keypad.KEY_SPACE && !viewInstance.isSelecting() &&
				!(viewInstance instanceof ListView) && getFieldWithFocus().equals(viewManager)) {
			setMovingImage(!isMovingImage);
			return true;
		}
		if(isMovingImage) {return true; }
		switch(Keypad.key(keycode)) {
			case Keypad.KEY_BACKSPACE:
				menuDelete.run();
				break;
			case Keypad.KEY_ENTER:
				if(getFieldWithFocus().equals(viewManager) && !viewInstance.isSelecting()) {
					BBSmartPhoto.instance.pushScreen(
							new ImageScreen(viewInstance.getAlbum(), viewInstance.getAlbumIndex()));
					break;
				}
			default:
				return super.keyDown(keycode, time);
		}
		return true;
	}
	
	protected boolean keyChar(char c, int status, int time) {
		if(viewInstance.isSelecting()) { return true; }
		switch(c) {
			case 'a':
				albumSelect.displayOptions();
				break;
			case 'v':
				viewSelect.displayOptions();
				break;
			case 'c':
				if(UiUtil.DEVICE_SURETYPE) {
					viewSelect.displayOptions();
					break;
				}
			case 'p':
				if(getFieldWithFocus().equals(viewManager)) {
					menuProperties.run();
					break;
				}
			case 'o':
				if(UiUtil.DEVICE_SURETYPE && getFieldWithFocus().equals(viewManager)) {
					menuProperties.run();
					break;
				}
			default:
				return super.keyChar(c, status, time);
		}
		return true;
	}
	
    // ********************** MENU ITEMS *************************
    protected void makeMenu(Menu menu, int instance) {
    	if(getFieldWithFocus().equals(viewManager)) {
    		menu.add(menuMoveToAlbum);
    		if(!viewInstance.isSelecting()) {
    			if(!(viewInstance instanceof ListView)) {
    				menu.add(menuMoveImage);
    			}
    			if(UiUtil.DEVICE_SURETYPE) { menuProperties.setText("Properties (O)"); }
    			menu.add(menuProperties);
    		}
    	}
        menu.add(menuManageAlbums);
        menu.add(MenuItem.separator(0));
        
        if(!viewInstance.isSelecting()) {
        	if(viewInstance.getAlbum().getSize() > 0) {
        		menu.add(menuSlideshow);
        	}
        	menu.add(menuTakePhoto);
        	menu.add(MenuItem.separator(0));
        }
        
        if(getFieldWithFocus().equals(viewManager)) {
	        menu.add(menuDelete);
        }
        
        if(!viewInstance.isSelecting()) {
        	if(viewInstance.getAlbum().getSize() > 0) {
        		menu.add(menuDeleteAll);
        	}
        }
    	menu.add(MenuItem.separator(0));
    	
    	if(!viewInstance.isSelecting()) {
    		if(getFieldWithFocus().equals(viewManager)) {
        		menu.add(menuEmail);
        		menu.add(MenuItem.separator(0));
        	}
        }
        
        menu.add(menuOptions);
        menu.add(menuMemory);
        
        // If not registered display buy/register option in the menu
		if (TrialManager.getInstance().state != TrialManager.STATE_REG) {
			menu.add(new MenuItem("Buy/Register", 0, 0) {
				public void run() {
					BBSmartPhoto.instance.pushScreen(new RegisterScreen(false));
				}
			});
		}
        
        menu.add(menuAbout);
        menu.add(MenuItem.separator(0));
        
        menu.add(menuMinimize);
        menu.add(menuForceClose);
    }
    
    private MenuItem menuMoveToAlbum = new MenuItem("Move to Album", 0, 0) {
        public void run() {
            Object[] obj = (Object [])AlbumList.getInstance().listAlbums();
            int i = BBPhotoDialog.ask("Select Album", obj);
            if(i >= 0 && i != albumSelect.getSelectedIndex()) {
            	Vector pictures = viewInstance.getSelectedPictures();

            	Album a = viewInstance.getAlbum();
            	Album newAlbum = AlbumList.getInstance().getAlbum((String)obj[i]);
            	for(int j = pictures.size()-1; j >= 0; j--) {
            		String picture = (String)pictures.elementAt(j);

            		AlbumPicture p = a.getPicture(picture);
    	            a.deletePicture(picture);
    	            newAlbum.addPicture(p);
    	            
    	            dataValid = false;
    	            viewInstance.deletePicture(picture);
            	}
	            if(viewInstance.getAlbum().getSize() <= 0) {
	            	redrawScreen();
	            } else {
	            	imageTracker.setText(viewInstance.trackImage());
	            }
            }
        }
    };
    
    private MenuItem menuMoveImage = new MenuItem("Move Image", 0, 0) {
        public void run() {
        	setMovingImage(true);
        }
    };
    
    private MenuItem  menuProperties = new MenuItem("Properties (P)", 0, 0) {
        public void run() {
    		BBSmartPhoto.instance.pushScreen(
    				new ImagePropertiesScreen(
    						albumSelect.getChoice(), viewInstance.getFocusedPicture()));
        }
    };
    
    private MenuItem menuManageAlbums = new MenuItem("Manage Albums", 0, 0) {
    	public void run() {
    		GeneralOptions.getInstance().app_ViewSelection = viewSelect.getSelectedIndex();
    		BBSmartPhoto.instance.pushScreen(new AlbumManagementScreen());
    	}
    };
    
    private MenuItem menuSlideshow = new MenuItem("Slideshow", 0, 0) {
        public void run() {
            BBSmartPhoto.instance.pushScreen(
            		new SlideshowScreen(viewInstance.getAlbum(), viewInstance.getAlbumIndex(), 
            				SlideshowOptions.getInstance().slideDelay, SlideshowOptions.getInstance().transEffect, 
            				SlideshowOptions.getInstance().randSeq, SlideshowOptions.getInstance().showNotes));
        }
    };
    
    private MenuItem menuTakePhoto = new MenuItem("Take Photo", 0, 0) {
    	public void run() {
    		if(DeviceInfo.hasCamera()) {
    			inCamera = true;
    			Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA, new CameraArguments());
    		}
    	}
    };
    
    private MenuItem menuDelete = new MenuItem("Delete", 0, 0) {
        public void run() {
        	if(GeneralOptions.getInstance().app_PromptIMGDelete) {
        		if (BBPhotoDialog.askDelete("Delete Picture?", 
        				BBPhotoDialog.CANCEL, true) == BBPhotoDialog.DELETE) {
        			doDelete();
        			GeneralOptions.getInstance().app_PromptIMGDelete = !BBPhotoDialog.getDontAskAgainValue();
        		}
        	} else {
        		doDelete();
        	}
        }
        
        private void doDelete() {
        	Vector pictures = viewInstance.getSelectedPictures();
        	for(int i = 0; i < pictures.size(); i++) {
        		ImageFileUtil.deleteImage((String)pictures.elementAt(i));
        	}
        	imageTracker.setText("");
        }
    };
    
    private MenuItem menuDeleteAll = new MenuItem("Delete All", 0, 0) {
        public void run() {
            if(BBPhotoDialog.askDelete("Delete All Pictures in this Album?", 
            		BBPhotoDialog.CANCEL, false) == BBPhotoDialog.DELETE) {
            	AlbumList.getInstance().clearAlbum(albumSelect.getChoice());
            	dataValid = false;
            }
        }
    };
    
    private MenuItem menuEmail = new MenuItem("Send by Email", 0, 0) {
        public void run() {
        	String file = viewInstance.getFocusedPicture();
            Message msg = new Message();
            Multipart multipart = new Multipart(); // Default type of multipart/mixed.
            SupportedAttachmentPart attach = new SupportedAttachmentPart(
            										multipart, MIMETypeAssociations.getMIMEType(file), 
            										file.substring(file.lastIndexOf('/')+1), 
            										ImageFileUtil.getImageBytes(file));
            multipart.addBodyPart(attach);
            try {
            	msg.setContent(multipart);
            } catch(Exception msgex) {}
            Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, new MessageArguments(msg));
        }
    };
    
    private MenuItem menuOptions = new MenuItem("Options", 0, 0) {
        public void run() {
            BBSmartPhoto.instance.pushScreen(new OptionsScreen());
        }
    };
    
    private MenuItem menuMemory = new MenuItem("Memory Status", 0, 0) {
    	public void run() {
    		BBSmartPhoto.instance.pushScreen(new MemoryPopup());
    	}
    };
    
    private MenuItem menuAbout = new MenuItem("About", 0, 0) {
    	public void run() {
    		BBSmartPhoto.instance.pushScreen(new AboutScreen());
    	}
    };
    
    private MenuItem menuMinimize = new MenuItem("Minimize", 0, 0) {
        public void run() {
            onClose();
        }
    };
    
    private MenuItem menuForceClose = new MenuItem("Force Exit", 0, 0) {
    	public void run() {
    		AlbumList.getInstance().save();
    		GeneralOptions.getInstance().save();
    		System.exit(0);
    	}
    };
    
    public void onExposed() {
    	if(inCamera) {
    		BBSmartPhoto.imageFileListener.processQueuedImages();
    		inCamera = false;
    	}
    	if(!themeValid) {
    		viewSelect.updateTheme();
    		albumSelect.updateTheme();
    		imageTracker.updateTheme();
    		themeValid = true;
    	}
    	if(dirty) {
    		reloadAlbumSelect();
    		redrawScreen();
    	}
    }
    
    public boolean onClose() {
    	if(!dataValid) {
    		AlbumList.getInstance().save();
    		GeneralOptions.getInstance().save();
    		dataValid = true;
    	}
        BBSmartPhoto.instance.requestBackground();
        return true;
    }
} 
