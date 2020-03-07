package com.bbsmart.pda.blackberry.bbphoto.models;

import java.util.Hashtable;
import java.util.Vector;

import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;

// FIXME: Update the virtual album only when needed if performance has been compromised
/**
 * An Album contains a collection of AlbumPicture objects
 */
public final class Album {
	private static int MEMORY_THRESHOLD = 5;		// The threshold for determining if too many picture thumbnails are
													// not in memory.  
    private String name;
    private boolean isPrivate;
    
    private Vector pictures;
    private int[] virtualAlbum;
    
    public Album(String name) {
        this.name = name;    
        isPrivate = false;
        pictures = new Vector();
    }
    
    // Creates a virtual album providing a mapping of shown image indices to a continuous indexable array
    private void createVirtualAlbum() {
    	virtualAlbum = new int[getSize()];
    	int index = 0;
    	for(int i = 0; i < pictures.size(); i++) {
    		if(!(!GeneralOptions.getInstance().privacy_ShowHide && ((AlbumPicture)pictures.elementAt(i)).isPrivate())) {
    			virtualAlbum[index++] = i;
    		}
    	}
    }
    
    // Privacy Show: Creates a new AlbumPicture object in the album from the given path
    // Privacy Hide: SAME
    public void addPicture(String path) {
    	try {
    		pictures.insertElementAt(new AlbumPicture(path), 0);
    	} catch(Exception e) {} // Handle exceptions quietly and continue
    }
    
    // Privacy Show: Adds the AlbumPicture object to the Album
    // Privacy Hide: SAME
    public void addPicture(AlbumPicture p) {
    	pictures.insertElementAt(p, 0);
    }
    
