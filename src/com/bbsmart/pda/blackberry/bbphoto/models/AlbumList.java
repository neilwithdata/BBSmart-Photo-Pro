package com.bbsmart.pda.blackberry.bbphoto.models;

import java.util.Hashtable;
import java.util.Vector;

import com.bbsmart.pda.blackberry.bbphoto.io.PersistenceManager;
import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;

/**
 * An AlbumList contains a collection of Album objects
 */
public final class AlbumList {
	private static final int STORE_INDX = 1;

	public static String UNSORTED_ALBUM_NAME = "Unsorted";
    
    private Album unsorted;
    private Vector albums;
    
    // Singleton Accessor
    private static AlbumList instance;

    public static AlbumList getInstance() {
        if (instance == null) {
            instance = new AlbumList();
        }
        return instance;
    }
    
    private AlbumList() {
        Object persistentObj = PersistenceManager.getStoreDataAtIndex(STORE_INDX);
        if (persistentObj == null) {
            // No data in the store so create with defaults and save  
            init();
            save();
        } else {
            restorePersistentObj(persistentObj);
        }
    }
    
    private void init() {
        unsorted = new Album(UNSORTED_ALBUM_NAME);
        albums = new Vector();
    }
    
    public void save() {
        PersistenceManager.setStoreDataAtIndex(STORE_INDX, getPersistentObj());
    }
    
    // Privacy Show: Deletes all the images from an album
    // Privacy Hide: Deletes ONLY all shown images from an album (Hidden private images remain)
    public void clearAlbum(String album) {
    	Album a = getAlbum(album);
    	AlbumPicture p;
    	for(int i = a.getSize() - 1; i >= 0; i--) {
    		p = a.getPicture(i);
			ImageFileUtil.deleteImage(p.getPath());
    	}
    }
    
