package com.bbsmart.pda.blackberry.bbphoto.util;

import javax.microedition.io.file.FileSystemListener;

import com.bbsmart.pda.blackberry.bbphoto.models.GeneralOptions;

public final class FileRootListener implements FileSystemListener {
	
	public FileRootListener() {
		super();
	}
	
	/** If a root of the device is changed then we need to rescan the memory to
	 *  determine if any changes to the image files has occured.  We need to 
	 *  perform a scan if either status calls are made to ensure all possible states
	 *  are covered.
	 *  
	 *  Reasons for this to be triggered:
	 *  	ROOT_ADDED:
	 *  		Device was disconnected from the PC after
     *   	 		Roxio media manager used	AND/OR
     *   			Mass Storage Mode used
     *   	ROOT_REMOVED:
     *    		While device is connected to the PC
     *   			Roxio is opened		AND/OR
     *   			Mass Storage Mode is entered
	 */
	public void rootChanged(int status, String rootName) {
        GeneralOptions.getInstance().app_DoScan = true;
	}
}