    // Privacy Show: Returns true if the image is in the album
    // Privacy Hide: SAME
    public boolean containsPicture(String path) {
    	for(int i = pictures.size() - 1; i >= 0; i--) {
    		if (((AlbumPicture)pictures.elementAt(i)).getPath().equals(path)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    // Privacy Show: Returns true if the image was deleted from the album
    // Privacy Hide: SAME
    public boolean deletePicture(String path) {
    	for(int i = pictures.size() - 1; i >= 0; i--) {
    		if (((AlbumPicture)pictures.elementAt(i)).getPath().equals(path)) {
    			pictures.removeElementAt(i);
    			// Delete thumbnail file as well
    			ImageFileUtil.deleteImage(path.substring(0, path.lastIndexOf('.')) + "-thumb.dat");
    			return true;
    		}
    	}
    	return false;
    }
    
    // Privacy Show: Returns the number of KBs taken up by all images in the album
    // Privacy Hide: Returns the number of KBs taken up ONLY by shown images in the album
    public int getFileSize() {
    	int fileSize = 0;
    	AlbumPicture p;
    	for(int i = 0; i < pictures.size(); i++) {
    		p = (AlbumPicture)pictures.elementAt(i);
    		if(!(!GeneralOptions.getInstance().privacy_ShowHide && p.isPrivate())) {
    			fileSize = fileSize + p.getFileSize();
    		}
    	}
    	return fileSize;
    }
    
    // Privacy Show: Returns a fast lookup table of images
    // Privacy Hide: SAME
    public Hashtable getImageLookup(Hashtable imageLookup) {
    	AlbumPicture p;
    	for(int i = pictures.size() - 1; i >= 0; i--) {
    		p = (AlbumPicture)pictures.elementAt(i);
    		imageLookup.put(p.getPath(), new Boolean(false));
    	}
    	return imageLookup;
    }
    
    // Privacy Show: If path is not found return index of the first picture
    // Privacy Hide: If path is not found return index of the first showable picture. Returns -1 if none found
    public int getIndexOfPicture(String path) {
    	for(int i = getSize() - 1; i >= 0; i--) {
    		if (getPicture(i).getPath().equals(path)) {
    			return i;
    		}
    	}
    	if(getSize() > 0) {
    		return 0;
    	}
    	return -1;
    }
    
    public String getName() {
        return name;
    }
    
    // Privacy Show: Returns false if the number of images in the album whose thumbnail is not in memory is > MEMORY_THRESHOLD
    // Privacy Hide: Returns false percentage of shown images in the album whose thumbnail is not in memory is > MEMORY_THRESHOLD
    public boolean inMemory() {
    	int notInMemory = 0;
    	for(int i = getSize()-1; i >= 0; i--) {
    		if(!getPicture(i).inMemory()) { notInMemory++; }
    	}
    	if(notInMemory > MEMORY_THRESHOLD) {
    		return false;
    	}
    	return true;
    }
    
    // Privacy Show: Returns the AlbumPicture at the index provided
    // Privacy Hide: Returns the AlbumPicture from the virtual album at the index provided
    public AlbumPicture getPicture(int index) {
    	createVirtualAlbum();
    	return (AlbumPicture)pictures.elementAt(virtualAlbum[index]);
    }
    
    // Privacy Show: Returns the picture specified by the given path
    // Privacy Hide: SAME
    public AlbumPicture getPicture(String path) {
    	AlbumPicture p;
    	for(int i = pictures.size() - 1; i >= 0; i--) {
    		p = (AlbumPicture)pictures.elementAt(i);
    		if(path.equals(p.getPath())) {
    			return p;
    		}
    	}
    	return null;
    }
    
    public boolean isPrivate() {
        return isPrivate;
    }
    
    public AlbumPicture getRealPicture(int index) {
    	return (AlbumPicture)pictures.elementAt(index);
    }
    
    public int getRealSize() {
    	return pictures.size();
    }
    
    // Privacy Show: Returns the number of pictures
    // Privacy Hide: Returns ONLY the number of shown pictures
    public int getSize() {
    	if(GeneralOptions.getInstance().privacy_ShowHide) {
    		return pictures.size();
    	}
    	int size = 0;
    	for(int i = 0; i < pictures.size(); i++) {
    		if(!((AlbumPicture)pictures.elementAt(i)).isPrivate()) {
    			size = size+1;
    		}
    	}
    	return size;
    }
    
    public void hideAll(boolean status) {
    	for(int i = 0; i < pictures.size(); i++) {
    		AlbumPicture p = (AlbumPicture)pictures.elementAt(i);
			ImageFileUtil.hideImage(p.getPath(), status);
    	}
    }
    
    public void hidePictures(boolean status) {
    	for(int i = 0; i < pictures.size(); i++) {
    		AlbumPicture p = (AlbumPicture)pictures.elementAt(i);
    		if(p.isPrivate()) {
    			ImageFileUtil.hideImage(p.getPath(), status);
    		}
    	}
    }
    
    // Privacy Show: Returns true if the image was in the album and the path was updated
    // Privacy Hide: SAME
    public boolean renamePicture(String path, String oldPath) {
    	for(int i = pictures.size() - 1; i >= 0; i--) {
    		if (((AlbumPicture)pictures.elementAt(i)).getPath().equals(oldPath)) {
    			((AlbumPicture)pictures.elementAt(i)).setPath(path);
    			return true;
    		}
    	}
    	return false;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPrivate(boolean status) {
        isPrivate = status;
        if(!status) {
        	hideAll(status);
        } else {
	        if(!GeneralOptions.getInstance().privacy_ShowHide) {
	        	hideAll(status);
	        }
        }
    }

    // Privacy Show: Swaps the position of the picture at index1 with the picture at index2. Indices are those of shown images
    // Privacy Hide: SAME
    public void swapPictures(int index1, int index2) {
    	int realIndex1 = virtualAlbum[index1];
    	int realIndex2 = virtualAlbum[index2];
    	
    	AlbumPicture temp = (AlbumPicture)pictures.elementAt(realIndex2);
    	pictures.setElementAt(pictures.elementAt(realIndex1), realIndex2);
    	pictures.setElementAt(temp, realIndex1);
    }
    
    // Privacy Show: Returns the name of the album, the number of pictures in it and a privacy indicator. eg: AlbumName (#)p
    // Privacy Hide: Returns the name of the album, ONLY the number of shown pictures in it and a privacy indicator.
    public String toString() {
    	String str = name + " (" + getSize() + ")";
    	if(isPrivate) {
    		str = str + "p";
    	}
    	return str;
    }
    
    /**
     * Creates an object for saving in the persistent store
     * @return A representation of the object for persisting
     */
    public Object getPersistentObj() {
        Vector persistentObj = new Vector(pictures.size()+2);

        persistentObj.addElement(name);
        persistentObj.addElement(new Boolean(isPrivate));
        
        AlbumPicture p;
        int size = pictures.size();
        for(int i = 0; i < size; i++) {
            p = (AlbumPicture)pictures.elementAt(i);
            persistentObj.addElement(p.getPersistentObj());
        }  
        
        return persistentObj;
    }
    
    /**
     * Sets this objects values to the values in the given persisted object
     * @param persistentObj A persisted representation of the object
     */
    public void restorePersistentObj(Object persistentObj) {
        Vector persistentVect = (Vector)persistentObj;
        pictures = new Vector(persistentVect.size()-2);

        name = (String)persistentVect.elementAt(0);
        isPrivate = ((Boolean)persistentVect.elementAt(1)).booleanValue();
        
        AlbumPicture p;
        int size = persistentVect.size();
        for(int i = 2; i < size; i++) {
            p = new AlbumPicture("");
            p.restorePersistentObj(persistentVect.elementAt(i));
            pictures.addElement(p);
        } 
    }
} 
