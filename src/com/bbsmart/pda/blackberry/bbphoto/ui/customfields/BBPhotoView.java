package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Vector;

import com.bbsmart.pda.blackberry.bbphoto.models.Album;

import net.rim.device.api.ui.Field;

// Views should initialise there variables in their constructors but leave any field generation
// for later.  The actual view generation should be contained in the run() method that should be performed
// on a new Thread.
// The isAlive() method should return the isAlive status of the process in run() and only return false
// once it is safe to call getAsField();
public interface BBPhotoView extends Runnable {
	
	public void deletePicture(String path);
	
	public Album getAlbum();
	
	public int getAlbumIndex();
	
	public String getFocusedPicture();
	
	public Vector getSelectedPictures();
	
	public Field getAsField();
	
	public String getNote();
	
	public boolean isAlive();
	
	public boolean isSelecting();
	
	public void moveFocusedPicture(int dx, int dy);
	
	public String trackImage();
	
	public void update(String path, String oldPath);
	
	public void updateBorder();
}