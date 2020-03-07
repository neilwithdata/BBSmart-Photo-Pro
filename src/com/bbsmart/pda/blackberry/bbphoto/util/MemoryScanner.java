package com.bbsmart.pda.blackberry.bbphoto.util;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.ui.screens.AlbumViewScreen;

import net.rim.device.api.io.MIMETypeAssociations;

/**
 * Scans the given directory and all sub-directories of the path passed to scanMemory.
 * To scan the entire device pass 'null' as the argument to scanMemory.
 * Performs a defined action for each image file defined as a MIME image type.
 */
public final class MemoryScanner extends Thread {
	private static Hashtable lookup;
	
	public void run() {
		scanMemory(null);
		validate();
		AlbumList.getInstance().save();
	}
	
	private static void generateLookup() {
		lookup = AlbumList.getInstance().getImageLookup();
	}
	
	private static void scanMemory(String root) {
		if(lookup == null) { generateLookup(); }
        FileConnection fc = null;
        Enumeration rootEnum = null;
        if (root != null) {
            // Open the file system and get the list of directories/files
            try {
                fc = (FileConnection)Connector.open("file:///" + root);
                rootEnum = fc.list();
            } catch (Exception ioex) {
            } finally {
                if (fc != null) {   // Everything is read, make sure to close the connection
                    try {
                        fc.close();
                        fc = null;
                    } catch (Exception ioex) {}
                }
            }
        }
        else { // There was no root to read, so now we are reading the system roots
            rootEnum = FileSystemRegistry.listRoots();
        }

        // Read through the found list of directories/files
        if (rootEnum != null) {
            while (rootEnum.hasMoreElements()) {
                String file = (String)rootEnum.nextElement();
                if (root != null && !root.startsWith("store/samples/")) { 
                	// Create full path name
                    file = root + file;
                }
                // Really bad hack since RIM can't get their naming right.  Only affects some devices
                if(file.equals("SDCard/blackberry/")) { file = "SDCard/BlackBerry/"; }
                
                String mimeType = MIMETypeAssociations.getMIMEType(file);
                if (mimeType != null) {
                	if(mimeType.startsWith("image")) {
	                    // Found an image file.  Check if it is registered
	            		if (lookup.containsKey(file)) {
	                        lookup.put(file, new Boolean(true));
	                    } else {
	                    	// Image is not registered in the application so add it
	                        AlbumList.getInstance().getUnsortedAlbum().addPicture(file);
	                        AlbumViewScreen.dirty = true;
	                        AlbumViewScreen.dataValid = false;
	                    }	
                	}
                } else {
                	scanMemory(file);
                }
            }
        }
    }
    
    private static void validate() {
    	Enumeration keys = lookup.keys();
    	while(keys.hasMoreElements()) {
    		String key = (String)keys.nextElement();
    		boolean marked = ((Boolean)lookup.get(key)).booleanValue();
    		if(!marked) {
    			AlbumList.getInstance().deletePicture(key);
    			AlbumViewScreen.dirty = true;
    			AlbumViewScreen.dataValid = false;
    		}
    	}
    	lookup = null;
    }
} 
