package com.bbsmart.pda.blackberry.bbphoto.models;

import java.util.Hashtable;

import com.bbsmart.pda.blackberry.bbphoto.io.StoreUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;

/**
 * A picture and its properties
 */
public final class AlbumPicture {
	public static final int THUMB_WIDTH = 80;
	public static final int THUMB_HEIGHT = 60;
	
    private String file;                // Full path to image
    private int imageWidth;				// Width of the image
    private int imageHeight;			// Height of the image
    private int fileSize;				// Size of the file in KBs
    
    private Bitmap thumbnail;			// Thumbnail of the image as a byte[], 80x60
    
    private boolean isPrivate;          // Marks if the image is private or not (default false)
    private String note;                // A personal note about the image
    
    public AlbumPicture(String file) {
        this.file = file;
        if (!file.equals("")) {
        	createThumbnail();
        }
        isPrivate = false;
        note = "";    
    }
    
    private void createThumbnail() {
    	EncodedImage image = ImageFileUtil.getFileAsEncodedImage(file);
    	imageWidth = image.getWidth();
    	imageHeight = image.getHeight();
    	fileSize = (image.getData().length)/1024;
    	
    	image = ImageFileUtil.resizeEI(image, THUMB_WIDTH, THUMB_HEIGHT);
    	thumbnail = new Bitmap(THUMB_WIDTH, THUMB_HEIGHT);
    	Graphics g = new Graphics(thumbnail);
    	
    	g.drawImage((THUMB_WIDTH/2)-(image.getScaledWidth()/2), (THUMB_HEIGHT/2)-(image.getScaledHeight()/2), 
    			image.getScaledWidth(), image.getScaledHeight(), image, 0, 0, 0);
    	
    	int[] data = new int[THUMB_WIDTH * THUMB_HEIGHT];
    	thumbnail.getARGB(data, 0, THUMB_WIDTH, 0, 0, THUMB_WIDTH, THUMB_HEIGHT);
    	
    	// Save the thumbnail to a file
    	ImageFileUtil.saveThumbnail(file, data);
    }
    
    public int getFileSize() {
    	return fileSize;
    }
    
    public int getImageHeight() {
    	return imageHeight;
    }
    
    public int getImageWidth() {
    	return imageWidth;
    }
    
    public String getNote() {
        return note;
    }
    
    public String getPath() {
        return file;
    }
    
    public boolean inMemory() {
    	if(thumbnail == null) {
    		return false;
    	}
    	return true;
    }
    
    public boolean isPrivate() {
        return isPrivate;
    }
    
    public Bitmap getThumbnail() {
    	if(thumbnail == null) {
    		thumbnail = ImageFileUtil.loadThumbnail(file, THUMB_WIDTH, THUMB_HEIGHT);
    		if(thumbnail == null) {
    			createThumbnail();
    		}
    	}
    	return thumbnail;
    }
    
    // Rename the image rename the file in the file system.  This will trigger the listeners to update the internal data.
    public boolean setName(String name) {
    	return ImageFileUtil.renameImage(file, name + file.substring(file.lastIndexOf('.')));
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Update the full path to the image.  Deletes the thumbnail file as it cannot be moved (API limitation).
     * thumb.dat will be recreated next time it is needed.
     * @param path New full path to the image
     */
    public void setPath(String path) {
    	ImageFileUtil.deleteImage(file.substring(0, file.lastIndexOf('.')) + "-thumb.dat");
        file = path;
    }
    
    // status TRUE: Sets the isHidden property of the file to true if private images are hidden
    // status FALSE: Sets the isHidden property of the file to false
    public void setPrivate(boolean status) {
        isPrivate = status;
        if(!status) {
        	ImageFileUtil.hideImage(file, status);
        } else {
	        if(!GeneralOptions.getInstance().privacy_ShowHide) {
	        	ImageFileUtil.hideImage(file, status);
	        }
        }
    }

    /**
     * Returns the name of the image without path details or file extension
     */
    public String toString() {
    	return file.substring(file.lastIndexOf('/')+1, file.lastIndexOf('.'));
    }
    
    /**
     * Creates an object for saving in the persistent store
     * @return A representation of the object for persisting
     */
    public Object getPersistentObj() {
        Hashtable persistentObj = new Hashtable(9);

        persistentObj.put("file",       	file);
        persistentObj.put("imageHeight", 	new Integer(imageHeight));
        persistentObj.put("imageWidth", 	new Integer(imageWidth));
        persistentObj.put("fileSize", 		new Integer(fileSize));
        persistentObj.put("private",    	new Boolean(isPrivate));
        persistentObj.put("note",   		note);
        
        return persistentObj;
    }
    
    /**
     * Sets this objects values to the values in the given persisted object
     * @param persistentObj A persisted representation of the object
     */
    public void restorePersistentObj(Object persistentObj) {
        Hashtable persistentHash = (Hashtable)persistentObj;
        
        file =          (String)persistentHash.get("file");
        imageHeight = 	StoreUtil.unwrapInt(persistentHash, "imageHeight");
        imageWidth = 	StoreUtil.unwrapInt(persistentHash, "imageWidth");
        fileSize = 		StoreUtil.unwrapInt(persistentHash, "fileSize");
        isPrivate =     StoreUtil.unwrapBool(persistentHash, "private");
        note =          (String)persistentHash.get("note");     
    }
} 
