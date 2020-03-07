package com.bbsmart.pda.blackberry.bbphoto.util;

import java.util.Vector;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.ui.screens.AlbumViewScreen;

import net.rim.device.api.io.file.FileSystemJournal;
import net.rim.device.api.io.file.FileSystemJournalEntry;
import net.rim.device.api.io.file.FileSystemJournalListener;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.Screen;

public final class ImageFileListener implements FileSystemJournalListener {    
    private long lastUSN;    
    private FileSystemJournalEntry entry;
    private Screen activeScreen;
    
    private Vector entryQueue = new Vector();
    
    public ImageFileListener() {
        super();
    }
    
    public void fileJournalChanged() {
        String mimeType;
        String path;
        long nextUSN = FileSystemJournal.getNextUSN();
        long loopUSN = nextUSN-1;

        while(loopUSN >= lastUSN) {
            entry = FileSystemJournal.getEntry(loopUSN);
        	if (entry != null) {
                path = URIUtil.decodeURI(entry.getPath().substring(1));
                mimeType = MIMETypeAssociations.getMIMEType(path);
                
                // Check if entry corresponds to an image file
                if(mimeType != null && mimeType.startsWith("image")) {
                	// TODO: Remove this check as it is only needed for the simulator
                	if(!ApplicationManager.getApplicationManager().inStartup()) {
                		activeScreen = BBSmartPhoto.instance.getActiveScreen();
                	}
                	String oldPath = URIUtil.decodeURI(entry.getOldPath().substring(1));
                    switch(entry.getEvent()) {
                        case FileSystemJournalEntry.FILE_ADDED:
                            // An image was copied OR
                            // A photo was taken OR
                            // An image was transfered from PC to Device Memory
                        	// NOTE: File does not contain the data yet so do nothing
                            break;
                        case FileSystemJournalEntry.FILE_CHANGED:
                            // An image was written to
                            // Occurs with most operations
                            imageChanged(path);
                            break;
                        case FileSystemJournalEntry.FILE_DELETED:
                            // An image was deleted
                            imageDeleted(path);
                            break;
                        case FileSystemJournalEntry.FILE_RENAMED:
                        	// An image was renamed OR
                        	// An image was moved
                        	imageRenamed(path, oldPath);
                            break;
                        default:
                            // The entry did not have a valid event type
                    }
                }   
            }
            loopUSN = loopUSN - 1;
        }
        lastUSN = nextUSN;
    }
    
    public void processQueuedImages() {
    	activeScreen = BBSmartPhoto.instance.getActiveScreen();
    	while(!entryQueue.isEmpty()) {
    		imageAdded(URIUtil.decodeURI(((FileSystemJournalEntry)entryQueue.firstElement()).getPath().substring(1)));
    		entryQueue.removeElementAt(0);
    	}
    }
    
    // **********************************************************************************
    // NOTE:  Each of the following methods require the String path to be in the form of
    //					<root>/<directory>/<filename>.<extension>
    //		  There should be NO leading / (eg. /SDCard/...)
    //		  All URI encoded characters must be decoded
    //					Hello%20World : must be converted to: Hello World
    //				Decoding available using URIUtil.decodeURI(<String with encoding>)
    // **********************************************************************************
    private void imageAdded(String path) {
    	if(activeScreen instanceof AlbumViewScreen) {
    		if(((AlbumViewScreen)activeScreen).viewInstance != null) {
    			((AlbumViewScreen)activeScreen).viewInstance.getAlbum().addPicture(path);
    		} else {
    			AlbumList.getInstance().getUnsortedAlbum().addPicture(path);
    		}
    	} else {
    		AlbumList.getInstance().getUnsortedAlbum().addPicture(path);
    	}
		AlbumViewScreen.dirty = true;
		AlbumViewScreen.dataValid = false;
    }
    
    private void imageChanged(String path) {
    	FileSystemJournalEntry prevEntry = FileSystemJournal.getEntry(entry.getUSN()-1);
    	if(prevEntry != null && 
    			prevEntry.getEvent() == FileSystemJournalEntry.FILE_ADDED && 
    				prevEntry.getPath().equals(entry.getPath())) {
    		if(AlbumViewScreen.inCamera) {
				entryQueue.addElement(entry);
			} else {
				imageAdded(path);
			}
    	}
    }
    
    private void imageRenamed(String path, String oldPath) {
    	AlbumList.getInstance().renamePicture(path, oldPath);
    	if(activeScreen instanceof AlbumViewScreen) {
    		if(((AlbumViewScreen)activeScreen).viewInstance != null) {
        		((AlbumViewScreen)activeScreen).viewInstance.update(path, oldPath);
    		}
    	}
    	AlbumViewScreen.dataValid = false;
    } 
    
    private void imageDeleted(String path) {
    	AlbumList.getInstance().deletePicture(path);
    	if(activeScreen instanceof AlbumViewScreen) {
    		AlbumViewScreen avs = (AlbumViewScreen)activeScreen;
    		if(avs.isVisible()) {
    			if(avs.viewInstance != null) {
        			avs.viewInstance.deletePicture(path);
        			if(avs.viewInstance.getAlbum().getSize() <= 0) {
        				avs.redrawScreen();
        			}
        		}
    		} else {
    			AlbumViewScreen.dirty = true;
    		}
    	}
    	AlbumViewScreen.dataValid = false;
    }
}
