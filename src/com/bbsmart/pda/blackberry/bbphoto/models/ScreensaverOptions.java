package com.bbsmart.pda.blackberry.bbphoto.models;

import java.util.Hashtable;

import com.bbsmart.pda.blackberry.bbphoto.io.PersistenceManager;
import com.bbsmart.pda.blackberry.bbphoto.io.StoreUtil;

public final class ScreensaverOptions {
	private static final int STORE_INDX = 3;
	
	// Options
	public boolean enabled;
    public int slideDelay;
    public String useAlbum;
    public int randSeq;
    public int transEffect;
    public int startDelay;
    public int timeOut;
    public int batteryStop;
    public boolean backlight;
	
	// Singleton Accessor
    private static ScreensaverOptions instance;

    public static ScreensaverOptions getInstance() {
        if (instance == null) {
            instance = new ScreensaverOptions();
        }
        return instance;
    }
      
    private ScreensaverOptions() {   
        Object persistentObj = PersistenceManager.getStoreDataAtIndex(STORE_INDX);
        if (persistentObj == null) {
            // Currently not in the data store so create with defaults and save
            init();
            save();
        } else {
            restorePersistentObj(persistentObj);
        } 
    }
	
    private void init() {
    	enabled     = false;    	//Screensaver Off
    	slideDelay  = 5;        	//5 seconds
    	useAlbum    = AlbumList.UNSORTED_ALBUM_NAME;   //Album containing all images
    	randSeq 	= 0;			//Random
        transEffect = 1;        	//Random
        startDelay  = 5;       		//5 minutes
        timeOut     = 1;       		//1 hour
        batteryStop = 25;       	//25% battery charge left
        backlight   = true;     	//Backlight enabled
    }
	
    public void save() {
    	PersistenceManager.setStoreDataAtIndex(STORE_INDX, getPersistentObj());
    }
    
    /**
     * Creates a persistent object for saving in the persistent store
     * @return A persistent Object
     */
    public Object getPersistentObj() {
        Hashtable persistentObj = new Hashtable(9);
        
        persistentObj.put("enabled", 	new Boolean(enabled));
        persistentObj.put("slideDel", 	new Integer(slideDelay));
        persistentObj.put("album", 		useAlbum);
        persistentObj.put("randSeq", 	new Integer(randSeq));
        persistentObj.put("effect", 	new Integer(transEffect));
        persistentObj.put("startDel", 	new Integer(startDelay));
        persistentObj.put("timeOut", 	new Integer(timeOut));
        persistentObj.put("batStop", 	new Integer(batteryStop));
        persistentObj.put("backlight", 	new Boolean(backlight));
        
        return persistentObj;
    }
    
    /**
     * Sets this objects values to the values in the given persisted object
     * @param persistentObj A persisted representation of the object
     */
    public void restorePersistentObj(Object persistentObj) {
        Hashtable persistentHash = (Hashtable)persistentObj;
        
        enabled     = StoreUtil.unwrapBool(persistentHash, "enabled");
    	slideDelay  = StoreUtil.unwrapInt(persistentHash, "slideDel");
    	useAlbum    = (String)persistentHash.get("album");
    	randSeq		= StoreUtil.unwrapInt(persistentHash, "randSeq");
        transEffect = StoreUtil.unwrapInt(persistentHash, "effect");
        startDelay  = StoreUtil.unwrapInt(persistentHash, "startDel");
        timeOut     = StoreUtil.unwrapInt(persistentHash, "timeOut");
        batteryStop = StoreUtil.unwrapInt(persistentHash, "batStop");
        backlight   = StoreUtil.unwrapBool(persistentHash, "backlight");
    }
}