    // Privacy Show: Returns true if an Album is currently named the given String name
    // Privacy Hide: SAME
    public boolean containsAlbum(String name) {
    	if(name.equals(UNSORTED_ALBUM_NAME)) { return true; }
    	for(int i = 0; i < albums.size(); i++) {
    		if(name.equals(((Album)albums.elementAt(i)).getName())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    // Privacy Show: Searches for the album and deletes it if found. All images in the album are moved to the 'Unsorted' album
    // Privacy Hide: SAME. This will move hidden images to the Unsorted album
    public void deleteAlbum(String album) {
    	Album a = getAlbum(album);
    	if(a != null) {
	    	for(int i = 0; i < a.getRealSize(); i++) {
	    		unsorted.addPicture(a.getRealPicture(i));
	    	}
	    	albums.removeElement(a);
    	}
    }
    
    // Privacy Show: Searches for the image and deletes it if found
    // Privacy Hide: SAME
    public void deletePicture(String path) {
    	if(!unsorted.deletePicture(path)) {
    		int i = albums.size() - 1;
    		while(i >= 0 && !((Album)albums.elementAt(i)).deletePicture(path)) {
    			i = i - 1;
    		}
    	}
    }
    
    // Privacy Show: Returns the album corresponding to the given name if it exists otherwise null
    // Privacy Hide: SAME
    public Album getAlbum(String name) {
    	if(name.equals(UNSORTED_ALBUM_NAME)) {
    		return unsorted;
    	} else {
    		for(int i = albums.size()-1; i >= 0; i--) {
    			if(((Album)albums.elementAt(i)).getName().equals(name)) {
    				return (Album)albums.elementAt(i);
    			}
    		}
    	}
    	return null;
    }
    
    // Privacy Show: Returns a fast lookup table of images
    // Privacy Hide: SAME
    public Hashtable getImageLookup() {
    	Hashtable imageLookup = new Hashtable();
    	imageLookup = unsorted.getImageLookup(imageLookup);
    	for(int i = albums.size()-1; i >= 0; i--) {
			imageLookup = ((Album)albums.elementAt(i)).getImageLookup(imageLookup);
		}
    	return imageLookup;
    }
    
    // Privacy Show: Returns the number of albums excluding the Unsorted album
    // Privacy Hide: Returns ONLY the number of public albums excluding the Unsorted album
    public int getSize() {
    	if(GeneralOptions.getInstance().privacy_ShowHide) {
    		return albums.size();
    	}
    	int size = 0;
    	for(int i = 0; i < albums.size(); i++) {
    		if(!((Album)albums.elementAt(i)).isPrivate()) {
    			size = size+1;
    		}
    	}
    	return size;
    }
    
    public Album getUnsortedAlbum() {
        return unsorted;
    }

    // Sets the isHidden of all pictures set as private to status
    // Sets the isHidden of all pictures in private albums to status
    public void hidePictures(boolean status) {
    	unsorted.hidePictures(status);
    	Album a;
    	for(int i = 0; i < albums.size(); i++) {
    		a = (Album)albums.elementAt(i);
    		if(a.isPrivate()) {
    			a.hideAll(status);
    		} else {
    			a.hidePictures(status);
    		}
    	}
    }
    
    // Privacy Show: Returns an array of the names of all albums including the Unsorted album
    // Privacy Hide: Returns an array of the names of ONLY public albums including the Unsorted album
    public String[] listAlbums() {
        String albumNames[] = new String[getSize()+1];
        
        albumNames[0] = unsorted.getName();
        Album a;
        int j = 1;
        for(int i = 0; i < albums.size(); i++) {
        	a = (Album)albums.elementAt(i);
        	if(!(!GeneralOptions.getInstance().privacy_ShowHide && a.isPrivate())) {
        		albumNames[j++] = a.getName();
        	}
        }
        return albumNames;
    }
    
    // Privacy Show: Returns an array of the names and number of images of all albums including the Unsorted album
    // Privacy Hide: Returns an array of the names and number of images of ONLY public albums including the Unsorted album
    public String[] listAlbumsWithDetails() {
        String albumNames[] = new String[getSize()+1];
        
        albumNames[0] = unsorted.toString();
        Album a;
        int j = 1;
        for(int i = 0; i < albums.size(); i++) {
        	a = (Album)albums.elementAt(i);
        	if(!(!GeneralOptions.getInstance().privacy_ShowHide && a.isPrivate())) {
        		albumNames[j++] = a.toString();
        	}
        }
        return albumNames;
    }
    
    // Privacy Show: Returns true if the album was created false otherwise
    // Privacy Hide: SAME. Names of hidden albums will be considered
    public boolean newAlbum(String name) {
    	if(containsAlbum(name)) { return false; }
    	
        if(!albums.isEmpty()) {
			int index = 0;
			while(index < albums.size()) {
				String text = ((Album)albums.elementAt(index)).getName();
				if(name.toLowerCase().compareTo(text.toLowerCase()) < 0) {
					albums.insertElementAt(new Album(name), index);
					return true;
				}
				index = index + 1;
			}
		}
		albums.addElement(new Album(name));
        return true;
    }
    
    // Privacy Show: Search for the image and rename it if found
    // Privacy Hide: SAME
    public void renamePicture(String path, String oldPath) {
    	if(!unsorted.renamePicture(path, oldPath)) {
    		int i = albums.size() - 1;
    		while(i >= 0 && !((Album)albums.elementAt(i)).renamePicture(path, oldPath)) {
    			i = i - 1;
    		}
    	}
    }
       
    /**
     * Creates an object for saving in the persistent store
     * @return A representation of the object for persisting
     */
    public Object getPersistentObj() {
        Vector persistentObj = new Vector(albums.size()+1);
        
        persistentObj.addElement(unsorted.getPersistentObj());
        
        Album a;
        int size = albums.size();
        for(int i = 0; i < size; i++) {
            a = (Album)albums.elementAt(i);
            persistentObj.addElement(a.getPersistentObj());
        }       
        return persistentObj;
    }
    
    /**
     * Sets this objects values to the values in the given persisted object
     * @param persistentObj A persisted representation of the object
     */
    public void restorePersistentObj(Object persistentObj) {
        Vector persistentVect = (Vector)persistentObj;
        albums = new Vector(persistentVect.size()-1);

        unsorted = new Album("");
        unsorted.restorePersistentObj(persistentVect.elementAt(0));
        UNSORTED_ALBUM_NAME = unsorted.getName();
        
        Album a;
        int size = persistentVect.size();
        for(int i = 1; i < size; i++) {
            a = new Album("");
            a.restorePersistentObj(persistentVect.elementAt(i));
            albums.addElement(a);
        }
    }
} 
